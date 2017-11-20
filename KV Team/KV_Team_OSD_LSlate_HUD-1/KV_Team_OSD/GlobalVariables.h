#define MPH 1
#define KMH 0

#define METRIC 0
#define IMPERIAL 1
//Analog input defines
const uint16_t voltagePin=0;
const uint16_t vidvoltagePin=2;
const uint16_t amperagePin=1;
const uint16_t rssiPin=3;
const uint16_t temperaturePin=6;            // Temperature pin 6 for original Rushduino Board V1.2
const uint8_t rssiSample=30;
//const uint8_t lowrssiAlarm=RSSI_ALARM;     // This will make blink the Rssi if lower then this value

//General use variables
int tenthSec=0;
int halfSec=0;
int Blink2hz=0;                             // This is turing on and off at 2hz
int Blink10hz=0;                            // This is turing on and off at 10hz
int lastCallSign=0;                          //callsign_timer
uint8_t rssiTimer=0;
uint8_t accCalibrationTimer=0;
uint8_t magCalibrationTimer=0;
uint8_t eepromWriteTimer=0;

int climbLineOffset=0;

unsigned int allSec=0;

// Config status and cursor location
uint8_t ROW=10;
uint8_t COL=3;
uint8_t configPage=MINPAGE;
uint8_t configMode=0;
uint8_t fontMode = 0;
uint8_t fontData[54];
uint8_t nextCharToRequest;
uint8_t lastCharToRequest;
uint8_t retransmitQueue;

// Mode bits
uint32_t mode_armed;
uint32_t mode_stable;
uint32_t mode_baro;
uint32_t mode_mag;
uint32_t mode_gpshome;
uint32_t mode_gpshold;
uint32_t mode_llights;
uint32_t mode_osd_switch;

// Settings Locations
enum Setting_ {
  S_CHECK_,		// used for check
  S_RSSIMIN,
  S_RSSIMAX,
  S_RSSI_ALARM,
  S_DISPLAYRSSI,
  S_DISPLAYVOLTAGE,
  S_VOLTAGEMIN,
  S_BATCELLS,
  S_DIVIDERRATIO,
  S_MAINVOLTAGE_VBAT,
  S_AMPERAGE,
  S_AMPER_HOUR,
  S_VIDVOLTAGE,
  S_VIDDIVIDERRATIO,
  S_VIDVOLTAGE_VBAT,
  S_DISPLAYTEMPERATURE,
  S_TEMPERATUREMAX,
  S_BOARDTYPE,
  S_DISPLAYGPS,
  S_COORDINATES,
  S_SHOWHEADING,
  S_HEADING360,
  S_UNITSYSTEM,
  S_VIDEOSIGNALTYPE,
  S_THROTTLEPOSITION,
  S_DISPLAY_HORIZON_BR,
  S_WITHDECORATION,
  S_SHOWBATLEVELEVOLUTION,
  S_RESETSTATISTICS,
  S_ENABLEADC,
  S_MWRSSI,
  S_PWMRSSI,
  S_USE_BOXNAMES,
  S_DISPLAY_CS,
  S_CS0,
  S_CS1,
  S_CS2,
  S_CS3,
  S_CS4,
  S_CS5,
  S_CS6,
  S_CS7,
  S_CS8,
  S_CS9,
  // EEPROM_SETTINGS must be last!
  EEPROM_SETTINGS
};

uint8_t Settings[EEPROM_SETTINGS];


