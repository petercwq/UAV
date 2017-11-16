// 本代码版权归Devymex所有，以GNU GENERAL PUBLIC LICENSE V3.0发布
// http://www.gnu.org/licenses/gpl-3.0.en.html
// 相关文档参见作者于知乎专栏发表的原创文章：
// http://zhuanlan.zhihu.com/devymex/20082486

//连线方法
//MPU-UNO
//VCC-VCC
//GND-GND
//SCL-A5
//SDA-A4
//INT-2 (Optional)

#include <Kalman.h>
#include <Wire.h>
#include <Math.h>

float fRad2Deg = 57.295779513f; //将弧度转为角度的乘数
const int MPU = 0x68;           //MPU-6050的I2C地址
const int nValCnt = 7;          //一次读取寄存器的数量

const int nCalibTimes = 1000; //校准时读数的次数
int calibData[nValCnt];       //校准数据

unsigned long nLastTime = 0; //上一次读数的时间
float fLastRoll = 0.0f;      //上一次滤波得到的Roll角
float fLastPitch = 0.0f;     //上一次滤波得到的Pitch角
Kalman kalmanRoll;           //Roll角滤波器
Kalman kalmanPitch;          //Pitch角滤波器

void setup()
{
  Serial.begin(9600);   //初始化串口，指定波特率
  Wire.begin();         //初始化Wire库
  WriteMPUReg(0x6B, 0); //启动MPU6050设备

  Calibration();        //执行校准
  nLastTime = micros(); //记录当前时间
}

void loop()
{
  int readouts[nValCnt];
  ReadAccGyr(readouts); //读出测量值

  float realVals[7];
  Rectify(readouts, realVals); //根据校准的偏移量进行纠正

  //计算加速度向量的模长，均以g为单位
  float fNorm = sqrt(realVals[0] * realVals[0] + realVals[1] * realVals[1] + realVals[2] * realVals[2]);
  float fRoll = GetRoll(realVals, fNorm); //计算Roll角
  if (realVals[1] > 0)
  {
    fRoll = -fRoll;
  }
  float fPitch = GetPitch(realVals, fNorm); //计算Pitch角
  if (realVals[0] < 0)
  {
    fPitch = -fPitch;
  }

  //计算两次测量的时间间隔dt，以秒为单位
  unsigned long nCurTime = micros();
  float dt = (double)(nCurTime - nLastTime) / 1000000.0;
  //对Roll角和Pitch角进行卡尔曼滤波
  float fNewRoll = kalmanRoll.getAngle(fRoll, realVals[4], dt);
  float fNewPitch = kalmanPitch.getAngle(fPitch, realVals[5], dt);
  //跟据滤波值计算角度速
  float fRollRate = (fNewRoll - fLastRoll) / dt;
  float fPitchRate = (fNewPitch - fLastPitch) / dt;

  //更新Roll角和Pitch角
  fLastRoll = fNewRoll;
  fLastPitch = fNewPitch;
  //更新本次测的时间
  nLastTime = nCurTime;

  //向串口打印输出Roll角和Pitch角，运行时在Arduino的串口监视器中查看
  Serial.print("Roll:");
  Serial.print(fNewRoll);
  Serial.print('(');
  Serial.print(fRollRate);
  Serial.print("),\tPitch:");
  Serial.print(fNewPitch);
  Serial.print('(');
  Serial.print(fPitchRate);
  Serial.print(")\n");
  delay(10);
}

//向MPU6050写入一个字节的数据
//指定寄存器地址与一个字节的值
void WriteMPUReg(int nReg, unsigned char nVal)
{
  Wire.beginTransmission(MPU);
  Wire.write(nReg);
  Wire.write(nVal);
  Wire.endTransmission(true);
}

//从MPU6050读出一个字节的数据
//指定寄存器地址，返回读出的值
unsigned char ReadMPUReg(int nReg)
{
  Wire.beginTransmission(MPU);
  Wire.write(nReg);
  Wire.requestFrom(MPU, 1, true);
  Wire.endTransmission(true);
  return Wire.read();
}

