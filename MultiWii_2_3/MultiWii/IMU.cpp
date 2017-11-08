#include "Arduino.h"
#include "config.h"
#include "def.h"
#include "types.h"
#include "MultiWii.h"
#include "IMU.h"
#include "Sensors.h"
#include "RunningMedian.h"

void getEstimatedAttitude();

void computeIMU()
{
  uint8_t axis;
  static int16_t gyroADCprevious[3] = {0, 0, 0};
  int16_t gyroADCp[3];
  int16_t gyroADCinter[3];
  static uint32_t timeInterleave = 0;

//we separate the 2 situations because reading gyro values with a gyro only setup can be acchieved at a higher rate
//gyro+nunchuk: we must wait for a quite high delay betwwen 2 reads to get both WM+ and Nunchuk data. It works with 3ms
//gyro only: the delay to read 2 consecutive values can be reduced to only 0.65ms
#if defined(NUNCHUCK)
  annexCode();
  while ((uint16_t)(micros() - timeInterleave) < INTERLEAVING_DELAY)
    ; //interleaving delay between 2 consecutive reads
  timeInterleave = micros();
  ACC_getADC();
  getEstimatedAttitude(); // computation time must last less than one interleaving delay
  while ((uint16_t)(micros() - timeInterleave) < INTERLEAVING_DELAY)
    ; //interleaving delay between 2 consecutive reads
  timeInterleave = micros();
  f.NUNCHUKDATA = 1;
  while (f.NUNCHUKDATA)
    ACC_getADC(); // For this interleaving reading, we must have a gyro update at this point (less delay)

  for (axis = 0; axis < 3; axis++)
  {
    // empirical, we take a weighted value of the current and the previous values
    // /4 is to average 4 values, note: overflow is not possible for WMP gyro here
    imu.gyroData[axis] = (imu.gyroADC[axis] * 3 + gyroADCprevious[axis]) >> 2;
    gyroADCprevious[axis] = imu.gyroADC[axis];
  }
#else
#if ACC
  ACC_getADC();
  getEstimatedAttitude();
#endif
#if GYRO
  Gyro_getADC();
#endif
  for (axis = 0; axis < 3; axis++)
    gyroADCp[axis] = imu.gyroADC[axis];
  timeInterleave = micros();
  annexCode();
  uint8_t t = 0;
  while ((uint16_t)(micros() - timeInterleave) < 650)
    t = 1; //empirical, interleaving delay between 2 consecutive reads
  if (!t)
    annex650_overrun_count++;
#if GYRO
  Gyro_getADC();
#endif
  for (axis = 0; axis < 3; axis++)
  {
    gyroADCinter[axis] = imu.gyroADC[axis] + gyroADCp[axis];
    // empirical, we take a weighted value of the current and the previous values
    imu.gyroData[axis] = (gyroADCinter[axis] + gyroADCprevious[axis]) / 3;
    gyroADCprevious[axis] = gyroADCinter[axis] >> 1;
    if (!ACC)
      imu.accADC[axis] = 0;
  }
#endif
#if defined(GYRO_SMOOTHING)
  static int16_t gyroSmooth[3] = {0, 0, 0};
  for (axis = 0; axis < 3; axis++)
  {
    imu.gyroData[axis] = (int16_t)(((int32_t)((int32_t)gyroSmooth[axis] * (conf.Smoothing[axis] - 1)) + imu.gyroData[axis] + 1) / conf.Smoothing[axis]);
    gyroSmooth[axis] = imu.gyroData[axis];
  }
#elif defined(TRI)
  static int16_t gyroYawSmooth = 0;
  imu.gyroData[YAW] = (gyroYawSmooth * 2 + imu.gyroData[YAW]) / 3;
  gyroYawSmooth = imu.gyroData[YAW];
#endif
}