// For Settings Defaults
uint8_t EEPROM_DEFAULT[EEPROM_SETTINGS] = {
1,   // used for check
0,   // S_RSSIMIN
255, // S_RSSIMAX
60,  //S_RSSI_ALARM
1,   // S_DISPLAYRSSI
1,   // S_DISPLAYVOLTAGE
105, // S_VOLTAGEMIN
3,   // S_BATCELLS
100, // S_DIVIDERRATIO
0,   // S_MAINVOLTAGE_VBAT
0,   // S_AMPERAGE
0,   // S_AMPER_HOUR
0,   // S_VIDVOLTAGE
100, // S_VIDDIVIDERRATIO
0,   // S_VIDVOLTAGE_VBAT
0,   // S_DISPLAYTEMPERATURE
255, // S_TEMPERATUREMAX
0,   // S_BOARDTYPE
1,   // S_DISPLAYGPS
0,   // S_COORDINATES
1,   // S_SHOWHEADING
1,   // S_HEADING360
0,   // S_UNITSYSTEM
1,   // S_VIDEOSIGNALTYPE
1,   // S_THROTTLEPOSITION
1,   // S_DISPLAY_HORIZON_BR
1,   // S_WITHDECORATION
0,   // S_SHOWBATLEVELEVOLUTION
1,   // S_RESETSTATISTICS
0,   // S_ENABLEADC
1,   // S_MWRSSI
0,   // S_PWMRSSI
0,   // S_USE_BOXNAMES
0,   // S_DISPLAY_CS,
0,   // S_CS0,
0,   // S_CS1,
0,   // S_CS2,
0,   // S_CS3,
0,   // S_CS4,
0,   // S_CS5,
0,   // S_CS6,
0,   // S_CS7,
0,   // S_CS8,
0,   // S_CS9,
};

static uint8_t P8[PIDITEMS], I8[PIDITEMS], D8[PIDITEMS];

static uint8_t rcRate8,rcExpo8;
static uint8_t rollPitchRate;
static uint8_t yawRate;
static uint8_t dynThrPID;
static uint8_t thrMid8;
static uint8_t thrExpo8;


static uint16_t  MwAccSmooth[3]={0,0,0};       // Those will hold Accelerator data
int32_t  MwAltitude=0;                         // This hold barometric value


int MwAngle[2]={0,0};           // Those will hold Accelerator Angle
static uint16_t MwRcData[8]={   // This hold receiver pulse signal
  1500,1500,1500,1500,1500,1500,1500,1500} ;

uint16_t  MwSensorPresent=0;
uint32_t  MwSensorActive=0;
uint8_t MwVersion=0;
uint8_t MwVBat=0;
int16_t MwVario=0;
uint8_t armed=0;
uint8_t previousarmedstatus=0;  // NEB for statistics after disarming
int16_t GPS_distanceToHome=0;
uint8_t GPS_fix=0;
int32_t GPS_latitude;
int32_t GPS_longitude;
int16_t GPS_altitude;
uint16_t GPS_speed=0;
int16_t GPS_directionToHome=0;
uint8_t GPS_numSat=0;
int16_t I2CError=0;
uint16_t cycleTime=0;
uint16_t pMeterSum=0;
uint8_t MwRssi=0;

//For Current Throttle
int LowT = 1100;
int HighT = 1900;

// For Time
uint16_t onTime=0;
uint16_t flyTime=0;

// For Heading
const char headGraph[] PROGMEM = {
  0x1a,0x1d,0x1c,0x1d,0x19,0x1d,0x1c,0x1d,0x1b,0x1d,0x1c,0x1d,0x18,0x1d,0x1c,0x1d,0x1a,0x1d,0x1c,0x1d,0x19,0x1d,0x1c,0x1d,0x1b};
static int16_t MwHeading=0;

// For Amperage
float amperage = 0;                // its the real value x10
float amperagesum = 0;

// Rssi
int rssi =0;
int rssiADC=0;
//int rssiMin;
//int rssiMax;
int rssi_Int=0;


// For Voltage
uint8_t voltage=0;                      // its the value x10
uint8_t vidvoltage=0;                   // its the value x10

// For temprature
int8_t temperature=0;                  // temperature in degrees Centigrade


// For Statistics
uint16_t speedMAX=0;
int8_t temperMAX=0;
int16_t altitudeMAX=0;
int16_t distanceMAX=0;
float trip=0;
uint16_t flyingTime=0; 


// ---------------------------------------------------------------------------------------
// Defines imported from Multiwii Serial Protocol MultiWii_shared svn r1337
#define MSP_VERSION              0

