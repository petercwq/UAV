#ifndef _VSARDUINO_H_
#define _VSARDUINO_H_
//Board = Arduino Pro or Pro Mini (5V, 16 MHz) w/ ATmega328
#define __AVR_ATmega328P__
#define 
#define ARDUINO 105
#define ARDUINO_MAIN
#define __AVR__
#define F_CPU 16000000L
#define __cplusplus
#define __inline__
#define __asm__(x)
#define __extension__
#define __ATTR_PURE__
#define __ATTR_CONST__
#define __inline__
#define __asm__ 
#define __volatile__

#define __builtin_va_list
#define __builtin_va_start
#define __builtin_va_end
#define __DOXYGEN__
#define __attribute__(x)
#define NOINLINE __attribute__((noinline))
#define prog_void
#define PGM_VOID_P int
            
typedef unsigned char byte;
extern "C" void __cxa_pure_virtual() {;}

void Sonar_Setup();
void Sonar_Measure();
long Distance(long time, int flag);
long TP_init(int tp, int ep);
void Servo_Setup();
void Servo_Potentio();
void Servo_Sweep();
void Servo_Test();
void LCD_Init (void);
void DS_Show(void);
void serialout();
void setup(void);
void loop(void);
void Buzzer_Setup();
void Buzzer_Tone();
void Buzzer_Nokia();
void Buzzer_Event();
void LCD_Command_Write(int command);
void LCD_Data_Write(int dat);
void LCD_SET_XY( int x, int y );
void LCD_Write_Char( int x,int y,int dat);
void LCD_Write_String(int X,int Y,char *s);
void LCD_Write_Long(int X, int Y, long val);
void LCD_Write_Float(int X, int Y, int intwid, int prec, float val);
void LCD_Clear(void);
void LCD_Setup (void);

#include "C:\Program Files (x86)\Arduino\hardware\arduino\variants\standard\pins_arduino.h" 
#include "C:\Program Files (x86)\Arduino\hardware\arduino\cores\arduino\arduino.h"
#include "C:\Users\Weiqing\Documents\UAV\ArduinoDev\SR04LCD1602\SR04LCD1602.ino"
#include "C:\Users\Weiqing\Documents\UAV\ArduinoDev\SR04LCD1602\Buzzer.ino"
#include "C:\Users\Weiqing\Documents\UAV\ArduinoDev\SR04LCD1602\LCD1602.ino"
#endif