// **************************************************
// Simplified IMU based on "Complementary Filter"
// Inspired by http://starlino.com/imu_guide.html
//
// adapted by ziss_dm : http://www.multiwii.com/forum/viewtopic.php?f=8&t=198
//
// The following ideas was used in this project:
// 1) Rotation matrix: http://en.wikipedia.org/wiki/Rotation_matrix
// 2) Small-angle approximation: http://en.wikipedia.org/wiki/Small-angle_approximation
// 3) C. Hastings approximation for atan2()
// 4) Optimization tricks: http://www.hackersdelight.org/
//
// Currently Magnetometer uses separate CF which is used only
// for heading approximation.
//
// **************************************************

//******  advanced users settings *******************
/* Set the Low Pass Filter factor for ACC
   Increasing this value would reduce ACC noise (visible in GUI), but would increase ACC lag time
   Comment this if  you do not want filter at all.
   unit = n power of 2 */
// this one is also used for ALT HOLD calculation, should not be changed
#ifndef ACC_LPF_FACTOR
#define ACC_LPF_FACTOR 4 // that means a LPF of 16
#endif

/* Set the Gyro Weight for Gyro/Acc complementary filter
   Increasing this value would reduce and delay Acc influence on the output of the filter*/
#ifndef GYR_CMPF_FACTOR
#define GYR_CMPF_FACTOR 600
#endif

/* Set the Gyro Weight for Gyro/Magnetometer complementary filter
   Increasing this value would reduce and delay Magnetometer influence on the output of the filter*/
#define GYR_CMPFM_FACTOR 250

//****** end of advanced users settings *************
#define INV_GYR_CMPF_FACTOR (1.0f / (GYR_CMPF_FACTOR + 1.0f))
#define INV_GYR_CMPFM_FACTOR (1.0f / (GYR_CMPFM_FACTOR + 1.0f))

typedef struct fp_vector
{
  float X, Y, Z;
} t_fp_vector_def;

typedef union {
  float A[3];
  t_fp_vector_def V;
} t_fp_vector;

typedef struct int32_t_vector
{
  int32_t X, Y, Z;
} t_int32_t_vector_def;

typedef union {
  int32_t A[3];
  t_int32_t_vector_def V;
} t_int32_t_vector;

int16_t _atan2(int32_t y, int32_t x)
{
  float z = (float)y / x;
  int16_t a;
  if (abs(y) < abs(x))
  {
    a = 573 * z / (1.0f + 0.28f * z * z);
    if (x < 0)
    {
      if (y < 0)
        a -= 1800;
      else
        a += 1800;
    }
  }
  else
  {
    a = 900 - 573 * z / (z * z + 0.28f);
    if (y < 0)
      a -= 1800;
  }
  return a;
}

float InvSqrt(float x)
{
  union {
    int32_t i;
    float f;
  } conv;
  conv.f = x;
  conv.i = 0x5f3759df - (conv.i >> 1);
  return 0.5f * conv.f * (3.0f - x * conv.f * conv.f);
}

// Rotate Estimated vector(s) with small angle approximation, according to the gyro data
void rotateV(struct fp_vector *v, float *delta)
{
  fp_vector v_tmp = *v;
  v->Z -= delta[ROLL] * v_tmp.X + delta[PITCH] * v_tmp.Y;
  v->X += delta[ROLL] * v_tmp.Z - delta[YAW] * v_tmp.Y;
  v->Y += delta[PITCH] * v_tmp.Z + delta[YAW] * v_tmp.X;
}

static int32_t accLPF32[3] = {0, 0, 1};
static float invG; // 1/|G|

static t_fp_vector EstG;
static t_int32_t_vector EstG32;
#if MAG
static t_int32_t_vector EstM32;
static t_fp_vector EstM;
#endif