//to multiwii developpers/committers : do not add new MSP messages without a proper argumentation/agreement on the forum
#define MSP_IDENT                100   //out message         multitype + multiwii version + protocol version + capability variable
#define MSP_STATUS               101   //out message         cycletime & errors_count & sensor present & box activation & current setting number
#define MSP_RAW_IMU              102   //out message         9 DOF
#define MSP_SERVO                103   //out message         8 servos
#define MSP_MOTOR                104   //out message         8 motors
#define MSP_RC                   105   //out message         8 rc chan and more
#define MSP_RAW_GPS              106   //out message         fix, numsat, lat, lon, alt, speed, ground course
#define MSP_COMP_GPS             107   //out message         distance home, direction home
#define MSP_ATTITUDE             108   //out message         2 angles 1 heading
#define MSP_ALTITUDE             109   //out message         altitude, variometer
#define MSP_ANALOG               110   //out message         vbat, powermetersum, rssi if available on RX
#define MSP_RC_TUNING            111   //out message         rc rate, rc expo, rollpitch rate, yaw rate, dyn throttle PID
#define MSP_PID                  112   //out message         P I D coeff (9 are used currently)
#define MSP_BOX                  113   //out message         BOX setup (number is dependant of your setup)
#define MSP_MISC                 114   //out message         powermeter trig
#define MSP_MOTOR_PINS           115   //out message         which pins are in use for motors & servos, for GUI 
#define MSP_BOXNAMES             116   //out message         the aux switch names
#define MSP_PIDNAMES             117   //out message         the PID names
#define MSP_WP                   118   //out message         get a WP, WP# is in the payload, returns (WP#, lat, lon, alt, flags) WP#0-home, WP#16-poshold
#define MSP_BOXIDS               119   //out message         get the permanent IDs associated to BOXes

#define MSP_SET_RAW_RC           200   //in message          8 rc chan
#define MSP_SET_RAW_GPS          201   //in message          fix, numsat, lat, lon, alt, speed
#define MSP_SET_PID              202   //in message          P I D coeff (9 are used currently)
#define MSP_SET_BOX              203   //in message          BOX setup (number is dependant of your setup)
#define MSP_SET_RC_TUNING        204   //in message          rc rate, rc expo, rollpitch rate, yaw rate, dyn throttle PID
#define MSP_ACC_CALIBRATION      205   //in message          no param
#define MSP_MAG_CALIBRATION      206   //in message          no param
#define MSP_SET_MISC             207   //in message          powermeter trig + 8 free for future use
#define MSP_RESET_CONF           208   //in message          no param
#define MSP_SET_WP               209   //in message          sets a given WP (WP#,lat, lon, alt, flags)
#define MSP_SELECT_SETTING       210   //in message          Select Setting Number (0-2)
#define MSP_SET_HEAD             211   //in message          define a new heading hold direction

#define MSP_BIND                 240   //in message          no param

#define MSP_EEPROM_WRITE         250   //in message          no param

#define MSP_DEBUGMSG             253   //out message         debug string buffer
#define MSP_DEBUG                254   //out message         debug1,debug2,debug3,debug4
// End of imported defines from Multiwii Serial Protocol MultiWii_shared svn r1333
// ---------------------------------------------------------------------------------------

// Private MSP for use with the GUI
#define MSP_OSD                  220   //in message          starts epprom send to OSD GUI
// Subcommands
#define OSD_NULL                 0
#define OSD_READ_CMD             1
#define OSD_WRITE_CMD            2
#define OSD_GET_FONT             3
#define OSD_SERIAL_SPEED         4
#define OSD_RESET                5
// End private MSP for use with the GUI

const char disarmed_text[] PROGMEM = "DISARMED";
const char armed_text[] PROGMEM = " ARMED";