//从MPU6050读出加速度计三个分量、温度和三个角速度计
//保存在指定的数组中
void ReadAccGyr(int *pVals)
{
  Wire.beginTransmission(MPU);
  Wire.write(0x3B);
  Wire.requestFrom(MPU, nValCnt * 2, true);
  Wire.endTransmission(true);
  for (long i = 0; i < nValCnt; ++i)
  {
    pVals[i] = Wire.read() << 8 | Wire.read();
  }
}

//对大量读数进行统计，校准平均偏移量
void Calibration()
{
  float valSums[7] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0};
  //先求和
  for (int i = 0; i < nCalibTimes; ++i)
  {
    int mpuVals[nValCnt];
    ReadAccGyr(mpuVals);
    for (int j = 0; j < nValCnt; ++j)
    {
      valSums[j] += mpuVals[j];
    }
  }
  //再求平均
  for (int i = 0; i < nValCnt; ++i)
  {
    calibData[i] = int(valSums[i] / nCalibTimes);
  }
  calibData[2] += 16384; //设芯片Z轴竖直向下，设定静态工作点。
}

//算得Roll角。算法见文档。
float GetRoll(float *pRealVals, float fNorm)
{
  float fNormXZ = sqrt(pRealVals[0] * pRealVals[0] + pRealVals[2] * pRealVals[2]);
  float fCos = fNormXZ / fNorm;
  return acos(fCos) * fRad2Deg;
}

//算得Pitch角。算法见文档。
float GetPitch(float *pRealVals, float fNorm)
{
  float fNormYZ = sqrt(pRealVals[1] * pRealVals[1] + pRealVals[2] * pRealVals[2]);
  float fCos = fNormYZ / fNorm;
  return acos(fCos) * fRad2Deg;
}

//对读数进行纠正，消除偏移，并转换为物理量。公式见文档。
void Rectify(int *pReadout, float *pRealVals)
{
  for (int i = 0; i < 3; ++i)
  {
    pRealVals[i] = (float)(pReadout[i] - calibData[i]) / 16384.0f;
  }
  pRealVals[3] = pReadout[3] / 340.0f + 36.53;
  for (int i = 4; i < 7; ++i)
  {
    pRealVals[i] = (float)(pReadout[i] - calibData[i]) / 131.0f;
  }
}


// #include <Wire.h> //I2C Arduino Library

// #define address 0x1E //0011110b, I2C 7bit address of HMC5883

// void setup(){
//   //Initialize Serial and I2C communications
//   Serial.begin(9600);
//   Wire.begin();
  
//   //Put the HMC5883 IC into the correct operating mode
//   Wire.beginTransmission(address); //open communication with HMC5883
//   Wire.send(0x02); //select mode register
//   Wire.send(0x00); //continuous measurement mode
//   Wire.endTransmission();
// }

// void loop(){
  
//   int x,y,z; //triple axis data

//   //Tell the HMC5883 where to begin reading data
//   Wire.beginTransmission(address);
//   Wire.send(0x03); //select register 3, X MSB register
//   Wire.endTransmission();
  
 
//  //Read data from each axis, 2 registers per axis
//   Wire.requestFrom(address, 6);
//   if(6<=Wire.available()){
//     x = Wire.receive()<<8; //X msb
//     x |= Wire.receive(); //X lsb
//     z = Wire.receive()<<8; //Z msb
//     z |= Wire.receive(); //Z lsb
//     y = Wire.receive()<<8; //Y msb
//     y |= Wire.receive(); //Y lsb
//   }
  
//   //Print out values of each axis
//   Serial.print("x: ");
//   Serial.print(x);
//   Serial.print("  y: ");
//   Serial.print(y);
//   Serial.print("  z: ");
//   Serial.println(z);
  
//   delay(250);
// }