void getEstimatedAttitude()
{
  uint8_t axis;
  int32_t accMag = 0;
  float scale, deltaGyroAngle[3];
  uint8_t validAcc;
  static uint16_t previousT;
  uint16_t currentT = micros();

  scale = (currentT - previousT) * GYRO_SCALE; // GYRO_SCALE unit: radian/microsecond
  previousT = currentT;

  // Initialization
  for (axis = 0; axis < 3; axis++)
  {
    deltaGyroAngle[axis] = imu.gyroADC[axis] * scale; // radian

    accLPF32[axis] -= accLPF32[axis] >> ACC_LPF_FACTOR;
    accLPF32[axis] += imu.accADC[axis];
    imu.accSmooth[axis] = accLPF32[axis] >> ACC_LPF_FACTOR;

    accMag += (int32_t)imu.accSmooth[axis] * imu.accSmooth[axis];
  }

  rotateV(&EstG.V, deltaGyroAngle);
#if MAG
  rotateV(&EstM.V, deltaGyroAngle);
#endif

  accMag = accMag * 100 / ((int32_t)ACC_1G * ACC_1G);
  validAcc = 72 < (uint16_t)accMag && (uint16_t)accMag < 133;
  // Apply complimentary filter (Gyro drift correction)
  // If accel magnitude >1.15G or <0.85G and ACC vector outside of the limit range => we neutralize the effect of accelerometers in the angle estimation.
  // To do that, we just skip filter, as EstV already rotated by Gyro
  for (axis = 0; axis < 3; axis++)
  {
    if (validAcc)
      EstG.A[axis] = (EstG.A[axis] * GYR_CMPF_FACTOR + imu.accSmooth[axis]) * INV_GYR_CMPF_FACTOR;
    EstG32.A[axis] = EstG.A[axis]; //int32_t cross calculation is a little bit faster than float
#if MAG
    EstM.A[axis] = (EstM.A[axis] * GYR_CMPFM_FACTOR + imu.magADC[axis]) * INV_GYR_CMPFM_FACTOR;
    EstM32.A[axis] = EstM.A[axis];
#endif
  }

  if ((int16_t)EstG32.A[2] > ACCZ_25deg)
    f.SMALL_ANGLES_25 = 1;
  else
    f.SMALL_ANGLES_25 = 0;

  // Attitude of the estimated vector
  int32_t sqGX_sqGZ = sq(EstG32.V.X) + sq(EstG32.V.Z);
  invG = InvSqrt(sqGX_sqGZ + sq(EstG32.V.Y));
  att.angle[ROLL] = _atan2(EstG32.V.X, EstG32.V.Z);
  att.angle[PITCH] = _atan2(EstG32.V.Y, InvSqrt(sqGX_sqGZ) * sqGX_sqGZ);

#if MAG
  att.heading = _atan2(
      EstM32.V.Z * EstG32.V.X - EstM32.V.X * EstG32.V.Z,
      (EstM.V.Y * sqGX_sqGZ - (EstM32.V.X * EstG32.V.X + EstM32.V.Z * EstG32.V.Z) * EstG.V.Y) * invG);
  att.heading += conf.mag_declination; // Set from GUI
  att.heading /= 10;
#endif

#if defined(THROTTLE_ANGLE_CORRECTION)
  cosZ = EstG.V.Z / ACC_1G * 100.0f;                                                        // cos(angleZ) * 100
  throttleAngleCorrection = THROTTLE_ANGLE_CORRECTION * constrain(100 - cosZ, 0, 100) >> 3; // 16 bit ok: 200*150 = 30000
#endif
}

#define UPDATE_INTERVAL 25000 // 40hz update rate (20hz LPF on acc)
#define BARO_TAB_SIZE 21

#define ACC_Z_DEADBAND (ACC_1G >> 5) // was 40 instead of 32 now

#define applyDeadband(value, deadband) \
  if (abs(value) < deadband)           \
  {                                    \
    value = 0;                         \
  }                                    \
  else if (value > 0)                  \
  {                                    \
    value -= deadband;                 \
  }                                    \
  else if (value < 0)                  \
  {                                    \
    value += deadband;                 \
  }