// For Intro
const char message0[] PROGMEM = "  MW PRO HUD OSD";
const char message1[] PROGMEM = "VIDEO SIGNAL: NTSC";
const char message2[] PROGMEM = "VIDEO SIGNAL: PAL ";
const char message5[] PROGMEM = "MW VERSION:";
const char message6[] PROGMEM = "MENU:THRT MIDDLE";
const char message7[] PROGMEM = "YAW RIGHT";
const char message8[] PROGMEM = "PITCH FULL";
const char message59[] PROGMEM = "UNIQUE ID:";         // Call Sign on the beggining of the transmission   

// For Config
const char configMsg0[] PROGMEM = "EXIT";
const char configMsg1[] PROGMEM = "SAVE-EXIT";
const char configMsg2[] PROGMEM = "<PAGE>";
//-----------------------------------------------------------Page1
const char configMsg3[] PROGMEM = "1/6 PID CONFIG";
const char configMsg4[] PROGMEM = "ROLL";
const char configMsg5[] PROGMEM = "PITCH";
const char configMsg6[] PROGMEM = "YAW";
const char configMsg7[] PROGMEM = "ALT";
const char configMsg8[] PROGMEM = "GPS";
const char configMsg9[] PROGMEM = "LEVEL";
const char configMsg10[] PROGMEM = "MAG";
//-----------------------------------------------------------Page2
const char configMsg11[] PROGMEM = "2/6 RC TUNING";
const char configMsg12[] PROGMEM = "RC RATE";
const char configMsg13[] PROGMEM = "EXPO";
const char configMsg14[] PROGMEM = "ROLL PITCH RATE";
const char configMsg15[] PROGMEM = "YAW RATE";
const char configMsg16[] PROGMEM = "THROTTLE PID ATT";
const char configMsg17[] PROGMEM = "MWCYCLE TIME";
const char configMsg18[] PROGMEM = "MWI2C ERRORS";
//-----------------------------------------------------------Page3
const char configMsg19[] PROGMEM = "3/6 DISPLAY & ALARM";
const char configMsg20[] PROGMEM = "      ";
const char configMsg21[] PROGMEM = "ON";
const char configMsg22[] PROGMEM = "OFF";
const char configMsg23[] PROGMEM = "DISPLAY VOLTAGE";
const char configMsg24[] PROGMEM = "VOLTAGE ALARM";
const char configMsg25[] PROGMEM = "DISPLAY TEMPERATURE";
const char configMsg26[] PROGMEM = "SET ALARM TEMP";
const char configMsg27[] PROGMEM = "DISPLAY GPS";
const char configMsg28[] PROGMEM = "DISPLAY COORDINATES";
const char configMsg29[] PROGMEM = " ";
const char configMsg30[] PROGMEM = "                   ";
//-----------------------------------------------------------Page4
const char configMsg31[] PROGMEM = "4/6 DISPLAY";
const char configMsg32[] PROGMEM = "ACTUAL RSSIADC(/4)";
const char configMsg33[] PROGMEM = "ACTUAL RSSI";
const char configMsg34[] PROGMEM = "SET RSSI MIN";
const char configMsg35[] PROGMEM = "SET RSSI MAX";
const char configMsg36[] PROGMEM = "DISPLAY RSSI";
const char configMsg37[] PROGMEM = "UNIT SYSTEM";
const char configMsg38[] PROGMEM = "METRIC";
const char configMsg39[] PROGMEM = "IMPERL";
const char configMsg40[] PROGMEM = "VIDEO SYSTEM";
const char configMsg41[] PROGMEM = "NTSC";
const char configMsg42[] PROGMEM = "PAL";
//-----------------------------------------------------------Page5
const char configMsg43[] PROGMEM = "5/6 CALIBRATION";
const char configMsg44[] PROGMEM = "ACC CALIBRATION";
const char configMsg45[] PROGMEM = "ACC ROLL :";
const char configMsg46[] PROGMEM = "ACC PITCH :";
const char configMsg47[] PROGMEM = "ACC Z :";
const char configMsg48[] PROGMEM = "MAG CALIBRATION";
const char configMsg49[] PROGMEM = "HEADING";
const char configMsg50[] PROGMEM = "MW EEPROM WRITE";
//-----------------------------------------------------------Page6
const char configMsg51[] PROGMEM = "6/6 STATISTICS";
const char configMsg52[] PROGMEM = "TRIP:";
const char configMsg53[] PROGMEM = "MAX DISTANCE:";
const char configMsg54[] PROGMEM = "MAX ALTITUDE:";
const char configMsg55[] PROGMEM = "MAX SPEED:";
const char configMsg56[] PROGMEM = "FLYING TIME:";
const char configMsg57[] PROGMEM = "DRAINED AMPS:";
const char configMsg58[] PROGMEM = "MAX TEMP:";



