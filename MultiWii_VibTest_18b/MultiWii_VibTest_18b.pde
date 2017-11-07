/*
MultiWiiCopter by Alexandre Dubus
www.multiwii.com
June  2011     V1.dev
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 any later version. see <http://www.gnu.org/licenses/>
*/

#include "config.h"
#include "def.h"
#define   VERSION  18

/*********** RC alias *****************/
#define ROLL       0
#define PITCH      1
#define YAW        2
#define THROTTLE   3
#define AUX1       4
#define AUX2       5
#define CAMPITCH   6
#define CAMROLL    7

#define PIDALT     3
#define PIDVEL     4
#define PIDLEVEL   5
#define PIDMAG     6

#define BOXACC      0
#define BOXBARO     1
#define BOXMAG      2
#define BOXCAMSTAB  3
#define BOXCAMTRIG  4
#define BOXARM      5


static uint32_t currentTime = 0;
static uint32_t previousTime = 0;
static uint16_t cycleTime = 0;     // this is the number in micro second to achieve a full loop, it can differ a little and is taken into account in the PID loop
static uint16_t calibratingA = 0;  // the calibration is done is the main loop. Calibrating decreases at each cycle down to 0, then we enter in a normal mode.
static uint8_t  calibratingM = 0;
static uint16_t calibratingG;
static uint8_t armed = 0;
static int16_t acc_1G;             // this is the 1G measured acceleration
static uint8_t nunchuk = 0;
static uint8_t accMode = 0;        // if level mode is a activated
static uint8_t magMode = 0;        // if compass heading hold is a activated
static uint8_t baroMode = 0;       // if altitude hold is activated
static int16_t gyroADC[3],accADC[3],magADC[3];
static int16_t accSmooth[3];       // projection of smoothed and normalized gravitation force vector on x/y/z axis, as measured by accelerometer

// change in Vibtest:
static int16_t accNoise[3];        // The deviation of the actual signal from the smoothed values
static int16_t gyroNoise[3];       // Same for gyro data
static uint8_t setupMode = 0;      // Changed to 1, if THROTTLE > MAXCHECK upon startup
static int8_t activeMotor = 0; // Added for VibTest, CW
static uint16_t rawThrottle;
static uint16_t launchTime, initTime;


static int16_t heading,magHold;
static uint8_t calibratedACC = 0;
static uint8_t vbat;               // battery voltage in 0.1V steps
static uint8_t okToArm = 0;
static uint8_t rcOptions;

static uint8_t baroNewData = 0;
static int32_t pressure = 0;
static float BaroAlt = 0.0f;
static float EstVelocity = 0.0f;
static float EstAlt = 0.0f;

//for log
static uint16_t cycleTimeMax = 0;       // highest ever cycle timen
static uint16_t cycleTimeMin = 65535;   // lowest ever cycle timen
static uint16_t powerMax = 0;           // highest ever current
static uint16_t powerAvg = 0;           // last known current


// ******************
// rc functions
// ******************
#define MINCHECK 1100
#define MAXCHECK 1900

volatile int16_t failsafeCnt = 0;

static int16_t rcData[8];    // interval [1000;2000]
static int16_t rcCommand[4]; // interval [1000;2000] for THROTTLE and [-500;+500] for ROLL/PITCH/YAW 

static uint8_t rcRate8;
static uint8_t rcExpo8;
static int16_t lookupRX[7]; //  lookup table for expo & RC rate

// **************
// gyro+acc IMU
// **************
static int16_t gyroData[3] = {0,0,0};
static int16_t gyroZero[3] = {0,0,0};
static int16_t accZero[3]  = {0,0,0};
static int16_t magZero[3]  = {0,0,0};
static int16_t angle[2]    = {0,0};  // absolute angle inclination in multiple of 0.1 degree

// *************************
// motor and servo functions
// *************************
static int16_t axisPID[3];
static int16_t motor[8];
static int16_t servo[4] = {1500,1500,1500,1500};

// **********************
// EEPROM & LCD functions
// **********************
static uint8_t P8[7], I8[7], D8[7]; //8 bits is much faster and the code is much shorter
static uint8_t dynP8[3], dynI8[3], dynD8[3];
static uint8_t rollPitchRate;
static uint8_t yawRate;
static uint8_t dynThrPID;
static uint8_t activate[6];

void blinkLED(uint8_t num, uint8_t wait,uint8_t repeat) {
  uint8_t i,r;
  for (r=0;r<repeat;r++) {
    for(i=0;i<num;i++) {
      LEDPIN_SWITCH //switch LEDPIN state
      BUZZERPIN_ON delay(wait); BUZZERPIN_OFF
    }
    delay(60);
  }
}