// 2015.11.30 by XD, x is in 0.1 deg, returns cos * (1 << 10)
// when x approaches zero, cos(x) = 1 - x ^ 2 / 2, x is in radians
// https://en.wikipedia.org/wiki/Small-angle_approximation
// rad = deg / 180 * PI
// int32_t _cos10(int16_t x)
// {
//   // x within [-1800, 1800]
//   int32_t radTemp = (int32_t)x * 114; // rad = x * ((PI / 1800) << 16), rad within [-205200, 205200]
//   int32_t rad = radTemp >> 6; // rad ^ 2 within [-657922500, 657922500]

//   int32_t cos20 = ((uint32_t)1 << 20) - ((rad * rad) >> 1);
//   int32_t result = cos20 >> 10;

//   return result;
// }

// another sonar alt fusion
// BaroAlt = ( logBaroGroundPressureSum - log(baroPressureSum) ) * baroGroundTemperatureScale;
// debug[0] = BaroAlt;
// debug[1] = sonarAlt;
// // Nov 8, 2017 WQ Chen - if sonar reads less than 4m (sensor limit 4.5m - safety margin 0.5m), use sonar
// if ((sonarAlt > 0 && sonarAlt < 400) && 
// ((att.angle[ROLL] > -60 && att.angle[ROLL] < 60) && (att.angle[PITCH] > -60 && att.angle[PITCH] < 60)))
// {
//   // actual alt = sonarAlt * cos(att.angle[ROLL]) * cos(att.angle[PITCH])
//   BaroAlt = (int32_t)abs(sonarAlt * cos(att.angle[ROLL]) * cos(att.angle[PITCH]));
// }
// alt.EstAlt = (alt.EstAlt * 6 + BaroAlt * 2) >> 3; // additional LPF to reduce baro noise (faster by 30 µs)
// debug[2] = alt.EstAlt;

#if BARO
RunningMedian baroValues = RunningMedian(29);
int32_t estBaroAlt = 0;

#if SONAR
RunningMedian sonarValues = RunningMedian(19);
int32_t estSonarAlt = 0;
#endif