// POSITION OF EACH CHARACTER OR LOGO IN THE MAX7456
const unsigned char speedUnitAdd[2] ={
  0xa5,0xa6} ; // [0][0] and [0][1] = Km/h   [1][0] and [1][1] = Mph
const unsigned char temperatureUnitAdd[2] = {
  0x0e,0x0d};

const char MultiWiiLogoL1Add[17] PROGMEM = {
  0xd0,0xd1,0xd2,0xd3,0xd4,0xd5,0xd6,0xd7,0xd8,0xd9,0xda,0xdb,0xdc,0xdd,0xde,0};
const char MultiWiiLogoL2Add[17] PROGMEM = {
  0xe0,0xe1,0xe2,0xe3,0xe4,0xe5,0xe6,0xe7,0xe8,0xe9,0xea,0xeb,0xec,0xed,0xee,0};
const char MultiWiiLogoL3Add[17] PROGMEM = {
  0xf0,0xf1,0xf2,0xf3,0xf4,0xf5,0xf6,0xf7,0xf8,0xf9,0xfa,0xfb,0xfc,0xfd,0xfe,0};

const unsigned char MwAltitudeAdd[2]={
  0xa7,0xa8};
/*const char* MwAltitudeAdd[3]={"ALT"};*/

const unsigned char MwClimbRateAdd[2]={
  0x9f,0x99};
/*const  char* MwClimbRateAdd[3]={"VSI"}*/;

const unsigned char GPS_distanceToHomeAdd[2]={
  0xbb,0xb9};
/*const  char* GPS_distanceToHomeAdd[3]={"DME"};*/

const unsigned char MwGPSAltPositionAdd[2]={
  0xa7,0xa8};
/*const  char* MwGPSAltPositionAdd[3]={"ALT"};*/
  
const char KVTeamVersionPosition = 35;


// All screen locations defines in ScreenLayout.ino
enum Positions {
  GPS_numSatPosition,
  GPS_directionToHomePosition,
  GPS_distanceToHomePosition,
  speedPosition,
  GPS_angleToHomePosition,
  MwGPSAltPosition,
  sensorPosition,
  MwHeadingPosition,
  MwHeadingGraphPosition,
  MwAltitudePosition,
  MwClimbRatePosition,
  CurrentThrottlePosition,
  ThrottleGraphPosition,
  flyTimePosition,
  onTimePosition,
  motorArmedPosition,
  MwGPSLatPosition,
  MwGPSLonPosition,
  rssiPosition,
  temperaturePosition,
  voltagePosition,
  vidvoltagePosition,
  amperagePosition,
  pMeterSumPosition,
  horizonPosition,
  callSignPosition
};

#define REQ_MSP_IDENT     (1 <<  0)
#define REQ_MSP_STATUS    (1 <<  1)
#define REQ_MSP_RAW_IMU   (1 <<  2)
#define REQ_MSP_RC        (1 <<  3)
#define REQ_MSP_RAW_GPS   (1 <<  4)
#define REQ_MSP_COMP_GPS  (1 <<  5)
#define REQ_MSP_ATTITUDE  (1 <<  6)
#define REQ_MSP_ALTITUDE  (1 <<  7)
#define REQ_MSP_ANALOG    (1 <<  8)
#define REQ_MSP_RC_TUNING (1 <<  9)
#define REQ_MSP_PID       (1 << 10)
#define REQ_MSP_BOX       (1 << 11)
#define REQ_MSP_FONT      (1 << 12)
