//SR04LCD1602.ino

/*----------------HC-SR04 Begin---------------*/
const int CM=1; //1 for CM, 0 for INCH
const int CNT=6;
const int TP[]= {A0,A1,A2,A3,A4,A5};      //Trig_pin
const int EP[]= {2,3,4,5,6,7};    //Echo_pin  
long DS[6];
int i=0;

void Sonar_Setup()
{
	for(i=0;i<CNT;i++)
	{
		pinMode(TP[i],OUTPUT);       // set TP output pin for trigger
		pinMode(EP[i],INPUT);        // set EP input pin for echo
	}
}

void Sonar_Measure()
{
	for(i=0;i<CNT;i++)
	{
		long microseconds = TP_init(TP[i], EP[i]);   // trigger and receive
		DS[i] = Distance(microseconds, CM); // Calculating the distance
	}
}

long Distance(long time, int flag) 
{ 
	long distacne; 
	if(flag) distacne = time /29 / 2 ; 
	// Distance_CM = ((Duration of high level)*(Sonic :340m/s))/2 
	// = ((Duration of high level)*(Sonic :0.034 cm/us))/2 
	// = ((Duration of high level)/(Sonic :29.4 cm/us))/2 
	else distacne = time / 74 / 2; 
	// INC 
	return distacne; 
} 

long TP_init(int tp, int ep) 
{ 
	digitalWrite(tp, LOW); 
	delayMicroseconds(2); 
	digitalWrite(tp, HIGH); 
	// pull the Trig pin to high level for more than 10us impulse 
	delayMicroseconds(10); 
	digitalWrite(tp, LOW); 
	long microseconds = pulseIn(ep,HIGH,20000); 
	// waits for the pin to go HIGH, and returns the length of the pulse in microseconds 
	return microseconds; // return microseconds 
}

/*----------------HC-SR04 End------------*/


/*------------------Servo Start-------------------*/
#include <Servo.h> 

const int servoPin=5;
const int potPin = 0;  // analog pin used to connect the potentiometer
// create servo object to control a servo 
// a maximum of eight servo objects can be created 
Servo servo1;
int val;    	// variable to read the value from the analog pin 
const int maxPos=180;
const int minPos=0;
int pos = minPos;    // variable to store the servo position 
int direct = -1;

void Servo_Setup() 
{ 
	servo1.attach(servoPin);  // attaches the servo on pin to the servo object 
	servo1.write(pos);
	delay(30);
} 

void Servo_Potentio() 
{ 
	val = analogRead(potPin);            // reads the value of the potentiometer (value between 0 and 1023) 
	val = map(val, 0, 1023, 0, 179);     // scale it to use it with the servo (value between 0 and 180) 
	servo1.write(val);                  // sets the servo position according to the scaled value 
	delay(15);                           // waits for the servo to get there 
} 

// Sweep
// by BARRAGAN <http://barraganstudio.com> 
// This example code is in the public domain.

void Servo_Sweep() 
{ 
	for(pos = 0; pos < 180; pos += 1)  // goes from 0 degrees to 180 degrees 
	{                                  // in steps of 1 degree 
		servo1.write(pos);              // tell servo to go to position in variable 'pos' 
		delay(15);                       // waits 15ms for the servo to reach the position 
	} 
	for(pos = 180; pos>=1; pos-=1)     // goes from 180 degrees to 0 degrees 
	{                                
		servo1.write(pos);              // tell servo to go to position in variable 'pos' 
		delay(15);                       // waits 15ms for the servo to reach the position 
	} 
} 

void Servo_Test()
{
	//writeMicroseconds()---输入一个值单位为us,来控制舵机转动到相应角度，输入值为1000时舵机轴转动到逆时针最大位置。输入值为2000时舵机轴转动到顺时针最大位置。为1500时舵机轴在中间位置。
	//servo1.writeMicroseconds(600);
	servo1.write(1);
	delay(2000);
	//servo1.writeMicroseconds(1800);
	servo1.write(90);
	delay(2000);
	//servo1.writeMicroseconds(3000);
	servo1.write(180);
	delay(1000);
}

/*------------------Servo End-------------------*/

char buffer[17];
int comand = 1;   // for incoming serial data

void LCD_Init (void)
{
	LCD_Setup();
	LCD_Clear();
	delay(100);
}

void DS_Show(void)
{
	sprintf(buffer,"1# %4d ",DS[0]);
	sprintf(buffer+8,"2# %4d ",DS[1]);
	Serial.print(buffer);
	LCD_Write_String(0,0,buffer);
	delay(100);
	sprintf(buffer,"pos %4d        ", pos);
	LCD_Write_String(0,1,buffer);
	delay(100);
}

void serialout()
{
	int i=0;
	char buff[8];
	for(;i<CNT;i++)
	{
		sprintf(buff,"%2d:%4d\n",i,DS[i]);
		Serial.print(buff);		
	}
}

void setup(void)
{
	//LCD_Init();
	Sonar_Setup();
	//Buzzer_Setup();
	//Servo_Setup();
	Serial.begin(115200);
}

void loop(void)
{
	// send data only when you receive data:
	if (Serial.available() > 0) {
		// read the incoming byte:
		comand = Serial.read();
	}

	if(comand==1)
	{
		//servo1.write(pos);
		//delay(5);
		Sonar_Measure();
		serialout();
		delay(50);
		//DS_Show();
		//if(pos==maxPos||pos==minPos) 
		//{
		//	direct=-direct;
		//	delay(1000);
		//}
		//Servo_Test();
		//pos+=60*direct;

		//Buzzer_Event();
	}
}