uint8_t getEstimatedAltitude()
{
  int32_t BaroAlt;
  static float baroGroundTemperatureScale, logBaroGroundPressureSum;
  static float vel = 0.0f;
  static uint16_t previousT;
  uint16_t currentT = micros();
  uint16_t dTime;

  dTime = currentT - previousT;
  if (dTime < UPDATE_INTERVAL)
    return 0;
  previousT = currentT;

  if (calibratingB > 0)
  {
    logBaroGroundPressureSum = log(baroPressureSum);
    baroGroundTemperatureScale = ((int32_t)baroTemperature + 27315) * (2 * 29.271267f); // 2 *  is included here => no need for * 2  on BaroAlt in additional LPF
    calibratingB--;
  }

  // baroGroundPressureSum is not supposed to be 0 here
  // see: https://code.google.com/p/ardupilot-mega/source/browse/libraries/AP_Baro/AP_Baro.cpp
  int32_t tmpBaroAlt = (logBaroGroundPressureSum - log(baroPressureSum)) * baroGroundTemperatureScale;
  baroValues.add(tmpBaroAlt);
  BaroAlt = baroValues.getAverage();
  // Estimated altitude from barometer
  estBaroAlt = (estBaroAlt * 6 + BaroAlt) >> 3;

#if SONAR
  int32_t tmpSonarAlt = 0;
  // Estimated altitude from ultrasonic sensor
  if (sonarAlt >= 0 && sonarAlt <= SONAR_MAX_RANGE)
  {
    sonarValues.add(sonarAlt - SONAR_GROUND_OFFSET);
    tmpSonarAlt = sonarValues.getMedian();
    estSonarAlt = estSonarAlt * SONAR_IMPACT + tmpSonarAlt * (1 - SONAR_IMPACT);
  }
  debug[0] = sonarAlt;
  debug[1] = tmpSonarAlt;
  debug[2] = estSonarAlt;
#endif

#if BARO && !SONAR
  // Use baro altitude
  alt.EstAlt = estBaroAlt;
#elif SONAR && !BARO
  // Use sonar altitude
  alt.EstAlt = estSonarAlt;
#elif SONAR && BARO
  // Use sonar altitude when near ground (lower than SONAR_BARO_FUSION_LC).
  // Use baro altitude when flying high (greater than SONAR_BARO_FUSION_HC)
  // or when there are too many failed measurements (negative value).
  // Mix baro and sonar between SONAR_BARO_FUSION_LC and SONAR_BARO_FUSION_HC.
  if (sonarAlt >= 0 && sonarAlt <= SONAR_BARO_FUSION_LC)
  {
    alt.EstAlt = estSonarAlt;
  }
  else if (sonarAlt >= 0 && sonarAlt <= SONAR_BARO_FUSION_HC)
  {
    // Linearly decrease sonar weight between LC (weight is 1) and HC (weight is 0)
    float fade = ((float)(SONAR_BARO_FUSION_HC - sonarAlt)) / (SONAR_BARO_FUSION_HC - SONAR_BARO_FUSION_LC);
    fade = constrain(fade, 0.0f, 1.0f);
    alt.EstAlt = estSonarAlt * fade + estBaroAlt * (1 - fade);
  }
  else
  {
    alt.EstAlt = estBaroAlt;
  }
  debug[3] = alt.EstAlt;
#endif

#if (defined(VARIOMETER) && (VARIOMETER != 2)) || !defined(SUPPRESS_BARO_ALTHOLD)
  //P
  int16_t error16 = constrain(AltHold - alt.EstAlt, -300, 300);
  // could comment this to remove "applyDeadband" because if the sensor alt is trustworthy accurate
  // the increase the max values of P and D from +-150 to +-200
  // refer http://www.multiwii.com/forum/viewtopic.php?f=8&t=7850
  applyDeadband(error16, 10); //remove small P parametr to reduce noise near zero position
  BaroPID = constrain((conf.pid[PIDALT].P8 * error16 >> 7), -150, +150);

  //I
  errorAltitudeI += conf.pid[PIDALT].I8 * error16 >> 6;
  errorAltitudeI = constrain(errorAltitudeI, -30000, 30000);
  BaroPID += errorAltitudeI >> 9; //I in range +/-60

  // projection of ACC vector to global Z, with 1G subtructed
  // Math: accZ = A * G / |G| - 1G
  int16_t accZ = (imu.accSmooth[ROLL] * EstG32.V.X + imu.accSmooth[PITCH] * EstG32.V.Y + imu.accSmooth[YAW] * EstG32.V.Z) * invG;

  static int16_t accZoffset = 0;
  if (!f.ARMED)
  {
    accZoffset -= accZoffset >> 3;
    accZoffset += accZ;
  }
  accZ -= accZoffset >> 3;
  applyDeadband(accZ, ACC_Z_DEADBAND);

  static int32_t lastBaroAlt;
  //int16_t baroVel = (alt.EstAlt - lastBaroAlt) * 1000000.0f / dTime;
  int16_t baroVel = (alt.EstAlt - lastBaroAlt) * (1000000 / UPDATE_INTERVAL);
  lastBaroAlt = alt.EstAlt;

  baroVel = constrain(baroVel, -300, 300); // constrain baro velocity +/- 300cm/s
  applyDeadband(baroVel, 10);              // to reduce noise near zero

  // Integrator - velocity, cm/sec
  vel += accZ * ACC_VelScale * dTime;

  // apply Complimentary Filter to keep the calculated velocity based on baro velocity (i.e. near real velocity).
  // By using CF it's possible to correct the drift of integrated accZ (velocity) without loosing the phase, i.e without delay
  vel = vel * 0.985f + baroVel * 0.015f;

  //D
  alt.vario = vel;
  applyDeadband(alt.vario, 5);
  BaroPID -= constrain(conf.pid[PIDALT].D8 * alt.vario >> 4, -150, 150);
#endif
  return 1;
}
#endif //BARO
