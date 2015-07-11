//Buzzer.ino

#define NTD0 -1
#define NTD1 294
#define NTD2 330
#define NTD3 350
#define NTD4 393
#define NTD5 441
#define NTD6 495
#define NTD7 556

#define NTDL1 147
#define NTDL2 165
#define NTDL3 175
#define NTDL4 196
#define NTDL5 221
#define NTDL6 248
#define NTDL7 278

#define NTDH1 589
#define NTDH2 661
#define NTDH3 700
#define NTDH4 786
#define NTDH5 882
#define NTDH6 990
#define NTDH7 112
//列出全部D调的频率
#define WHOLE 1
#define HALF 0.5
#define QUARTER 0.25
#define EIGHTH 0.25
#define SIXTEENTH 0.625

//列出所有节拍
int tune[]=                 //根据简谱列出各频率
{
  NTD3,NTD3,NTD4,NTD5,
  NTD5,NTD4,NTD3,NTD2,
  NTD1,NTD1,NTD2,NTD3,
  NTD3,NTD2,NTD2,
  NTD3,NTD3,NTD4,NTD5,
  NTD5,NTD4,NTD3,NTD2,
  NTD1,NTD1,NTD2,NTD3,
  NTD2,NTD1,NTD1,
  NTD2,NTD2,NTD3,NTD1,
  NTD2,NTD3,NTD4,NTD3,NTD1,
  NTD2,NTD3,NTD4,NTD3,NTD2,
  NTD1,NTD2,NTDL5,NTD0,
  NTD3,NTD3,NTD4,NTD5,
  NTD5,NTD4,NTD3,NTD4,NTD2,
  NTD1,NTD1,NTD2,NTD3,
  NTD2,NTD1,NTD1
};

float durt[]=                   //根据简谱列出各节拍
{
  1,1,1,1,
  1,1,1,1,
  1,1,1,1,
  1+0.5,0.5,1+1,
  1,1,1,1,
  1,1,1,1,
  1,1,1,1,
  1+0.5,0.5,1+1,
  1,1,1,1,
  1,0.5,0.5,1,1,
  1,0.5,0.5,1,1,
  1,1,1,1,
  1,1,1,1,
  1,1,1,0.5,0.5,
  1,1,1,1,
  1+0.5,0.5,1+1,
};

int nokia[]=
{
  NTD5,NTD4,NTDL6,NTDL7,NTD3,NTD2,NTDL4,NTDL5,NTD2,NTD1,NTDL3,NTD5,NTD1
};

float durtnokia[]=
{
  0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4,0.4
};

int length;
int tonepin=3;   //得用6号接口
int lefpin=13;
int ledstate=LOW;

void Buzzer_Setup()
{
  pinMode(tonepin,OUTPUT);
  pinMode(lefpin,OUTPUT);
  length=sizeof(tune)/sizeof(tune[0]);   //计算长度
}

void Buzzer_Tone()
{
  for(int x=0;x<length;x++)
  {
    tone(tonepin,tune[x]);
    ledstate=!ledstate;
    digitalWrite(lefpin, ledstate);
    delay(400*durt[x]);   //这里用来根据节拍调节延时，500这个指数可以自己调整，在该音乐中，我发现用500比较合适。
    noTone(tonepin);
  }
}

void Buzzer_Nokia()
{
  int len=sizeof(nokia)/sizeof(nokia[0]);
  for(int x=0;x<len;x++)
  {
    tone(tonepin,nokia[x]);
    ledstate=!ledstate;
    digitalWrite(lefpin, ledstate);
    delay(400*durtnokia[x]);   //这里用来根据节拍调节延时，500这个指数可以自己调整，在该音乐中，我发现用500比较合适。
    noTone(tonepin);
  }
}

void Buzzer_Event()
{
  unsigned char i,j,k=2;//定义变量
  while(k>0)
  {
    for(i=0;i<10;i++)//输出一个频率的声音
    {
      ledstate=!ledstate;
      digitalWrite(lefpin, ledstate);
      digitalWrite(tonepin,HIGH);//发声音
      delayMicroseconds(500);//延时1ms
      digitalWrite(tonepin,LOW);//不发声音
      delayMicroseconds(500);//延时ms
    }
    for(i=0;i<20;i++)//输出另一个频率的声音
    {
      ledstate=!ledstate;
      digitalWrite(lefpin, ledstate);
      digitalWrite(tonepin,HIGH);//发声音
      delay(1);//延时2ms
      digitalWrite(tonepin,LOW);//不发声音
      delay(1);//延时2ms
    }
    k--;
  }
}