void annexCode() { //this code is excetuted at each loop and won't interfere with control loop if it lasts less than 650 microseconds
  static uint32_t serialTime = 0;
  static uint32_t buzzerTime = 0;
  static uint32_t calibratedAccTime;
  static uint8_t  buzzerState = 0;
  static uint32_t vbatRaw = 0;       //used for smoothing voltage reading
  static uint8_t buzzerFreq;         //delay between buzzer ring
  uint8_t axis;
  uint8_t prop1,prop2;

  for(axis=0;axis<2;axis++) {
    //PITCH & ROLL dynamic PID adjustemnt, depending on stick deviation
    prop1 = 100-min(abs(rcData[axis]-1500)/5,100)*rollPitchRate/100;
    dynP8[axis] = P8[axis]*prop1/100*prop2/100;
    dynD8[axis] = D8[axis]*prop1/100*prop2/100;
  }
  
  //YAW dynamic PID adjustemnt
  prop1 = 100-min(abs(rcData[YAW]-1500)/5,100)*yawRate/100;
  dynP8[YAW] = P8[YAW]*prop1/100;
  dynD8[YAW] = D8[YAW]*prop1/100;

  if ( ( (calibratingA>0 && (ACC || nunchuk) ) || (calibratingG>0) ) ) {  // Calibration phasis
    LEDPIN_SWITCH
  } else {
    if (calibratedACC == 1) LEDPIN_OFF
    if (armed) LEDPIN_ON
  }

  if ( micros() > calibratedAccTime + 500000 ) {
    if (abs(angle[ROLL])>150 || abs(angle[PITCH])>150) { //more than 15 deg detection
      calibratedACC = 0; //the multi uses ACC and is not calibrated or is too much inclinated
      LEDPIN_SWITCH
      calibratedAccTime = micros();
    } else
      calibratedACC = 1;
  }
  if (micros() > serialTime + 20000) { // 50Hz
    serialCom();
    serialTime = micros();
  }
  for(axis=0;axis<2;axis++) {
    uint16_t tmp = abs(rcData[axis]-MIDRC);
    uint16_t tmp2 = tmp/100;
    rcCommand[axis] = lookupRX[tmp2] + (tmp-tmp2*100) * (lookupRX[tmp2+1]-lookupRX[tmp2]) / 100;
    if (rcData[axis]<MIDRC) rcCommand[axis] = -rcCommand[axis];
  }
  rcCommand[THROTTLE] = MINTHROTTLE + (int32_t)(MAXTHROTTLE-MINTHROTTLE)* (rcData[THROTTLE]-MINCHECK)/(2000-MINCHECK);
  rcCommand[YAW]      = rcData[YAW]-MIDRC;
}


void setup() {
  launchTime = micros();
  Serial.begin(SERIAL_COM_SPEED);
  LEDPIN_PINMODE
  POWERPIN_PINMODE
  BUZZERPIN_PINMODE
  STABLEPIN_PINMODE
  POWERPIN_OFF  
  configureReceiver(); // Change ChW : moveod to first place of inits, so we can get a throttle value
  readEEPROM();
  checkFirstTime();

  delay(200);          // Change CHW : get first RC values, TEST here, which valie is required !!!!!!!!!!!
  rawThrottle = readRawRC(THROTTLE);
  if (rawThrottle > MAXCHECK) { // Change CHW : check for THROTTLE max and use according initOutput()
    initOutputMax();
    setupMode = 1;
  }
  else {
    initOutput();
    setupMode = 0;
  }
  initTime = micros();
  
  initSensors();
  previousTime = micros();
  #if defined(GIMBAL) || defined(FLYING_WING)
   calibratingA = 400;
  #endif
  calibratingG = 400;
}

// ******** Main Loop *********
void loop () {
  static uint8_t rcDelayCommand; // this indicates the number of time (multiple of RC measurement at 50Hz) the sticks must be maintained to run or switch off motors
  uint8_t axis,i;
  int16_t error,errorAngle;
  int16_t delta;
  int16_t PTerm,ITerm,DTerm;
  static int16_t lastGyro[3] = {0,0,0};
  static int16_t delta1[3],delta2[3];
  static int16_t errorGyroI[3] = {0,0,0};
  static int16_t errorAngleI[2] = {0,0};
  static uint8_t camCycle = 0;
  static uint8_t camState = 0;
  static uint32_t camTime = 0;
  static uint32_t rcTime  = 0;
  static int16_t initialThrottleHold;
  static int32_t errorAltitudeI = 0;
  static int16_t AltPID = 0;
  static int32_t VelErrorI = 0;
  static int16_t lastVelError = 0;
  static int16_t lastAltError = 0;
  static float AltHold = 0.0;

  if (currentTime > (rcTime + 20000) ) { // 50Hz
    rcTime = currentTime; 
    computeRC();

// Here is the section where the RC commands trigger activities of the software

    if(setupMode == 0){ // Vibration Monitor Mode
      if (rcData[THROTTLE] < MINCHECK) {
        errorGyroI[YAW] = 0; errorGyroI[PITCH] = 0; errorGyroI[YAW] = 0;
        errorAngleI[YAW] = 0; errorAngleI[PITCH] = 0;
        rcDelayCommand++; // rcDelayCommand = 20 => 20x20ms = 0.4s = time to wait for a specific RC command to be acknowledged
        if (rcData[YAW] > MAXCHECK) {
          if (rcDelayCommand == 20) 
            increaseActiveMotor();
        } else if (rcData[YAW] < MINCHECK) {
          if (rcDelayCommand == 20) 
            decreaseActiveMotor();
        } else
          rcDelayCommand = 0;
      }
    } else {           // ESC Setup Mode
      if (rcData[THROTTLE] < MINCHECK) {
          writeAllMotors(1000);
      } else if (rcData[THROTTLE] > MAXCHECK) {
          writeAllMotors(2000);
      } else {
          writeAllMotors(rcData[THROTTLE]);
      }
    }

// End of section where the RC commands trigger activities of the software

    rcOptions = (rcData[AUX1]<1300)   + (1300<rcData[AUX1] && rcData[AUX1]<1700)*2  + (rcData[AUX1]>1700)*4
               +(rcData[AUX2]<1300)*8 + (1300<rcData[AUX2] && rcData[AUX2]<1700)*16 + (rcData[AUX2]>1700)*32;
    
    //note: if FAILSAFE is disable, failsafeCnt > 5*FAILSAVE_DELAY is always false
    if (((rcOptions & activate[BOXACC]) || (failsafeCnt > 5*FAILSAVE_DELAY) ) && (ACC || nunchuk)) { 
      // bumpless transfer to Level mode
      if (!accMode) {
        errorAngleI[ROLL] = 0; errorAngleI[PITCH] = 0;
        accMode = 1;
      }  
    } else accMode = 0;  // modified by MIS for failsave support

    if ((rcOptions & activate[BOXARM]) == 0) okToArm = 1;
    if (accMode == 1) STABLEPIN_ON else STABLEPIN_OFF;

  }
    
  computeIMU();
  // Measure loop rate just afer reading the sensors
  currentTime = micros();
  cycleTime = currentTime - previousTime;
  previousTime = currentTime;



  //**** PITCH & ROLL & YAW PID ****    
  for(axis=0;axis<3;axis++) {
    if (accMode == 1 && axis<2 ) { //LEVEL MODE
      errorAngle = rcCommand[axis] - angle[axis];                                 //500+180 = 680: 16 bits is ok here
      PTerm      = errorAngle*(P8[PIDLEVEL]/10)/10 ;                              //680*20 = 13600: 16 bits is ok here

      errorAngleI[axis] += errorAngle;                                            //16 bits is ok here
      errorAngleI[axis]  = constrain(errorAngleI[axis],-10000,+10000); //WindUp   //16 bits is ok here
      ITerm              = (int32_t)errorAngleI[axis]*I8[PIDLEVEL]/4000;          //32 bits is needed for calculation:10000*I8 could exceed 32768   16 bits is ok for result
    } else { //ACRO MODE or YAW axis
      error = (int32_t)rcCommand[axis]*10*8/P8[axis] - gyroData[axis];            //32 bits is needed for calculation: 500*10*8 = 40000   16 bits is ok for result if P>2
      PTerm = rcCommand[axis];

      errorGyroI[axis] += error;                                                  //16 bits is ok here
      errorGyroI[axis]  = constrain(errorGyroI[axis],-16000,+16000); //WindUp     //16 bits is ok here
      if (abs(gyroData[axis])>640) errorGyroI[axis] = 0;
      ITerm = (int32_t)errorGyroI[axis]*I8[axis]/1000/8;                          //32 bits is needed for calculation: 16000*I8  16 bits is ok for result
    }
    PTerm         -= (int32_t)gyroData[axis]*dynP8[axis]/10/8;                    //32 bits is needed for calculation            16 bits is ok for result

    delta          = gyroData[axis] - lastGyro[axis];                             //16 bits is ok here, because the dif between 2 consecutive gyro reads is limited
    DTerm          = (delta1[axis]+delta2[axis]+delta+1)*dynD8[axis]/3/8;         //16 bits is ok here
    delta2[axis]   = delta1[axis];
    delta1[axis]   = delta;
    lastGyro[axis] = gyroData[axis];

    axisPID[axis] =  PTerm + ITerm - DTerm;
  }
  
 
  if(setupMode == 0){ // Vibration Monitor Mode
    my_mixTable(); // new function for Vibtest instead of mixTable()
    writeMotors();
  }  
}
