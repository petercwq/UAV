import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.Serial; 
import controlP5.*; 
import java.io.File; 
import java.lang.*; 
import javax.swing.SwingUtilities; 
import javax.swing.JFileChooser; 
import javax.swing.filechooser.FileFilter; 
import javax.swing.JOptionPane; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.FileOutputStream; 
import java.io.FileInputStream; 
import java.util.*; 
import java.io.FileNotFoundException; 
import java.text.DecimalFormat; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class KV_Team_GUI extends PApplet {

/*
   KV Team OSD GUI V2.3 May 2014
   http://code.google.com/p/rush-osd-development/
  
   Work released under the Creative Commons Attribution NonCommercial ShareAlike 3.0 Unported License.
   http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode
*/


 // serial library
 // controlP5 library

 // for efficient String concatemation
 // required for swing and EDT
// Saving dialogue
 // for our configuration file filter "*.mwi"
 // for message dialogue


 






String KV_OSD_GUI_Version = "2.3";
PImage img_Clear,GUIBackground,RadioPot;
  
int DisplayWindowX = 350;
int DisplayWindowY = 340; 
int WindowAdjX = -84; 
int WindowAdjY = -31;
int WindowShrinkX = 8;
int WindowShrinkY = 48;

int currentCol = 0;
int currentRow = 0;  

ControlP5 controlP5;
ControlP5 SmallcontrolP5;
ControlP5 ScontrolP5;
ControlP5 FontGroupcontrolP5;
ControlP5 GroupcontrolP5;
Textlabel txtlblWhichcom; 
ListBox commListbox;

boolean PortRead = false;
boolean PortWrite = false;
ControlGroup messageBox;
Textlabel MessageText;

// Int variables
String OSname = System.getProperty("os.name");
String LoadPercent = "";
//String CallSign = "";

int init_com = 0;
int commListMax = 0;
int whichKey = -1;  // Variable to hold keystoke values
int inByte = -1;    // Incoming serial data
int[] serialInArray = new int[3];    // Where we'll put what we receive


//int serialCount = 0;                 // A count of how many bytes we receive
int ConfigEEPROM = -1;
int ConfigVALUE = -1;

int windowsX    = 797;        int windowsY    = 459;
int xGraph      = 10;         int yGraph      = 325;
int xObj        = 520;        int yObj        = 293;
int xCompass    = 920;        int yCompass    = 341;
int xLevelObj   = 920;        int yLevelObj   = 80;
int xParam      = 120;        int yParam      = 5;
int xRC         = 690;        int yRC         = 10;
int xMot        = 690;        int yMot        = 155;
int xButton     = 845;        int yButton     = 231; 
int xBox        = 415;        int yBox        = 10;
int XSim        = DisplayWindowX+WindowAdjX;        int YSim        = 288-WindowShrinkY + 85;

// Box locations -------------------------------------------------------------------------
int Col1Width = 165;        int Col2Width = 181;      int Col3Width = 154;      int Col4Width = 110;

int XEEPROM     = 120;        int YEEPROM      = 5;  //hidden do not remove
int XBoard      = 120;        int YBoard       = 5;
int XRSSI       = 120;        int YRSSI        = 45;
int XVolts      = 288;        int YVolts       = 78;
int XAmps       = 640;        int YAmps        = 152;
int XVVolts     = 288;        int YVVolts      = 167;
int XTemp       = 305;        int YTemp        = 5;
int XGPS        = 288;        int YGPS         = 5; 
int XCS         = 5;          int YCS          = 242;
int XOther      = 456;        int YOther       = 60;
int XVolume     = 120;        int YVolume      = 182;
int XPortStat   = 5;          int YPortStat    = 290;
int XControlBox = 5;          int YControlBox  = 320;
int XUnitVideo  = 456;        int YUnitVideo   = 5;
int XADC        = 456;        int YADC         = 167;



String FontFileName = "data/KVTOSD_FMap_v1_Small.mcm";

int activeTab = 1;
int YLocation = 0;

static int MwHeading=0;

String[] ConfigHelp = {
  "EEPROM Loaded",
  
  "RSSI Min",
  "RSSI Max",
  "RSSI WARNING",
  "Input", //MW ADC
  "PWM",
  "PWM Divider",
  "Minim Volt WARNING",
  "Battery Cells",
  "Divider Ratio",
  "Input", //MW ADC  
  "Divider Ratio",
  "Input", //Mw ADC
  
  "", // for Board type do not remove
  
  "GPS",
  "Coords",
  "Heading",
  "Unit",   //unit
  "Video",  // signal
  "Stats",
  "OSD ADC",
  "Blink Frequency",
  "Input",
  "Sensitivity",
  "OffSet High",
  "OffSet Low",
  "V Speed WARNING (m/s)",
  "Dist Max (x100m)",
  "Alt Max (x2m)",
  "Alt Min",
  "Minim Volt WARNING",
  "Pitch Angle WARNING \u00b0",
  
  "Call Sign",
  
  "S_CS0",
  "S_CS1",
  "S_CS2",
  "S_CS3",
  "S_CS4",
  "S_CS5",
  "S_CS6",
  "S_CS7",
  "S_CS8",
  "S_CS9",
};

String[] ConfigNames = {
   "EEPROM Loaded",
  
  "RSSI Min",
  "RSSI Max",
  "RSSI WARNING",
  "Input", //MW ADC
  "PWM",
  "PWM Divider",
  "Minim Volt WARNING",
  "Battery Cells",
  "Divider Ratio",
  "Input", //MW ADC  
  "Divider Ratio",
  "Input", //Mw ADC
  
  "", // for Board type do not remove
  
  "GPS",
  "Coords",
  "Heading",
  "Unit",   //unit
  "Video",  // signal
  "Stats",
  " ",
  "Blink Frequency",
  "Input",
  "Sensitivity",
  "OffSet High",
  "OffSet Low",
  "V Speed WARNING (m/s)",
  "Dist Max (x100m)",
  "Alt Max (x10m)",
  "Alt Min",
  "Minim Volt WARNING",
  "Pitch Angle WARNING \u00b0",
  
  "Call Sign",
  
  "S_CS0",
  "S_CS1",
  "S_CS2",
  "S_CS3",
  "S_CS4",
  "S_CS5",
  "S_CS6",
  "S_CS7",
  "S_CS8",
  "S_CS9",
};


static int CHECKBOXITEMS=0;
int CONFIGITEMS=ConfigNames.length;
static int SIMITEMS=6;
  
int[] ConfigRanges = {
1,   // used for check             0

255, // S_RSSIMIN                   1
255, // S_RSSIMAX                   2
70,  // S_RSSI_ALARM                3
1,   // S_MWRSSI                    4
1,   // S_PWMRSSI                   5
8,   // S_PWMRSSIDIVIDER            6
215, // S_VOLTAGEMIN                7
6,   // S_BATCELLS                  8
110, // S_DIVIDERRATIO              9
1,   // S_MAINVOLTAGE_VBAT          10
110, // S_VIDDIVIDERRATIO           11
1,   // S_VIDVOLTAGE_VBAT           12 
1,   // S_BOARDTYPE                 13
1,   // S_DISPLAYGPS                14
1,   // S_COORDINATES               15
1,   // S_HEADING360                16
1,   // S_UNITSYSTEM                17
1,   // S_VIDEOSIGNALTYPE           18
1,   // S_RESETSTATISTICS           19
1,   // S_ENABLEADC                 20
10,  // S_BLINKINGHZ                21
1,   // S_MWAMPERAGE                22
100,  // S_CURRSENSSENSITIVITY       23
2,   // S_CURRSENSOFFSET_H          24
255, // S_CURRSENSOFFSET_L          25
8,   // S_CLIMB_RATE_ALARM          26
250, // S_VOLUME_DIST_MAX           27
250, // S_VOLUME_ALT_MAX            28
100,  // S_VOLUME_ALT_MIN           29
110, // S_VIDVOLTAGEMIN             30
75,  // S_PITCH_WARNING             31

0,   // S_CALLSIGN                  32


 255,      //Call sign 10 chars 32 to 41
 255,
 255,
 255,
 255,
 255,
 255,
 255,
 255,
 255,

};
/*
int[] ItemLocationPAL = {
  // ROW= Row position on screen (255= no action)
// COL= Column position on screen (255= no action)
// DSPL= Display item on screen
2,   // L_GPS_NUMSATPOSITIONROW LINE02+6
18,  // L_GPS_NUMSATPOSITIONCOL 
1,   // L_GPS_NUMSATPOSITIONDSPL

6,   // L_GPS_DIRECTIONTOHOMEPOSROW LINE03+14
2,   // L_GPS_DIRECTIONTOHOMEPOSCOL
1,   // L_GPS_DIRECTIONTOHOMEPOSDSPL

10,   // L_GPS_DISTANCETOHOMEPOSROW LINE02+24
2,    // L_GPS_DISTANCETOHOMEPOSCOL
1,    // L_GPS_DISTANCETOHOMEPOSDSPL

10,   // L_SPEEDPOSITIONROW LINE03+24
23,   // L_SPEEDPOSITIONCOL
1,    // L_SPEEDPOSITIONDSPL

9,   // L_GPS_ANGLETOHOMEPOSROW LINE04+12
2,   // L_GPS_ANGLETOHOMEPOSCOL
0,   // L_GPS_ANGLETOHOMEPOSDSPL

2,   // L_SENSORPOSITIONROW LINE03+2
24,  // L_SENSORPOSITIONCOL
1,   // L_SENSORPOSITIONDSPL

2,   // L_MODEPOSITIONROW   LINE05+2
8,   // L_MODEPOSITIONCOL
1,   // L_MODEPOSITIONDSPL

3,   // L_MW_HEADINGPOSITIONROW LINE02+19
2,   // L_MW_HEADINGPOSITIONCOL
1,   // L_MW_HEADINGPOSITIONDSPL

2,   // L_MW_HEADINGGRAPHPOSROW LINE02+10
2,   // L_MW_HEADINGGRAPHPOSCOL
1,   // L_MW_HEADINGGRAPHPOSDSPL

6,   // L_MW_ALTITUDEPOSITIONROW LINE08+2
23,  // L_MW_ALTITUDEPOSITIONCOL
1,   // L_MW_ALTITUDEPOSITIONDSPL

8,   // L_CLIMBRATEPOSITIONROW LINE08+24
5,   // L_CLIMBRATEPOSITIONCOL
1,   // L_CLIMBRATEPOSITIONDSPL

6,   // L_HORIZONPOSITIONROW LINE06+8
8,   // L_HORIZONPOSITIONCOL
1,   // L_HORIZONPOSITIONDSPL

255, // L_HORIZONSIDEREFROW
255, // L_HORIZONSIDEREFCOL
1,   // L_HORIZONSIDEREFDSPL

255, // L_HORIZONCENTERREFROW
255, // L_HORIZONCENTERREFCOL
1,   // L_HORIZONCENTERREFDSPL

7,   // L_CURRENTTHROTTLEPOSITIONROW LINE14+22
20,  // L_CURRENTTHROTTLEPOSITIONCOL
1,   // L_CURRENTTHROTTLEPOSITIONDSPL

15,  // L_FLYTIMEPOSITIONROW LINE15+22
14,  // L_FLYTIMEPOSITIONCOL
1,   // L_FLYTIMEPOSITIONDSPL

15,  // L_ONTIMEPOSITIONROW LINE15+22
14,  // L_ONTIMEPOSITIONCOL
1,   // L_ONTIMEPOSITIONDSPL

3,   // L_MOTORARMEDPOSITIONROW LINE14+11
24,  // L_MOTORARMEDPOSITIONCOL
1,   // L_MOTORARMEDPOSITIONDSPL

14,  // L_MW_GPS_LATPOSITIONROW  LINE12+2
2,   // L_MW_GPS_LATPOSITIONCOL
1,   // L_MW_GPS_LATPOSITIONDSPL

15,  // L_MW_GPS_LONPOSITIONROW  LINE12+15
2,   // L_MW_GPS_LONPOSITIONCOL
1,   // L_MW_GPS_LONPOSITIONDSPL

2,   // L_RSSIPOSITIONROW LINE14+2
12,  // L_RSSIPOSITIONCOL
1,   // L_RSSIPOSITIONDSPL

15,  // L_VOLTAGEPOSITIONROW LINE15+3
23,  // L_VOLTAGEPOSITIONCOL
1,   // L_VOLTAGEPOSITIONDSPL

13,  // L_VIDVOLTAGEPOSITIONROW LINE13+3
23,  // L_VIDVOLTAGEPOSITIONCOL
0,   // L_VIDVOLTAGEPOSITIONDSPL

14,  // L_AMPERAGEPOSITIONROW LINE15+10
23,  // L_AMPERAGEPOSITIONCOL
1,   // L_AMPERAGEPOSITIONDSPL

14,  // L_PMETERSUMPOSITIONROW LINE15+16
14,  // L_PMETERSUMPOSITIONCOL
1,   // L_PMETERSUMPOSITIONDSPL

13,  // L_CALLSIGNPOSITIONROW LINE14+10
10,  // L_CALLSIGNPOSITIONCOL
1,   // L_CALLSIGNPOSITIONDSPL
};

int[] ItemLocationNTSC = {
2,   // L_GPS_NUMSATPOSITIONROW LINE02+6
18,  // L_GPS_NUMSATPOSITIONCOL 
1,   // L_GPS_NUMSATPOSITIONDSPL

6,   // L_GPS_DIRECTIONTOHOMEPOSROW LINE03+14
2,   // L_GPS_DIRECTIONTOHOMEPOSCOL
1,   // L_GPS_DIRECTIONTOHOMEPOSDSPL

10,  // L_GPS_DISTANCETOHOMEPOSROW LINE02+24
2,   // L_GPS_DISTANCETOHOMEPOSCOL
1,   // L_GPS_DISTANCETOHOMEPOSDSPL

10,  // L_SPEEDPOSITIONROW LINE03+24
23,  // L_SPEEDPOSITIONCOL 
1,   // L_SPEEDPOSITIONDSPL

9,   // L_GPS_ANGLETOHOMEPOSROW LINE04+12
2,   // L_GPS_ANGLETOHOMEPOSCOL
0,   // L_GPS_ANGLETOHOMEPOSDSPL

2,   // L_SENSORPOSITIONROW LINE03+2
24,  // L_SENSORPOSITIONCOL
1,   // L_SENSORPOSITIONDSPL

2,   // L_MODEPOSITIONROW   LINE05+2
8,   // L_MODEPOSITIONCOL
1,   // L_MODEPOSITIONDSPL

3,   // L_MW_HEADINGPOSITIONROW LINE02+19
2,   // L_MW_HEADINGPOSITIONCOL
1,   // L_MW_HEADINGPOSITIONDSPL

2,   // L_MW_HEADINGGRAPHPOSROW LINE02+10
2,   // L_MW_HEADINGGRAPHPOSCOL
1,   // L_MW_HEADINGGRAPHPOSDSPL

6,   // L_MW_ALTITUDEPOSITIONROW LINE08+2
23,  // L_MW_ALTITUDEPOSITIONCOL
1,   // L_MW_ALTITUDEPOSITIONDSPL

8,   // L_CLIMBRATEPOSITIONROW LINE08+24
5,   // L_CLIMBRATEPOSITIONCOL
1,   // L_CLIMBRATEPOSITIONDSPL

6,   // L_HORIZONPOSITIONROW LINE06+8
8,   // L_HORIZONPOSITIONCOL
1,   // L_HORIZONPOSITIONDSPL

255, // L_HORIZONSIDEREFROW
255, // L_HORIZONSIDEREFCOL
1,   // L_HORIZONSIDEREFDSPL

255, // L_HORIZONCENTERREFROW
255, // L_HORIZONCENTERREFCOL
1,   // L_HORIZONCENTERREFDSPL

7,   // L_CURRENTTHROTTLEPOSITIONROW LINE14+22
20,  // L_CURRENTTHROTTLEPOSITIONCOL
1,   // L_CURRENTTHROTTLEPOSITIONDSPL

13,  // L_FLYTIMEPOSITIONROW LINE15+22
14,  // L_FLYTIMEPOSITIONCOL
1,   // L_FLYTIMEPOSITIONDSPL

13,  // L_ONTIMEPOSITIONROW LINE15+22
14,  // L_ONTIMEPOSITIONCOL
1,   // L_ONTIMEPOSITIONDSPL

3,   // L_MOTORARMEDPOSITIONROW LINE14+11
24,  // L_MOTORARMEDPOSITIONCOL
1,   // L_MOTORARMEDPOSITIONDSPL

12,  // L_MW_GPS_LATPOSITIONROW  LINE12+2
2,   // L_MW_GPS_LATPOSITIONCOL
1,   // L_MW_GPS_LATPOSITIONDSPL

13,  // L_MW_GPS_LONPOSITIONROW  LINE12+15
2,   // L_MW_GPS_LONPOSITIONCOL
1,   // L_MW_GPS_LONPOSITIONDSPL

2,   // L_RSSIPOSITIONROW LINE14+2
12,  // L_RSSIPOSITIONCOL
1,   // L_RSSIPOSITIONDSPL

13,  // L_VOLTAGEPOSITIONROW LINE15+3
23,  // L_VOLTAGEPOSITIONCOL
1,   // L_VOLTAGEPOSITIONDSPL

11,  // L_VIDVOLTAGEPOSITIONROW LINE13+3
23,  // L_VIDVOLTAGEPOSITIONCOL
0,   // L_VIDVOLTAGEPOSITIONDSPL

12,  // L_AMPERAGEPOSITIONROW LINE15+10
23,  // L_AMPERAGEPOSITIONCOL
1,   // L_AMPERAGEPOSITIONDSPL

12,  // L_PMETERSUMPOSITIONROW LINE15+16
14,  // L_PMETERSUMPOSITIONCOL
1,   // L_PMETERSUMPOSITIONDSPL

11,  // L_CALLSIGNPOSITIONROW LINE14+10
10,  // L_CALLSIGNPOSITIONCOL
1,   // L_CALLSIGNPOSITIONDSPL
};
 */ 

PFont font8,font9,font10,font11,font12,font15;

// Colors------------------------------------------------------------------------------------------------------------------
int yellow_ = color(200, 200, 20), green_ = color(30, 100, 30), red_ = color(120, 30, 30), blue_ = color(25, 50, 80),
grey_ = color(30, 30, 30);
// Colors------------------------------------------------------------------------------------------------------------------

// Textlabels -------------------------------------------------------------------------------------------------------------
Textlabel txtlblconfItem[] = new Textlabel[CONFIGITEMS] ;
Textlabel txtlblSimItem[] = new Textlabel[SIMITEMS] ;
Textlabel FileUploadText, TXText, RXText, Links;
// Textlabels -------------------------------------------------------------------------------------------------------------

// Buttons-----------------------------------------------------------------------------------------------------------------
Button buttonIMPORT,buttonSAVE,buttonREAD,buttonRESET,buttonWRITE,buttonRESTART, buttonOSDHome, buttonOSDWiki, buttonMWiiForum, buttonSupport;
// Buttons------------------------------------------------------------------------------------------------------------------

// Toggles------------------------------------------------------------------------------------------------------------------
Toggle toggleConfItem[] = new Toggle[CONFIGITEMS] ;
// Toggles------------------------------------------------------------------------------------------------------------------    

// Checkboxes---------------------------------------------------------------------------------------------------------------
CheckBox checkboxConfItem[] = new CheckBox[CONFIGITEMS] ;
// Checkboxes---------------------------------------------------------------------------------------------------------------

// Toggles------------------------------------------------------------------------------------------------------------------    
RadioButton RadioButtonConfItem[] = new RadioButton[CONFIGITEMS] ;
RadioButton R_PortStat;
// Toggles------------------------------------------------------------------------------------------------------------------    

// Number boxes-------------------------------------------------------------------------------------------------------------
Numberbox confItem[] = new Numberbox[CONFIGITEMS] ;
// Number boxes-------------------------------------------------------------------------------------------------------------

Group MGUploadF,
  G_EEPROM,
  G_RSSI,
  G_Voltage,
  G_Amperage,
  G_VVoltage,
  G_Board,
  G_GPS,
  G_UV,
  G_Other,
  G_ADC,
  G_UnitVideo,
  G_Volume,
  G_CallSign,
  G_PortStatus  
  ;
 
  public controlP5.Controller hideLabel(controlP5.Controller c) {
  c.setLabel("");
  c.setLabelVisible(false);
  return c;
}

//*****************************************************************************************************//

public void setup() {
  size(windowsX,windowsY);
  GUIBackground = loadImage ("Background.png");
  font8 = createFont("Arial bold",8,false);
  font9 = createFont("Arial bold",10,false);
  font10 = createFont("Arial bold",11,false);
  font11 = createFont("Arial bold",11,false);
  font12 = createFont("Arial bold",12,false);
  font15 = createFont("Arial bold",15,false);

  controlP5 = new ControlP5(this); // initialize the GUI controls
  controlP5.setControlFont(font10);
  SmallcontrolP5 = new ControlP5(this); // initialize the GUI controls
  SmallcontrolP5.setControlFont(font9); 
  ScontrolP5 = new ControlP5(this); // initialize the GUI controls
  ScontrolP5.setControlFont(font10);  
  GroupcontrolP5 = new ControlP5(this); // initialize the GUI controls
  GroupcontrolP5.setControlFont(font10);
  FontGroupcontrolP5 = new ControlP5(this); // initialize the GUI controls
  FontGroupcontrolP5.setControlFont(font10);
  
  SetupGroups();

  commListbox = controlP5.addListBox("portComList",5,100,110,260); // make a listbox and populate it with the available comm ports
  commListbox.setItemHeight(15);
  commListbox.setBarHeight(15);

  commListbox.captionLabel().set("PORT COM");
  commListbox.setColorBackground(red_);
  for(int i=0;i<Serial.list().length;i++) {
    String pn = shortifyPortName(Serial.list()[i], 13);
    if (pn.length() >0 ) commListbox.addItem(pn,i); // addItem(name,value)
    commListMax = i;
  }
  commListbox.addItem("Close Comm",++commListMax); // addItem(name,value)
  
  // text label for which comm port selected
  txtlblWhichcom   = controlP5.addTextlabel("txtlblWhichcom","No Port Selected",5,65); // textlabel(name,text,x,y)
  // Links Label
  Links            = controlP5.addTextlabel("LinkInf"," Handy Buttons >") .setPosition(XVolume, YVolume+74);    
  
  buttonSAVE       = controlP5.addButton("bSAVE",1,15,45,40,19); buttonSAVE.setLabel("SAVE"); buttonSAVE.setColorBackground(red_);
  buttonIMPORT     = controlP5.addButton("bIMPORT",1,65,45,40,19); buttonIMPORT.setLabel("LOAD"); buttonIMPORT.setColorBackground(red_);  
  buttonREAD       = controlP5.addButton("READ",1,XControlBox+34,YControlBox+25,37,15);buttonREAD.setColorBackground(red_);
  buttonRESET      = controlP5.addButton("RESET",1,XControlBox+32,YControlBox+50,40,15);buttonRESET.setColorBackground(red_);
  buttonWRITE      = controlP5.addButton("WRITE",1,XControlBox+32,YControlBox+75,42,15);buttonWRITE.setColorBackground(red_);
  buttonRESTART    = controlP5.addButton("RESTART",1,XControlBox+25,YControlBox+100,55,16);buttonRESTART.setColorBackground(red_); 
  buttonOSDHome    = controlP5.addButton("OSDHome", 1, XVolume+100, YVolume+74, 80, 15).setColorBackground(blue_).setLabel("      Home");//.setColorLabel(yellow_);
  buttonOSDWiki    = controlP5.addButton("OSDWiki", 1, XVolume+190, YVolume+74, 80, 15).setColorBackground(blue_).setLabel("        Wiki");//.setColorLabel(yellow_);
  buttonMWiiForum  = controlP5.addButton("MWiiForum",1, XVolume+280, YVolume+74, 80, 15).setColorBackground(blue_).setLabel(" MWii Forum");//.setColorLabel(yellow_);
  buttonSupport    = controlP5.addButton("Support",1, XVolume+370, YVolume+74, 80, 15).setColorBackground(blue_).setLabel("    Support");//.setColorLabel(yellow_);
  

// EEPROM---------------------------------------------------------------------------

CreateItem(GetSetting("S_CHECK_"), 5, 0, G_EEPROM);

// RSSI  ---------------------------------------------------------------------------
CreateItem(GetSetting("S_MWRSSI"),  5,0*17, G_RSSI);
BuildRadioButton(GetSetting("S_MWRSSI"),  5,0*17, G_RSSI, "ADC","MWii");
CreateItem(GetSetting("S_PWMRSSI"),  5,1*17, G_RSSI);
BuildRadioButton(GetSetting("S_PWMRSSI"),  5,1*17, G_RSSI, "Off","On");
CreateItem(GetSetting("S_PWMRSSIDIVIDER"),  5,2*17, G_RSSI);
CreateItem(GetSetting("S_RSSIMIN"), 5,3*17, G_RSSI);
CreateItem(GetSetting("S_RSSIMAX"), 5,4*17, G_RSSI);
RSSIWarning(GetSetting("S_RSSI_ALARM"), 5,5*17, G_RSSI);

// Voltage  ------------------------------------------------------------------------
CreateItem(GetSetting("S_MAINVOLTAGE_VBAT"), 5,0*17, G_Voltage);
BuildRadioButton(GetSetting("S_MAINVOLTAGE_VBAT"),  5,0*17, G_Voltage, "ADC","MWii");
BattWarningItem(GetSetting("S_VOLTAGEMIN"), 5,1*17, G_Voltage);
BattCellItem(GetSetting("S_BATCELLS"), 5,2*17, G_Voltage);
CreateItem(GetSetting("S_DIVIDERRATIO"), 5,3*17, G_Voltage);

// Video Voltage  -------------------------------------------------------------------
CreateItem(GetSetting("S_VIDVOLTAGE_VBAT"),  5,0*17, G_VVoltage);
BuildRadioButton(GetSetting("S_VIDVOLTAGE_VBAT"),  5,0, G_VVoltage, "ADC","MWii");
BattWarningItem(GetSetting("S_VIDVOLTAGEMIN"), 5,1*17, G_VVoltage);
CreateItem(GetSetting("S_VIDDIVIDERRATIO"),  5,2*17, G_VVoltage);

//  Board ---------------------------------------------------------------------------
CreateItem(GetSetting("S_BOARDTYPE"),  5,0, G_Board);
BuildRadioButton(GetSetting("S_BOARDTYPE"),  5,0, G_Board, "Rush","Minim");

//  GPS  ----------------------------------------------------------------------------
CreateItem(GetSetting("S_DISPLAYGPS"), 5,0, G_GPS);
BuildRadioButton(GetSetting("S_DISPLAYGPS"),  5,0, G_GPS, "Off","On");
CreateItem(GetSetting("S_COORDINATES"),  5,1*17, G_GPS);
BuildRadioButton(GetSetting("S_COORDINATES"),  5,1*17, G_GPS, "Off","On");
CreateItem(GetSetting("S_HEADING360"),  5,2*17, G_GPS);
BuildRadioButton(GetSetting("S_HEADING360"),  5,2*17, G_GPS, "180\u00b0","360\u00b0");

//  Unit & Video  ------------------------------------------------------------------
CreateItem(GetSetting("S_UNITSYSTEM"),  5,0, G_UnitVideo);
BuildRadioButton(GetSetting("S_UNITSYSTEM"),  5,0, G_UnitVideo, "Metric","Imperial");
CreateItem(GetSetting("S_VIDEOSIGNALTYPE"),  5,1*17, G_UnitVideo);
BuildRadioButton(GetSetting("S_VIDEOSIGNALTYPE"),  5,1*17, G_UnitVideo, "NTSC","PAL");

//  Other  -------------------------------------------------------------------------
CreateItem(GetSetting("S_RESETSTATISTICS"),  5,0*17, G_Other);
BuildRadioButton(GetSetting("S_RESETSTATISTICS"),  5,0*17, G_Other, "Maintain","Reset");
BlinkFreqItem(GetSetting("S_BLINKINGHZ"),  5,2*17, G_Other);
DescendWarningItem(GetSetting("S_CLIMB_RATE_ALARM"),  5,3*17, G_Other);
PitchWarningItem(GetSetting("S_PITCH_WARNING"),  5,4*17, G_Other);

// OSD ADC\u00b4s   --------------------------------------------------------------------------
CreateItem(GetSetting("S_ENABLEADC"),  53,1*17, G_ADC);
BuildRadioButton(GetSetting("S_ENABLEADC"),  53,1*17, G_ADC, "Off","On");

// Amperage  ------------------------------------------------------------------------
CreateItem(GetSetting("S_MWAMPERAGE"),  5,0, G_Amperage);
BuildRadioButton(GetSetting("S_MWAMPERAGE"),  5,0, G_Amperage, "ADC","MWii");
CreateItem(GetSetting("S_CURRSENSSENSITIVITY"),  5,1*17, G_Amperage);
CreateItem(GetSetting("S_CURRSENSOFFSET_H"),  5,2*17, G_Amperage);
CreateItem(GetSetting("S_CURRSENSOFFSET_L"),  5,3*17, G_Amperage);

// Volume Flight  -------------------------------------------------------------------
VolumeFlightDist(GetSetting("S_VOLUME_DIST_MAX"),  5,0*17, G_Volume);
VolumeFlightAltMax(GetSetting("S_VOLUME_ALT_MAX"),  5,1*17, G_Volume);
VolumeFlightAltMin(GetSetting("S_VOLUME_ALT_MIN"),  5,2*17, G_Volume);

// Call Sign ------------------------------------------------------------------------
CreateItem(GetSetting("S_CALLSIGN"),9,0,G_CallSign);
BuildRadioButton(GetSetting("S_CALLSIGN"),10,2*17,G_CallSign, "","");
controlP5.addTextfield("CallSign")
     .setPosition(15,0*16)
     .setSize(78,13)
     .setFont(font10)
     .setAutoClear(false)
     .setGroup(G_CallSign)
     ;
 controlP5.addTextlabel("","")
 .setGroup(G_CallSign);
 CreateCS(GetSetting("S_CS0"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS1"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS2"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS3"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS4"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS5"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS6"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS7"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS8"),  0,0, G_CallSign);
 CreateCS(GetSetting("S_CS9"),  0,0, G_CallSign);

//******************************************* Config Items ********************************************//

  for(int i=0;i<CONFIGITEMS;i++) {
    if (ConfigRanges[i] == 0) {
      toggleConfItem[i].hide();
      confItem[i].hide();
    }
    if (ConfigRanges[i] > 1) {
      try{
      toggleConfItem[i].hide();
      }catch(Exception e) {
      }finally {
   }  	
 }
      
    if (ConfigRanges[i] == 1){
      confItem[i].hide();  
    }
  }
  for (int txTimes = 0; txTimes<255; txTimes++) {
    inBuf[txTimes] = 0;
  }

  Font_Editor_setup();
   SimSetup();
  img_Clear = LoadFont(FontFileName);
  LoadConfig();
  
}

public controlP5.Controller hideCheckbox(controlP5.Controller c) {
  c.hide();
  c.setLabelVisible(false);
  return c;
}

public controlP5.Controller CheckboxVisable(controlP5.Controller c) {
  c.isVisible(); 

  c.setLabelVisible(false);
  return c;
}

public void BuildRadioButton(int ItemIndex, int XLoction, int YLocation,Group inGroup, String Cap1, String Cap2){   
     RadioButtonConfItem[ItemIndex] = controlP5.addRadioButton("RadioButton"+ItemIndex)
         .setPosition(XLoction,YLocation+3)
         .setSize(10,10)
         .setNoneSelectedAllowed(false) 
         .setItemsPerRow(2)
         .setSpacingColumn(PApplet.parseInt(textWidth(Cap1))+10)
         .addItem("First"+ItemIndex,0)
         .addItem("Second"+ItemIndex,1)
         .toUpperCase(false)
         //.hideLabels() 
         ;
     RadioButtonConfItem[ItemIndex].setGroup(inGroup);
     RadioButtonConfItem[ItemIndex].getItem(0).setCaptionLabel(Cap1);
     RadioButtonConfItem[ItemIndex].getItem(1).setCaptionLabel(Cap2 + "    " + ConfigNames[ItemIndex]);
    
     toggleConfItem[ItemIndex].hide();
     txtlblconfItem[ItemIndex].hide();    
}

public void CreateCS(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setMax(255);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
     confItem[ItemIndex].hide();
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].hide();
}

public void CreateItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(blue_);
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void BlinkFreqItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(blue_);
     confItem[ItemIndex].setMin(1);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void PitchWarningItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(15);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
} 


public void BattCellItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(blue_);
     confItem[ItemIndex].setMin(2);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void BattWarningItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(7);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void RSSIWarning(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(10);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void DescendWarningItem(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void VolumeFlightDist(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void VolumeFlightAltMax(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void VolumeFlightAltMin(int ItemIndex, int XLoction, int YLocation, Group inGroup){
  //numberbox
     confItem[ItemIndex] = (controlP5.Numberbox) hideLabel(controlP5.addNumberbox("configItem"+ItemIndex,0,XLoction,YLocation,35,14));
     confItem[ItemIndex].setColorBackground(red_);
     confItem[ItemIndex].setMin(0);
     confItem[ItemIndex].setDirection(Controller.HORIZONTAL);
     confItem[ItemIndex].setMax(ConfigRanges[ItemIndex]);
     confItem[ItemIndex].setDecimalPrecision(0);
     confItem[ItemIndex].setGroup(inGroup);
  //Toggle
     toggleConfItem[ItemIndex] = (controlP5.Toggle) hideLabel(controlP5.addToggle("toggleValue"+ItemIndex));
     toggleConfItem[ItemIndex].setPosition(XLoction,YLocation+3);
     toggleConfItem[ItemIndex].setSize(35,10);
     toggleConfItem[ItemIndex].setMode(ControlP5.SWITCH);
     toggleConfItem[ItemIndex].setGroup(inGroup);
  //TextLabel
     txtlblconfItem[ItemIndex] = controlP5.addTextlabel("txtlblconfItem"+ItemIndex,ConfigNames[ItemIndex],XLoction+40,YLocation);
     txtlblconfItem[ItemIndex].setGroup(inGroup);
     controlP5.getTooltip().register("txtlblconfItem"+ItemIndex,ConfigHelp[ItemIndex]);
}

public void MakePorts(){
  
  if (PortWrite){  
       TXText.setColorValue(color(255,10,0));
  }
  else
  {
    TXText.setColorValue(color(100,10,0));
  }
  if (PortRead){  
    RXText.setColorValue(color(0,240,0));
  }
   else
  {
    RXText.setColorValue(color(0,100,0));
  }
}

public void draw() {
  time=millis();
  if ((init_com==1)  && (toggleMSP_Data == true)) {
    //time2 = time;
    PortRead = true;
    MakePorts();
    MWData_Com();
    if (!FontMode) PortRead = false;
    
  }
  if ((SendSim ==1) && (ClosePort == false)){
    if ((init_com==1)  && (time-time5 >5000) && (toggleMSP_Data == false) && (!FontMode)){
      if(ClosePort) return;
      time5 = time;
       
      if (init_com==1){
        SendCommand(MSP_BOXIDS);
      }
    }
    if ((init_com==1)  && (time-time4 >200) && (!FontMode)){
      if(ClosePort) return;
      time4 = time; 

      if (init_com==1)SendCommand(MSP_ANALOG);
      if (init_com==1)SendCommand(MSP_STATUS);
      if (init_com==1)SendCommand(MSP_RC);
      if (init_com==1)SendCommand(MSP_ALTITUDE);
      if (init_com==1)SendCommand(MSP_RAW_GPS);
      if (init_com==1)SendCommand(MSP_COMP_GPS); 
    }
      if ((init_com==1)  && (time-time1 >40) && (!FontMode)){
      if(ClosePort) return;
      time1 = time; 
      PortWrite = !PortWrite;
      
      if (init_com==1)SendCommand(MSP_ATTITUDE);
    }
  }
  else
  {
      if (!FontMode) PortWrite = false; 
  }
      if ((FontMode) && (time-time2 >100)){
    SendChar();
   }   
    
  MakePorts();   

  background(80);
  
  // ----------------------------------------------------------------------------------------------------------------------
  // Draw background control boxes
  // ----------------------------------------------------------------------------------------------------------------------

  // Coltrol Box
  fill(80); strokeWeight(2);stroke(100); rectMode(CORNERS); rect(XControlBox,YControlBox, XControlBox+108, YControlBox+134); 
  textFont(font12); fill(255, 255, 255); text("OSD Controls",XControlBox + 15,YControlBox + 15); 
 // if (activeTab == 1) {}
  fill(60, 40, 40);
  strokeWeight(3);stroke(0);
  rectMode(CORNERS);
  rect(5,5,113,40);
  textFont(font12);
  // version
  fill(255, 255, 255);
  text("  KV Team OSD",10,19);
  text(" GUI Version",10,35);
  text(KV_OSD_GUI_Version, 88, 35);
  fill(0, 0, 0);
  strokeWeight(3);stroke(0);
  rectMode(CORNERS);
  image(GUIBackground,DisplayWindowX+WindowAdjX, DisplayWindowY+WindowAdjY);
  MatchConfigs();
  MakePorts();

  if ((ClosePort ==true)&& (PortWrite == false)){ 
    ClosePort();
  }
}

public int GetSetting(String test){
  int TheSetting = 0;
  for (int i=0; i<Settings.values().length; i++) 
  if (Settings.valueOf(test) == Settings.values()[i]){ 
      TheSetting = Settings.values()[i].ordinal();
  }
  return TheSetting;
}

public void BuildCallSign(){
  String CallSText = "";
  for (int i=0; i<10; i++){ 
    if (PApplet.parseInt(confItem[GetSetting("S_CS0")+i].getValue())>0){
    CallSText+=PApplet.parseChar(PApplet.parseInt(confItem[GetSetting("S_CS0")+i].getValue()));
    }
  }
  controlP5.get(Textfield.class,"CallSign").setText(CallSText);
}  

public void CheckCallSign() {
  // automatically receives results from controller input
  String CallSText = controlP5.get(Textfield.class,"CallSign").getText().toUpperCase();
  controlP5.get(Textfield.class,"CallSign").setText(CallSText);
    if (CallSText.length()  >10){
      controlP5.get(Textfield.class,"CallSign").setText(CallSText.substring(0, 10));
      CallSText = controlP5.get(Textfield.class,"CallSign").getText();
    } 
    for (int i=0; i<10; i++){ 
    confItem[GetSetting("S_CS0")+i].setValue(0);
    }
    for (int i=0; i<CallSText.length(); i++){ 
      confItem[(GetSetting("S_CS0"))+i].setValue(PApplet.parseInt(CallSText.charAt(i)));
    }
}

public void MatchConfigs()

{
 for(int i=0;i<CONFIGITEMS;i++) {
   try{ 
       if (RadioButtonConfItem[i].isVisible()){
          confItem[i].setValue(PApplet.parseInt(RadioButtonConfItem[i].getValue()));
       }
        }catch(Exception e) {}finally {}
    
  
     if  (toggleConfItem[i].isVisible()){
       if (PApplet.parseInt(toggleConfItem[i].getValue())== 1){
       confItem[i].setValue(1);
     }
     else{ 
       confItem[i].setValue(0);
      }
    }
  }
}

//***************************************************************//

// controls comport list click
public void controlEvent(ControlEvent theEvent) {
  
  try{
  if (theEvent.isGroup())
    if (theEvent.name()=="portComList")
      InitSerial(theEvent.group().value()); // initialize the serial port selected
  }catch(Exception e){
    System.out.println("error with Port");
  }
  
  
if (theEvent.name()=="CallSign"){
  CheckCallSign();
}
      
  try{
    if ((theEvent.getController().getName().substring(0, 7).equals("CharPix")) && (theEvent.getController().isMousePressed())) {
        int ColorCheck = PApplet.parseInt(theEvent.getController().value());
        curPixel = theEvent.controller().id();
    }
    if ((theEvent.getController().getName().substring(0, 7).equals("CharMap")) && (theEvent.getController().isMousePressed())) {
      curChar = theEvent.controller().id();    
    }
   } catch(ClassCastException e){}
     catch(StringIndexOutOfBoundsException se){}
      
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////// BEGIN FILE OPS//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//save the content of the model to a file
public void bSAVE() {
  updateModel();
  SwingUtilities.invokeLater(new Runnable(){
    public void run() {
     final JFileChooser fc = new JFileChooser(dataPath("")) {

        private static final long serialVersionUID = 7919427933588163126L;

        public void approveSelection() {
            File f = getSelectedFile();
            if (f.exists() && getDialogType() == SAVE_DIALOG) {
                int result = JOptionPane.showConfirmDialog(this,
                        "The file exists, overwrite?", "Existing file",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
                default:
                    return;
                }
            }
        super.approveSelection();
        }
    };

      fc.setDialogType(JFileChooser.SAVE_DIALOG);
      fc.setFileFilter(new MwiFileFilter());
      int returnVal = fc.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        String filePath = file.getPath();
        if(!filePath.toLowerCase().endsWith(".osd")){
          file = new File(filePath + ".osd");
        }

        
        FileOutputStream out =null;
        String error = null;
        try{
          out = new FileOutputStream(file) ;
          MWI.conf.storeToXML(out, "KVTOSD Configuration File  " + new  Date().toString());
          JOptionPane.showMessageDialog(null,new StringBuffer().append("configuration saved : ").append(file.toURI()) );
           }catch(FileNotFoundException e){
         
          error = e.getCause().toString();
           }catch( IOException ioe){
                /*failed to write the file*/
                ioe.printStackTrace();
                error = ioe.getCause().toString();
           }finally{
             
          if (out!=null){
            try{
              out.close();
            }catch( IOException ioe){/*failed to close the file*/error = ioe.getCause().toString();}
          }
          if (error !=null){
                  JOptionPane.showMessageDialog(null, new StringBuffer().append("error : ").append(error) );
           }
         }
       }
     }
   }
 );
}

public void updateModel(){
  for(int j=0;j<ConfigNames.length;j++) {
         MWI.setProperty(ConfigNames[j],String.valueOf(confItem[j].value()));
  }
}

public void updateView(){
  for(int j=0; j<ConfigNames.length; j++) {
    
    if(j >= CONFIGITEMS)
    return;
  int value = PApplet.parseInt(MWI.getProperty(ConfigNames[j]));
  confItem[j].setValue(value);
  if (j == CONFIGITEMS-1){
  }  
  if (value >0){
    toggleConfItem[j].setValue(1);
    }
    else {
    toggleConfItem[j].setValue(0);
  }

  try{
    switch(value) {
    case(0):
      RadioButtonConfItem[j].activate(0);
      break;
    case(1):
      RadioButtonConfItem[j].activate(1);
      break;
    }
  }
  catch(Exception e) {}finally {}
  }
  
  BuildCallSign(); 
}

public class MwiFileFilter extends FileFilter {
  public boolean accept(File f) {
    if(f != null) {
      if(f.isDirectory()) {
        return true;
      }
      String extension = getExtension(f);
      if("osd".equals(extension)) {
        return true;
      }
    }
    return false;
 }
  
  public String getExtension(File f) {
    if(f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');
      if(i>0 && i<filename.length()-1) {
        return filename.substring(i+1).toLowerCase();
      }
    }
    return null;
  } 

  public String getDescription() {
    return "*.osd KVT_OSD configuration file";
  }   
}

//*****************************************************************************************************//

// import the content of a file into the model
public void bIMPORT(){
  SwingUtilities.invokeLater(new Runnable(){
    public void run(){
      final JFileChooser fc = new JFileChooser(dataPath(""));
      fc.setDialogType(JFileChooser.SAVE_DIALOG);
      fc.setFileFilter(new MwiFileFilter());
      int returnVal = fc.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        FileInputStream in = null;
        boolean completed = false;
        String error = null;
        try{
          in = new FileInputStream(file) ;
          MWI.conf.loadFromXML(in); 
          JOptionPane.showMessageDialog(null,new StringBuffer().append("configuration loaded : ").append(file.toURI()) );
          completed  = true;
          
        }catch(FileNotFoundException e){
                error = e.getCause().toString();

        }catch( IOException ioe){/*failed to read the file*/
                ioe.printStackTrace();
                error = ioe.getCause().toString();
        }finally{
          if (!completed){
                 // MWI.conf.clear();
                 // or we can set the properties with view values, sort of 'nothing happens'
                 updateModel();
          }
          updateView();
          if (in!=null){
            try{
              in.close();
            }catch( IOException ioe){/*failed to close the file*/}
          }
          
          if (error !=null){
                  JOptionPane.showMessageDialog(null, new StringBuffer().append("error : ").append(error) );
          }
        }
      }
    }
  }
 );
}

//*****************************************************************************************************//
//  our model 
static class MWI {
  private static Properties conf = new Properties();
  public static void setProperty(String key ,String value ){
    conf.setProperty( key,value );
  }
  public static String getProperty(String key ){
    return conf.getProperty( key,"0");
  }
  public static void clear( ){
    conf= null; // help gc
    conf = new Properties();
  }
}

public void updateConfig(){
  String error = null;
  FileOutputStream out =null;
  ConfigClass.setProperty("StartFontFile",FontFileName);
  File file = new File(dataPath("GUI.Config"));
  try{
    out = new FileOutputStream(file) ;
    ConfigClass.conf.storeToXML(out, "KV_Team_OSD GUI Configuration File  " + new  Date().toString());
   }catch(FileNotFoundException e){
      error = e.getCause().toString();
   }catch( IOException ioe){
        /*failed to write the file*/
        ioe.printStackTrace();
        error = ioe.getCause().toString();
      }finally{
        if (out!=null){
          try{
            out.close();
            }catch( IOException ioe){/*failed to close the file*/error = ioe.getCause().toString();}
            }
            if (error !=null){
            JOptionPane.showMessageDialog(null, new StringBuffer().append("error : ").append(error) );
    }
  }
}

public void LoadConfig(){
  String error = null;
  FileInputStream in =null;  
  try{
    in = new FileInputStream(dataPath("GUI.Config"));
  }catch(FileNotFoundException e){
    System.out.println("Configuration Failed- Creating Default");
    updateConfig();
    }catch( IOException ioe){
      /*failed to write the file*/
      ioe.printStackTrace();
      error = ioe.getCause().toString();
    }//finally{
      if (in!=null){
        try{
          ConfigClass.conf.loadFromXML(in); 
          FontFileName = ConfigClass.getProperty("StartFontFile");
          img_Clear = LoadFont(FontFileName);
          System.out.println("Configuration Successful");
          in.close();
          }catch( IOException ioe){/*failed to close the file*/error = ioe.getCause().toString();}
          }
          if (error !=null){
          JOptionPane.showMessageDialog(null, new StringBuffer().append("error : ").append(error) );
  }
}

//***************************************************************//
//  our configuration 
static class ConfigClass {
  private static Properties conf = new Properties();
  public static void setProperty(String key ,String value ){
    conf.setProperty( key,value );
  }
  public static String getProperty(String key ){
    return conf.getProperty( key,"0");
  }
  public static void clear( ){
    conf= null; // help gc
    conf = new Properties();
  }
}

public void mouseReleased() {
  mouseDown = false;
  mouseUp = true;
  if (curPixel>-1)changePixel(curPixel);
  if (curChar>-1)GetChar(curChar);
  ControlLock();
  
} 

public void mousePressed() {
                mouseDown = false;
                mouseUp = true;
        }

        public boolean mouseDown() {
                return mouseDown;
        }

        public boolean mouseUp() {
                return mouseUp;
        
        }
        
  // Web Links
   public void OSDHome()   { link("http://code.google.com/p/rush-osd-development/");}
   public void OSDWiki()   { link("http://code.google.com/p/rush-osd-development/w/");}
   public void MWiiForum() { link("http://www.multiwii.com/forum/viewtopic.php?f=8&t=2918");}
   public void Support ()  { link("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Q5VJ43Q2U2J6C");}
   

PImage FullFont;

public boolean FontChanged = false;
public boolean mouseDown = false;
public boolean mouseUp = true;
PImage PreviewChar;
PImage[] CharImages = new PImage[256];
int row = 0;
int gap = 5;
int gapE = 1;
int curPixel = -1;
int curChar = -1;
int editChar = -1;
boolean mouseSet = false;
int gray = color(120);
int white = color(255);
int black = color(0);


// screen locations
int XFullFont = 25;    int YFullFont = 25;
int XcharEdit = 5;    int YcharEdit = 5;
int PreviewX = 60;     int PreviewY = 275;



Bang CharBang[] = new Bang[256] ;
Bang CharPixelsBang[] = new Bang[216] ;
Bang PreviewCharBang;
Textlabel CharTopLabel[] = new Textlabel[16] ;
Textlabel CharSideLabel[] = new Textlabel[16] ;
Textlabel LabelCurChar;
Group FG,FGFull,FGCharEdit, FGPreview;
Button buttonSendFile,buttonBrowseFile,buttonEditFont,buttonFClose,buttonFSave;


public void Font_Editor_setup() {
  for (int i=0; i<256; i++) {
    CharImages[i] = createImage(12, 18, ARGB);
  }
  
 
  
  FG = FontGroupcontrolP5.addGroup("FG")
    .setPosition(121,15)
    .setWidth(670)
    .setBarHeight(14)
    .activateEvent(true)
    .setBackgroundColor(color(200,255))
    .setBackgroundHeight(440)
    .setLabel("Font Editor")
    .setMoveable(true)
    .disableCollapse()
    .hide();
    
    ;
    FGFull = FontGroupcontrolP5.addGroup("FGFull")
    .setPosition(20,20)
    .setWidth( 16*(12+gap)+ 25)
    .setBarHeight(12)
    .activateEvent(true)
    .hideBar() 
    .setBackgroundColor(color(0,255))
    .setBackgroundHeight(16*(18+gap)+35)
    .setLabel("Character List")
    .setMoveable(true)
    .setGroup(FG);
    ;
    FGCharEdit = FontGroupcontrolP5.addGroup("FGCharEdit")
    .setPosition(350,20)
    .setWidth( 12*(10+gapE)+ 33)
    .setBarHeight(12)
    .activateEvent(true)
    .hideBar() 
    .setBackgroundColor(color(180,255))
    .setBackgroundHeight(18*(10+gapE)+120)
    .setMoveable(true)
    .setGroup(FG);
    ;
        
// RawFont = LoadFont("MW_OSD_Team.mcm");
for (int i=0; i<256; i++) {
    int boxX = XFullFont+(i % 16) * (12+gap);
    int boxY = YFullFont + (i / 16) * (18+gap);
    CharBang[i] = FontGroupcontrolP5.addBang("CharMap"+i)
      .setPosition(boxX, boxY)
      .setSize(12, 18)
      .setLabel("")
      .setId(i)
      .setImages(CharImages[i], CharImages[i], CharImages[i], CharImages[i]) 
      .setGroup(FGFull);
    ;
   
  } 
String[] CharRows = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
  
  for (int i=0; i<16; i++) {
    int boxX = XFullFont-15;
    int boxY = YFullFont + i * (18+gap);
    CharSideLabel[i] = FontGroupcontrolP5.addTextlabel("charlabside" +i,CharRows[i],boxX ,boxY )
    .setGroup(FGFull)
    .setColor(white);
    ;
  } 
  for (int i=0; i<16; i++) {
    int boxX = XFullFont + i * (12+gap);
    int boxY = YFullFont-15;
    CharTopLabel[i] =FontGroupcontrolP5.addTextlabel("charlabtop" +i,CharRows[i],boxX ,boxY )
    .setGroup(FGFull)
    .setColor(white);
    ;
  } 
  

 
 for (int i=0; i<216; i++) {
    int boxX = XcharEdit+(i % 12) * (12+gapE);
    int boxY = YcharEdit + (i / 12) * (12+gapE);
    CharPixelsBang[i] = FontGroupcontrolP5.addBang("CharPix"+i)
      .setPosition(boxX, boxY)
      .setSize(12, 12)
      .setLabel("")
      .setId(i)
      .setColorBackground(gray)
      .setColorForeground(gray)
      .setValue(0)
      .setGroup(FGCharEdit)
      ;
    
  }

  LabelCurChar = FontGroupcontrolP5.addTextlabel("LabelCurChar","No Index Set" ,XcharEdit+ 10,YcharEdit + 18*(12+gapE)+5)
    .setColor(white)
    .setGroup(FGCharEdit);
 
  PreviewCharBang = FontGroupcontrolP5.addBang("PreviewCharBang")
    .setPosition(FGCharEdit.getWidth() / 2 -6, PreviewY)
    .setSize(12, 18)
    .setLabel("")
    .setImages(PreviewChar, PreviewChar, PreviewChar, PreviewChar) 
    .setGroup(FGCharEdit);
  ;
  buttonFSave = FontGroupcontrolP5.addButton("FSave",1,FGCharEdit.getWidth()-55, FGCharEdit.getBackgroundHeight()-25,45,18);//buttonFClose.setColorBackground(red_);
  buttonFSave.getCaptionLabel()
    .setFont(font12)
    .toUpperCase(false)
    .setText("SAVE");
  buttonFSave.setGroup(FGCharEdit); 
 
 
  buttonFClose = FontGroupcontrolP5.addButton("FCLOSE",1,550 ,10,45,18);buttonFClose.setColorBackground(red_);
  buttonFClose.getCaptionLabel()
    .setFont(font12)
    .toUpperCase(false)
    .setText("CLOSE");
  buttonFClose.setGroup(FG);
 
  MGUploadF = controlP5.addGroup("MGUploadF")
                .setPosition(640,20)
                .setWidth(155)
                .setTab ("default")
                .setBarHeight(15)
                .activateEvent(true)  
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(128) 
                .setLabel("             Font Tools")
                .setColorForeground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .disableCollapse();
               ; 

FileUploadText = controlP5.addTextlabel("FileUploadText","",10,5)
.setGroup(MGUploadF);
;

buttonEditFont = controlP5.addButton("EditFont",1,48,25,60,16)
.setGroup(MGUploadF);
 buttonEditFont.getCaptionLabel()
.toUpperCase(false)
.setText(" Edit Font")
;

buttonSendFile = controlP5.addButton("FONT_UPLOAD",1,48,50,60,16)
.setGroup(MGUploadF);

buttonBrowseFile = controlP5.addButton("Browse",1,48,75,60,16)
.setGroup(MGUploadF);
buttonBrowseFile.getCaptionLabel()
.toUpperCase(false)
.setText("  Browse");


buttonSendFile.getCaptionLabel()
    .toUpperCase(false)
    .setText("  Upload");
 
}

public void Send(){

}

public void MakePreviewChar(){
  PImage PreviewChar = createImage(12, 18, ARGB);
   PreviewChar.loadPixels();
  for(int byteNo = 0; byteNo < 216; byteNo++) {
    switch(PApplet.parseInt(CharPixelsBang[byteNo].value())) {
      case 0:
        PreviewChar.pixels[byteNo] = gray;
      break;     
      case 1:
        PreviewChar.pixels[byteNo] = black;

      break; 
      case 2:
        PreviewChar.pixels[byteNo] = white;

      break; 
    }
  }
  PreviewChar.updatePixels();
  PreviewCharBang.setImages(PreviewChar, PreviewChar, PreviewChar, PreviewChar); 
}

public void FSave(){
  UpdateChar();
  CreateFontFile();
}

public void FCLOSE(){
  Lock_All_Controls(false);
  FG.hide();
}

public void EditFont(){
  Lock_All_Controls(true);
  FG.show();
  Lock_All_Controls(true);
}

public void changePixel(int id){
 
  if ((mouseUp) && (id > -1)){
    int curColor = PApplet.parseInt(CharPixelsBang[id].value());
    switch(curColor) {
    
    case 0: 
     CharPixelsBang[id].setColorForeground(black);
     CharPixelsBang[id].setValue(1);
     FontChanged = true;

    
    break; 
    
    case 1: 
     CharPixelsBang[id].setColorForeground(white);
     CharPixelsBang[id].setValue(2);
     FontChanged = true;
    
    break; 
    
    case 2: 
     CharPixelsBang[id].setColorForeground(gray);
     CharPixelsBang[id].setValue(0);
     FontChanged = true;
    
    break; 
   }
  }
  curPixel = -1;
  MakePreviewChar();
 
}  

public void GetChar(int id){
 
  if ((mouseUp) && (id > -1)){
    for(int byteNo = 0; byteNo < 216; byteNo++) {
      CharPixelsBang[byteNo].setColorForeground(gray);
      CharPixelsBang[byteNo].setValue(0);
    }
    for(int byteNo = 0; byteNo < 216; byteNo++) {
      switch(CharImages[id].pixels[byteNo]) {
          case 0xFF000000:
            CharPixelsBang[byteNo].setColorForeground(black);
            CharPixelsBang[byteNo].setValue(1);
          break; 
          case 0xFFFFFFFF:
            CharPixelsBang[byteNo].setColorForeground(white);
            CharPixelsBang[byteNo].setValue(2);
          break;
          default:
            CharPixelsBang[byteNo].setColorForeground(gray);
            CharPixelsBang[byteNo].setValue(0);
          break;  
      }
      
    }
   
    LabelCurChar.setValue(str(id));
    LabelCurChar.setColorBackground(0);
    MakePreviewChar();
    editChar = id; 
    curChar = -1;
  }
 
}


public void UpdateChar(){
 int changechar = Integer.parseInt(LabelCurChar.getStringValue());

 
  CharImages[changechar].loadPixels();
  for(int byteNo = 0; byteNo < 216; byteNo++) {
    switch(PApplet.parseInt(CharPixelsBang[byteNo].value())) {
      case 0:
       CharImages[changechar].pixels[byteNo] = gray;
      break;     
      case 1:
       CharImages[changechar].pixels[byteNo] = black;
      break; 
      case 2:
       CharImages[changechar].pixels[byteNo] = white;
      break; 
    }
  }
  CharImages[changechar].updatePixels();
  CharBang[changechar].setImages(CharImages[changechar], CharImages[changechar], CharImages[changechar], CharImages[changechar]); 
}    
  
  
  




public void setLock(Controller theController, boolean theValue) {
  theController.setLock(theValue);
}

public void Lock_All_Controls(boolean theLock){


ControllerInterface[] sctrl = ScontrolP5.getControllerList();
   for(int i=0; i<sctrl.length; ++i)
   {
    try{
      setLock(ScontrolP5.getController(sctrl[i].getName()),theLock);
    }catch (NullPointerException e){}
   }
   
   ControllerInterface[] ctrl5 = controlP5.getControllerList();
   for(int i=0; i<ctrl5.length; ++i)
   {
    try{
      setLock(controlP5.getController(ctrl5[i].getName()),theLock);
    }catch (NullPointerException e){}
   }
}

public void Browse(){
  SwingUtilities.invokeLater(new Runnable(){
    public void run(){
      final JFileChooser fc = new JFileChooser(dataPath(""));
      fc.setDialogType(JFileChooser.SAVE_DIALOG);
      fc.setFileFilter(new FontFileFilter());
      //fc.setCurrentDirectory();
      int returnVal = fc.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File FontFile = fc.getSelectedFile();
        FileInputStream in = null;
        boolean completed = false;
        String error = null;
        try{
          in = new FileInputStream(FontFile) ;
          FontFileName = FontFile.getPath();
          img_Clear = LoadFont(FontFileName);
          updateConfig(); 
          JOptionPane.showMessageDialog(null,new StringBuffer().append("Font File successfully loaded"));
          completed  = true;
          
        }catch(FileNotFoundException e){
                error = e.getCause().toString();

        }catch( IOException ioe){/*failed to read the file*/
                ioe.printStackTrace();
                error = ioe.getCause().toString();
        }finally{
          if (!completed){
                 // MWI.conf.clear();
                 // or we can set the properties with view values, sort of 'nothing happens'
                 //updateModel();
          }
          //updateView();
          if (in!=null){
            try{
              in.close();
            }catch( IOException ioe){/*failed to close the file*/}
          }
          
          if (error !=null){
                  JOptionPane.showMessageDialog(null, new StringBuffer().append("error : ").append(error) );
          }
        }
      }
    }
  }
  );  
}


public class FontFileFilter extends FileFilter {
  public boolean accept(File f) {
    if(f != null) {
      if(f.isDirectory()) {
        return true;
      }
      String extension = getExtension(f);
      if("mcm".equals(extension)) {
        return true;
      }
    }
    return false;
  }

  public String getExtension(File f) {
    if(f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');
      if(i>0 && i<filename.length()-1) {
        return filename.substring(i+1).toLowerCase();
      }
    }
    return null;
  } 

  public String getDescription() {
    return "*.mcm Font File";
  }   
}


public void SetupGroups(){
 
  G_PortStatus = FontGroupcontrolP5.addGroup("G_PortStatus")
    .setPosition(XPortStat,YPortStat)
    .setTab ("default")
    .setWidth(110)
    .setColorForeground(color(30,255))
    .setColorBackground(color(30,255))
    .setColorLabel(color(0, 300, 320))
    .setBarHeight(15)
    .setBackgroundColor(color(30,255))
    .setColorActive(red_)
    .setBackgroundHeight(27)
    .setLabel("    Port Status")
    .disableCollapse()
    ;
    TXText = FontGroupcontrolP5.addTextlabel("TXText","TX",65,5)
    .setColorValue(red_)
    .setGroup("G_PortStatus");
    
    RXText = FontGroupcontrolP5.addTextlabel("RXText","RX",15,5)
    .setColorValue(green_)
   
    .setGroup("G_PortStatus");
    
G_EEPROM = GroupcontrolP5.addGroup("G_EEPROM")
                .setPosition(XEEPROM,YEEPROM+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorForeground(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((1*17) +5)
                .setLabel("EEPROM")
                .disableCollapse() 
                ; 
                G_EEPROM.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;
                G_EEPROM.hide();
                
G_RSSI = GroupcontrolP5.addGroup("G_RSSI")
                .setPosition(XRSSI,YRSSI+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((7*14)+6)
                .setLabel("RSSI SETTINGS")
                .disableCollapse() 
                ; 
                G_RSSI.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ; 
G_Voltage = GroupcontrolP5.addGroup("G_Voltage")
                .setPosition(XVolts,YVolts+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((5*13) +5)
                .setLabel("MAIN VOLTAGE")
                .disableCollapse() 
                ; 
                G_Voltage.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
              ; 
 G_VVoltage = GroupcontrolP5.addGroup("G_VVoltage")
                .setPosition(XVVolts,YVVolts+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((3*17) +4)
                .setLabel("VIDEO VOLTAGE")
                .disableCollapse() 
                ; 
                G_VVoltage.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ; 
G_GPS = GroupcontrolP5.addGroup("G_GPS")
                .setPosition(XGPS,YGPS+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setBarHeight(16)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(16)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((3*17) +3) 
                .setLabel("GPS SETTINGS")
                .disableCollapse() 
                ; 
                G_GPS.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;        
G_Board = GroupcontrolP5.addGroup("G_Board")
                .setPosition(XBoard,YBoard+15)
                .setWidth(Col1Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((1*17) +5)
                .setLabel("OSD BOARD TYPE")
                .disableCollapse(); 
                G_Board.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;                
G_CallSign = GroupcontrolP5.addGroup("G_CallSign")
                .setPosition(XCS,YCS+15)
                .setWidth(Col4Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((3*4) +4)
                .setLabel("Call Sign")
                .disableCollapse(); 
                G_CallSign.captionLabel()
                .toUpperCase(true)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;                                                
G_Other = GroupcontrolP5.addGroup("G_Other")
                .setPosition(XOther,YOther+15)
                .setWidth(Col2Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((10*8) +9)
                .setLabel("OTHER FUNCTIONS")
                .disableCollapse() 
                ; 
                G_Other.captionLabel()
                .toUpperCase(true)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;  
G_Amperage = GroupcontrolP5.addGroup("G_Amperage")
                .setPosition(XAmps,YAmps+15)
                .setWidth(Col3Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((5*13) +5)
                .setLabel("AMPERAGE")
                .disableCollapse() 
                ; 
                G_Amperage.captionLabel()
                .align(controlP5.CENTER,controlP5.CENTER)
                ;
G_Volume = GroupcontrolP5.addGroup("G_Volume")
                .setPosition(XVolume,YVolume)
                .setWidth(Col1Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((10*5) +5)
                .setLabel("VOLUME FLIGHT WARNINGS")
                .disableCollapse() 
                ; 
                G_Volume.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;               
G_UnitVideo = GroupcontrolP5.addGroup("G_UnitVideo")
                .setPosition(XUnitVideo,YUnitVideo+15)
                .setWidth(Col2Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((10*3) +7)
                .setLabel("UNIT & VIDEO")
                .disableCollapse() 
                ; 
                G_UnitVideo.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;

G_ADC = GroupcontrolP5.addGroup("G_ADC")
                .setPosition(XADC,YADC+15)
                .setWidth(Col2Width)
                .setTab ("default")
                .setBarHeight(15)
                .setColorForeground(color(30,255))
                .setColorBackground(color(30,255))
                .setColorLabel(color(0, 300, 320))
                .setBarHeight(15)
                .setBackgroundColor(color(30,255))
                .setColorActive(red_)
                .setBackgroundHeight((10*5)+5)
                .setLabel("HARDWARE ADC\u00b4S")
                .disableCollapse() 
                ; 
                G_ADC.captionLabel()
                .toUpperCase(false)
                .align(controlP5.CENTER,controlP5.CENTER)
                ;
}

public boolean toggleRead = false,
        toggleMSP_Data = true,
        toggleReset = false,
        toggleCalibAcc = false,
        toggleCalibMag = false,
        toggleWrite = false,
        toggleSpekBind = false,
        toggleSetSetting = false;
Serial g_serial;      // The serial port
float LastPort = 0;
int time,time1,time2,time3,time4,time5;

boolean ClosePort = false;
boolean PortIsWriting = false;
boolean FontMode = false;
int FontCounter = 0;
int CloseMode = 0;

/******************************* Multiwii Serial Protocol **********************/

private static final String MSP_HEADER = "$M<";

private static final int
  MSP_IDENT                =100,
  MSP_STATUS               =101,
  MSP_RAW_IMU              =102,
  MSP_SERVO                =103,
  MSP_MOTOR                =104,
  MSP_RC                   =105,
  MSP_RAW_GPS              =106,
  MSP_COMP_GPS             =107,
  MSP_ATTITUDE             =108,
  MSP_ALTITUDE             =109,
  MSP_ANALOG               =110,
  MSP_RC_TUNING            =111,
  MSP_PID                  =112,
  MSP_BOX                  =113,
  MSP_MISC                 =114,
  MSP_MOTOR_PINS           =115,
  MSP_BOXNAMES             =116,
  MSP_PIDNAMES             =117,
  MSP_BOXIDS               =119,
  MSP_RSSI                 =120,
  MSP_SET_RAW_RC           =200,
  MSP_SET_RAW_GPS          =201,
  MSP_SET_PID              =202,
  MSP_SET_BOX              =203,
  MSP_SET_RC_TUNING        =204,
  MSP_ACC_CALIBRATION      =205,
  MSP_MAG_CALIBRATION      =206,
  MSP_SET_MISC             =207,
  MSP_RESET_CONF           =208,
  MSP_SET_WP               =209,
  MSP_SELECT_SETTING       =210,
  MSP_SET_HEAD             =211,
  
  MSP_SPEK_BIND            =240,

  MSP_EEPROM_WRITE         =250,
  
  MSP_DEBUGMSG             =253,
  MSP_DEBUG                =254;

private static final int
  MSP_OSD                  =220;

// Subcommands
private static final int
  OSD_NULL                 =0,
  OSD_READ_CMD             =1,
  OSD_WRITE_CMD            =2,
  OSD_GET_FONT             =3,
  OSD_SERIAL_SPEED         =4,
  OSD_RESET                =5;


// initialize the serial port selected in the listBox
public void InitSerial(float portValue) {
  if (portValue < commListMax) {
    if(init_com == 0){ 
      try{
      String portPos = Serial.list()[PApplet.parseInt(portValue)];
      txtlblWhichcom.setValue("COM = " + shortifyPortName(portPos, 8));
      g_serial = new Serial(this, portPos, 115200);
      LastPort = portValue;
      init_com=1;
      toggleMSP_Data = true;
      ClosePort = false;
      buttonREAD.setColorBackground(green_);
      buttonRESET.setColorBackground(green_);
      commListbox.setColorBackground(green_);
      buttonRESTART.setColorBackground(green_);
      
      
      g_serial.buffer(256);
      System.out.println("Port Turned On " );
      FileUploadText.setText("");
      delay(1500);
      SendCommand(MSP_IDENT);
     
      SendCommand(MSP_STATUS);
      READ();
       } catch (Exception e) { // null pointer or serial port dead
         noLoop();
        JOptionPane.showConfirmDialog(null,"Error Opening Port It may be in Use", "Port Error", JOptionPane.PLAIN_MESSAGE,JOptionPane.WARNING_MESSAGE);
        loop();
        System.out.println("OpenPort error " + e);
     }
   }
 }
  else {
    if(init_com == 1){
     System.out.println("Begin Port Down " ); 
      txtlblWhichcom.setValue("Comm Closed");
      toggleMSP_Data = false;
      ClosePort = true;
      init_com=0;
    }
  }
  
}

public void ClosePort(){
  init_com=0;
  g_serial.clear();
  g_serial.stop();
  init_com=0;
  commListbox.setColorBackground(red_);
  buttonREAD.setColorBackground(red_);
  buttonRESET.setColorBackground(red_);
  buttonWRITE.setColorBackground(red_);
  buttonRESTART.setColorBackground(red_);
  if (CloseMode > 0){
    InitSerial(LastPort);
    CloseMode = 0;
  }
    
  
}

public void SetConfigItem(int index, int value) {
  if(index >= CONFIGITEMS)
    return;

  confItem[index].setValue(value);
  if (index == CONFIGITEMS-1)
    buttonWRITE.setColorBackground(green_);
    
  if (value >0){
    toggleConfItem[index].setValue(1);
  }
  else{
    toggleConfItem[index].setValue(0);
  }

  try{
    switch(value) {
    case(0):
      RadioButtonConfItem[index].activate(0);
      break;
    case(1):
      RadioButtonConfItem[index].activate(1);
      break;
    }
  }
  catch(Exception e) {
  }finally {
  }
BuildCallSign();  	
}

public void PORTCLOSE(){
  toggleMSP_Data = false;
  CloseMode = 0;
  InitSerial(200.00f);
}

public void BounceSerial(){
  toggleMSP_Data = false;
  CloseMode = 1;
  InitSerial(200.00f);
  
}  


public void RESTART(){
  toggleMSP_Data = true;
  for (int txTimes = 0; txTimes<2; txTimes++) {
    headSerialReply(MSP_OSD, 1);
    serialize8(OSD_RESET);
    tailSerialReply();
  }
  toggleMSP_Data = false;
  CloseMode = 1;
  ClosePort();
  delay(1500);
  READ();
} 



public void READ(){
  
  for(int i = 0; i < CONFIGITEMS; i++){
    SetConfigItem((byte)i, 0);
  }
  PortRead = true; 
  MakePorts();
   for (int txTimes = 0; txTimes<2; txTimes++) {
     toggleMSP_Data = true;
     headSerialReply(MSP_OSD, 1);
     serialize8(OSD_READ_CMD);
     tailSerialReply();
   }
}

public void WRITE(){
  CheckCallSign();
  PortWrite = true;
  MakePorts();
  toggleMSP_Data = true;
  p = 0;
  inBuf[0] = OSD_WRITE_CMD;
  for (int txTimes = 0; txTimes<2; txTimes++) {
    headSerialReply(MSP_OSD, CONFIGITEMS+1);
    serialize8(OSD_WRITE_CMD);
    for(int i = 0; i < CONFIGITEMS; i++){
      serialize8(PApplet.parseInt(confItem[i].value()));
    }
    tailSerialReply();
  }
  
  toggleMSP_Data = false;
  g_serial.clear();
  PortWrite = false;
}

public void FONT_UPLOAD(){
  if (init_com==0){
    noLoop();
    JOptionPane.showConfirmDialog(null,"Please Select a Port", "Not Connected", JOptionPane.PLAIN_MESSAGE,JOptionPane.WARNING_MESSAGE);
    loop();
  }else
  {
  SimControlToggle.setValue(0);
  System.out.println("FONT_UPLOAD");
  //toggleMSP_Data = true;
  FontMode = true;
  PortWrite = true;
  MakePorts();
  FontCounter = 0;
  FileUploadText.setText("Please Wait...");
  p = 0;
  inBuf[0] = OSD_GET_FONT;
    headSerialReply(MSP_OSD, 5);
    serialize8(OSD_GET_FONT);
    serialize16(7456);  // safety code
    serialize8(0);    // first char
    serialize8(255);  // last char
    tailSerialReply();
 //}
  }

}

public void SendChar(){
 time2=time;
    PortWrite = !PortWrite;  // toggle PortWrite to flash TX
    MakePorts();
    System.out.println("Sent Char "+FontCounter);
    buttonSendFile.getCaptionLabel().setText("  " +nf(FontCounter, 3)+"/256");
    headSerialReply(MSP_OSD, 56);
    serialize8(OSD_GET_FONT);
    for(int i = 0; i < 54; i++){
      serialize8(PApplet.parseInt(raw_font[FontCounter][i]));
    }
    serialize8(FontCounter);
    tailSerialReply();
    if (FontCounter <255){
      FontCounter++;
    }else{ 
      g_serial.clear();
      PortWrite = false;
      FontMode = false;      
      System.out.println("Finished Uploading Font");
      buttonSendFile.getCaptionLabel().setText("  Upload");
      FileUploadText.setText("");
      RESTART();
      READ();
    } 
  
}


public void RESET(){
 if (init_com==1){
    noLoop();
    int Reset_result = JOptionPane.showConfirmDialog(this,"Are you sure you wish to RESET?", "RESET OSD MEMORY",JOptionPane.WARNING_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
    loop();
    switch (Reset_result) {
      case JOptionPane.YES_OPTION:
        toggleConfItem[0].setValue(0);
        confItem[0].setValue(0);
        WRITE();
        RESTART();
        return;
      case JOptionPane.CANCEL_OPTION:
        return;
      default:
        return;
    }
 }else
 {
   noLoop();
   JOptionPane.showConfirmDialog(null,"Please Select a Port", "Not Connected", JOptionPane.PLAIN_MESSAGE,JOptionPane.WARNING_MESSAGE);
   loop();
 }
}

String boxids[] = {
    "ARM;",
    "ANGLE;",
    "HORIZON;",
    "BARO;",
    "MAG;",
    "GPS HOME;",
    "GPS HOLD;",
    "OSD SW;"
  };
String strBoxIds = join(boxids,"");  

public void SendCommand(int cmd){
  switch(cmd) {
  
  case MSP_STATUS:
        PortIsWriting = true;
        Send_timer+=1;
        headSerialReply(MSP_STATUS, 11);
        serialize16(Send_timer);
        serialize16(0);
        serialize16(1|1<<1|1<<2|1<<3|0<<4);
        int modebits = 0;
        int BitCounter = 1;
        for (int i=0; i<boxids.length; i++) {
          if(toggleModeItems[i].getValue() > 0) modebits |= BitCounter;
          BitCounter += BitCounter;
        }
        serialize32(modebits);
        serialize8(0);   // current setting
        tailSerialReply();
        PortIsWriting = false;
      break;
      
      case MSP_RC:
        PortIsWriting = true;
        headSerialReply(MSP_RC, 14);
        serialize16(PApplet.parseInt(Pitch_Roll.arrayValue()[0]));
        serialize16(PApplet.parseInt(Pitch_Roll.arrayValue()[1]));
        serialize16(PApplet.parseInt(Throttle_Yaw.arrayValue()[0]));
        serialize16(PApplet.parseInt(Throttle_Yaw.arrayValue()[1]));
        for (int i=5; i<8; i++) {
          serialize16(1500);
        }
        tailSerialReply();
        PortIsWriting = false;
      break;
      
      case MSP_BOXIDS:
        headSerialReply(MSP_BOXIDS, strBoxIds.length());
        for(int i=0;i<4;i++) 
        {serialize8(i);
        serialize8(1);
        serialize8(2);
        serialize8(3);
        serialize8(5);
        serialize8(10);
        serialize8(11);
        serialize8(19);
        tailSerialReply();
        
        PortIsWriting = false;
     }
     
      case MSP_ATTITUDE:
        PortIsWriting = true;
        headSerialReply(MSP_ATTITUDE, 8);
        serialize16(PApplet.parseInt(MW_Pitch_Roll.arrayValue()[0])*20);
        serialize16(PApplet.parseInt(MW_Pitch_Roll.arrayValue()[1])*20);
        serialize16(MwHeading);
        serialize16(0);
        tailSerialReply();
        PortIsWriting = false;
      break;
     
     
     
      case MSP_ANALOG:
        PortIsWriting = true;
        headSerialReply(MSP_ANALOG, 5);
        serialize8(PApplet.parseInt(sVBat * 10));
        serialize16(0);
        serialize16(PApplet.parseInt(sMRSSI));
        tailSerialReply();
        PortIsWriting = false;
      break;
      
      
      case MSP_RAW_GPS:
        // We have: GPS_fix(0-2), GPS_numSat(0-15), GPS_coord[LAT & LON](signed, in 1/10 000 000 degres), GPS_altitude(signed, in meters) and GPS_speed(in cm/s)  
        headSerialReply(MSP_RAW_GPS,16);
        serialize8(PApplet.parseInt(SGPS_FIX.arrayValue()[0]));
        serialize8(PApplet.parseInt(SGPS_numSat.value()));
        serialize32(384627000);
        serialize32(-90803000);
        serialize16(PApplet.parseInt(SGPS_altitude.value()/100));
        serialize16(PApplet.parseInt(SGPS_speed.value()));
        serialize16(355);     
    break;
    
  
    case MSP_COMP_GPS:
      headSerialReply(MSP_COMP_GPS,5);
      serialize16(PApplet.parseInt(SGPS_distanceToHome.value()));
      int GPSheading = PApplet.parseInt(SGPSHeadHome.value());
      if(GPSheading < 0) GPSheading += 360;
      serialize16(GPSheading);
      serialize8(0);
    break;
    
    
    case MSP_ALTITUDE:
      headSerialReply(MSP_ALTITUDE, 6);
      serialize32(PApplet.parseInt(sAltitude) *100);
      serialize16(PApplet.parseInt(sVario) *10);     
    break;
      
    }
    tailSerialReply();   
  
}

// coded by Eberhard Rensch
// Truncates a long port name for better (readable) display in the GUI
public String shortifyPortName(String portName, int maxlen)  {
  String shortName = portName;
  if(shortName.startsWith("/dev/")) shortName = shortName.substring(5);  
  if(shortName.startsWith("tty.")) shortName = shortName.substring(4); // get rid of leading tty. part of device name
  if(portName.length()>maxlen) shortName = shortName.substring(0,(maxlen-1)/2) + "~" +shortName.substring(shortName.length()-(maxlen-(maxlen-1)/2));
  if(shortName.startsWith("cu.")) shortName = "";// only collect the corresponding tty. devices
  return shortName;
}

public static final int
  IDLE = 0,
  HEADER_START = 1,
  HEADER_M = 2,
  HEADER_ARROW = 3,
  HEADER_SIZE = 4,
  HEADER_CMD = 5,
  HEADER_ERR = 6;

private static final String MSP_SIM_HEADER = "$M>";
int c_state = IDLE;
boolean err_rcvd = false;
byte checksum=0;
byte cmd = 0;
int offset=0, dataSize=0;
byte[] inBuf = new byte[256];
int Send_timer = 1;
int p=0;
public int read32() {return (inBuf[p++]&0xff) + ((inBuf[p++]&0xff)<<8) + ((inBuf[p++]&0xff)<<16) + ((inBuf[p++]&0xff)<<24); }
public int read16() {return (inBuf[p++]&0xff) + ((inBuf[p++])<<8); }
public int read8()  {return inBuf[p++]&0xff;}

int outChecksum;

public void serialize8(int val) {
 if (init_com==1)  {
       try{
       g_serial.write(val);
       outChecksum ^= val;
       } catch(java.lang.Throwable t) {
         System.out.println( t.getClass().getName() ); //this'll tell you what class has been thrown
         t.printStackTrace(); //get a stack trace
    }
  }
}

public void serialize16(int a) {
  if (str(a)!=null ){
  serialize8((a   ) & 0xFF);
  serialize8((a>>8) & 0xFF);
  }
}

public void serialize32(int a) {
  if (str(a)!=null ){
    serialize8((a    ) & 0xFF);
    serialize8((a>> 8) & 0xFF);
    serialize8((a>>16) & 0xFF);
    serialize8((a>>24) & 0xFF);
  }
 
}

public void headSerialResponse(int requestMSP, Boolean err, int s) {
  serialize8('$');
  serialize8('M');
  serialize8(err ? '!' : '>');
  outChecksum = 0; // start calculating a new checksum
  serialize8(s);
  serialize8(requestMSP);
}

public void headSerialReply(int requestMSP, int s) {
  if ((str(requestMSP) !=null) && (str(s)!=null)){
    headSerialResponse(requestMSP, false, s);
  }
}

public void tailSerialReply() {
  if (outChecksum > 0) serialize8(outChecksum);
}

public void DelayTimer(int ms){
  int time = millis();
  while(millis()-time < ms);
}

public void evaluateCommand(byte cmd, int size) {
  if ((init_com==0)  || (toggleMSP_Data == false)) return;
  MakePorts(); 
  int icmd = PApplet.parseInt(cmd&0xFF);
  if (icmd !=MSP_OSD)return;

    time2=time;
    switch(icmd) {
      case MSP_OSD:
        int cmd_internal = read8();
        
        if(cmd_internal == OSD_NULL) {
        }

        if(cmd_internal == OSD_READ_CMD) {
          if(size == 1) {
          }
          else {
            // Returned result from OSD.
            for(int i = 0; i < CONFIGITEMS; i++){
              SetConfigItem(i, read8());
            }
            if (FontMode == false){
              toggleMSP_Data = false;
              g_serial.clear();
              
              
            }
          }
        }
        
        if(cmd_internal == OSD_GET_FONT) {
          if( size == 1) {
          }
          if(size == 3) {
       }
     }
    break;
  }
}

public void MWData_Com() {
  if ((toggleMSP_Data == false) ||(init_com==0)) return;
  int i,aa;
  float val,inter,a,b,h;
  int c = 0;
    while (g_serial.available()>0 && (toggleMSP_Data == true)) {
    try{
      c = (g_serial.read());
      
     if (str(c) == null)return;
      
      PortRead = true;
      if (c_state == IDLE) {
        c_state = (c=='$') ? HEADER_START : IDLE;
      }
      else if (c_state == HEADER_START) {
        c_state = (c=='M') ? HEADER_M : IDLE;
      }
      else if (c_state == HEADER_M) {
        if (c == '<') {
          c_state = HEADER_ARROW;
        } else if (c == '!') {
          c_state = HEADER_ERR;
        } else {
          c_state = IDLE;
        }
      }
      else if (c_state == HEADER_ARROW || c_state == HEADER_ERR) {
        /* is this an error message? */
        err_rcvd = (c_state == HEADER_ERR);        /* now we are expecting the payload size */
        dataSize = (c&0xFF);
        /* reset index variables */
        p = 0;
        offset = 0;
        checksum = 0;
        checksum ^= (c&0xFF);
        /* the command is to follow */
        c_state = HEADER_SIZE;
      }
      else if (c_state == HEADER_SIZE) {
        cmd = (byte)(c&0xFF);
        checksum ^= (c&0xFF);
        c_state = HEADER_CMD;
      }
      else if (c_state == HEADER_CMD && offset < dataSize) {
          checksum ^= (c&0xFF);
          inBuf[offset++] = (byte)(c&0xFF);
      } 
      else if (c_state == HEADER_CMD && offset >= dataSize) {
        /* compare calculated and transferred checksum */
        if ((checksum&0xFF) == (c&0xFF)) {
          if (err_rcvd) {
          } else {
            /* we got a valid response packet, evaluate it */
            try{
              if ((init_com==1)  && (toggleMSP_Data == true)) {
                  evaluateCommand(cmd, (int)dataSize);
              }
              else{
                System.out.println("port is off ");
              }
              
              
              } catch (Exception e) { // null pointer or serial port dead
              System.out.println("write error " + e);
              }
           }
        }
        else {
          System.out.println("invalid checksum for command "+((int)(cmd&0xFF))+": "+(checksum&0xFF)+" expected, got "+(int)(c&0xFF));
          System.out.print("<"+(cmd&0xFF)+" "+(dataSize&0xFF)+"> {");
          for (i=0; i<dataSize; i++) {
            if (i!=0) { System.err.print(' '); }
            System.out.print((inBuf[i] & 0xFF));
          }
          System.out.println("} ["+c+"]");
          System.out.println(new String(inBuf, 0, dataSize));
      }
        c_state = IDLE;
        
      }
      
      } catch(java.lang.Throwable t) {
         System.out.println( t.getClass().getName() ); //this'll tell you what class has been thrown
         t.printStackTrace(); //get a stack trace
      }
   }
}





      
float sAltitude = 0;
float sVario = 0;
float sVBat = 0;
float sMRSSI = 0;
int mode_armed = 0;
int mode_stable = 0;
int mode_baro = 0;
int mode_mag = 0;
int mode_gpshome = 0;
int mode_gpshold = 0;
int mode_llights = 0;
int mode_osd_switch = 0;

int SendSim = 0;

boolean[] keys = new boolean[526];
boolean armed = false;

Group SG,SGControlBox,SGModes,SGAtitude,SGRadio,SGSensors1,SGGPS; 

// Checkboxs
CheckBox checkboxSimItem[] = new CheckBox[SIMITEMS] ;
CheckBox UnlockControls, SGPS_FIX;
// Toggles
Toggle toggleModeItems[] = new Toggle[boxids.length] ;
Toggle SimControlToggle;
// Slider2d ---
Slider2D Pitch_Roll, Throttle_Yaw,MW_Pitch_Roll;
// Sliders ---
Slider s_Altitude,s_Vario,s_VBat,s_MRSSI;
// Label
Textlabel txtlblModeItems[] = new Textlabel[boxids.length] ;
Textlabel SimControlText;

// Knobs----
Knob HeadingKnob,SGPSHeadHome;

Numberbox SGPS_numSat, SGPS_altitude, SGPS_speed, SGPS_ground_course,SGPS_distanceToHome,SGPS_directionToHome,SGPS_update;
DecimalFormat OnePlaceDecimal = new DecimalFormat("0.0");

//**************************************************************//

 
public void SimSetup(){


  SG = ScontrolP5.addGroup("SG")
    .setPosition(120,YSim + -71)
    //.setTab ("Simulate") // Test OK
    .setWidth(674)
    .setBarHeight(14)
    .activateEvent(true)
    .setValue(0)
    .close()
    //.hide()
    .setBackgroundColor(color(0,255))
    .setColorLabel(color(yellow_))
    .setBackgroundHeight(204)
   .setLabel("                                                                 Multiwii flight control data simulator ")
   .setMoveable(false)
    ;
                
 
  SGModes = ScontrolP5.addGroup("SGModes")
                .setPosition(570,20)
                .setWidth(100)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight((boxids.length*17) + 9)
                .setLabel("  Flight Modes")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                .disableCollapse()
                //.close() 
               ; 
               
  SGAtitude = ScontrolP5.addGroup("SGAtitude")
                .setPosition(186,124)
                .setWidth(200)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(75)
                .setLabel("                       Attitude")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                //.close() 
               ;
               
 SGRadio = ScontrolP5.addGroup("SGRadio")
                .setPosition(391,20)
                .setWidth(174)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(72)
                .setLabel("  RC Radio Control")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                //.close() 
               ; 

SGSensors1 = ScontrolP5.addGroup("SGSensors1")
                .setPosition(5,20)
                .setWidth(175)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(110)
                .setLabel("      MWii Sensors & ADC\u00b4s ")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                //.close() 
               ;                                  
SGGPS = ScontrolP5.addGroup("SGGPS")
                .setPosition(186,20)
                .setWidth(200)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(85)
                .setLabel("               Multiwii GPS Data")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                //.close() 
               ;

SGControlBox = ScontrolP5.addGroup("SGControlBox")
                .setPosition(5,150)
                .setWidth(175)
                .setBarHeight(15)
                .activateEvent(true)
                .disableCollapse()
                .setBackgroundColor(color(30,255))
                .setBackgroundHeight(49)
                .setLabel("       Simulator Control")
                .setColorLabel(color(0, 300, 320))
                .setGroup(SG)
                //.close() 
               ;   

SimControlToggle = (controlP5.Toggle) hideLabel(controlP5.addToggle("SendSim"));
SimControlToggle.setPosition(60,5);
SimControlToggle.setSize(40,10);
SimControlToggle.setMode(ControlP5.SWITCH);
SimControlToggle.setColorActive(color(0, 160, 100));
SimControlToggle.setColorBackground(color(80,0,0));
SimControlToggle.setGroup(SGControlBox);
SimControlText = controlP5.addTextlabel("SimControlText","On  Off",56,15);
SimControlText.setGroup(SGControlBox);
               
               
SGPS_FIX =  ScontrolP5.addCheckBox("GPS_FIX",5,5);
    SGPS_FIX.setColorBackground(color(80));
    SGPS_FIX.setColorActive(color(0, 160, 100));
    SGPS_FIX.addItem("GPS Fix",1);
    SGPS_FIX.setGroup(SGGPS);
    
SGPS_numSat = ScontrolP5.addNumberbox("SGPS_numSat",0,5,20,40,14);
    SGPS_numSat.setLabel("Sats");
   // SGPS_numSat.setColorBackground(red_);
    SGPS_numSat.setMin(0);
    SGPS_numSat.setDirection(Controller.HORIZONTAL);
    SGPS_numSat.setMax(15);
    SGPS_numSat.setDecimalPrecision(0);
    SGPS_numSat.setGroup(SGGPS); 
 ScontrolP5.getController("SGPS_numSat").getCaptionLabel()
   .align(ControlP5.LEFT, ControlP5.RIGHT_OUTSIDE).setPaddingX(45);

SGPS_altitude = ScontrolP5.addNumberbox("SGPS_altitude",0,5,40,40,14);
    SGPS_altitude.setLabel("Alt-cm");
    SGPS_altitude.setMin(0);
    SGPS_altitude.setDirection(Controller.HORIZONTAL);
    SGPS_altitude.setMax(10000);
    SGPS_altitude.setDecimalPrecision(0);
    SGPS_altitude.setGroup(SGGPS); 
 ScontrolP5.getController("SGPS_altitude").getCaptionLabel()
   .align(ControlP5.LEFT, ControlP5.RIGHT_OUTSIDE).setPaddingX(45); 
   SGPS_altitude.hide();   
 
 SGPS_speed = ScontrolP5.addNumberbox("SGPS_speed",0,5,40,40,14);
    SGPS_speed.setLabel("Speed-cm/s");
    //SGPS_speed.setColorBackground(blue_);
    SGPS_speed.setMin(0);
    SGPS_speed.setDirection(Controller.HORIZONTAL);
    SGPS_speed.setMax(10000);
    SGPS_speed.setDecimalPrecision(0);
    SGPS_speed.setGroup(SGGPS); 
 ScontrolP5.getController("SGPS_speed").getCaptionLabel()
   .align(ControlP5.LEFT, ControlP5.RIGHT_OUTSIDE).setPaddingX(45);   
 
 SGPS_distanceToHome = ScontrolP5.addNumberbox("SGPS_distanceToHome",0,5,60,40,14);
    SGPS_distanceToHome.setLabel("Dist Home-M");
    //SGPS_numSat.setColorBackground(red_);
    SGPS_distanceToHome.setMin(0);
    SGPS_distanceToHome.setDirection(Controller.HORIZONTAL);
    SGPS_distanceToHome.setMax(1000);
    SGPS_distanceToHome.setDecimalPrecision(0);
    SGPS_distanceToHome.setGroup(SGGPS); 
 ScontrolP5.getController("SGPS_distanceToHome").getCaptionLabel()
   .align(ControlP5.LEFT, ControlP5.RIGHT_OUTSIDE).setPaddingX(45);   
                 
  SGPSHeadHome = ScontrolP5.addKnob("SGPSHeadHome")
   .setRange(-180,+180)
   .setValue(0)
   .setPosition(140,5)
   .setRadius(25)
   .setLabel("Home")
   .setColorBackground(color(0, 160, 100))
   .setColorActive(color(255,255,0))
   .setDragDirection(Knob.HORIZONTAL)
   .setGroup(SGGPS);
  ScontrolP5.getTooltip().register("SGPSHeadHome","Home direction Simulation")
   ;

                 
               
               
 MW_Pitch_Roll = ScontrolP5.addSlider2D("MWPitch/Roll")
    .setPosition(25,5)
    .setSize(50,50)
    .setArrayValue(new float[] {50, 50})
    .setMaxX(20) 
    .setMaxY(-20) 
    .setMinX(-20) 
    .setMinY(20)
    .setValueLabel("") 
    .setLabel("Roll/Pitch")
    .setGroup(SGAtitude)
    ;
 ScontrolP5.getController("MWPitch/Roll").getCaptionLabel()
   .align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);   
 ScontrolP5.getController("MWPitch/Roll").getValueLabel().hide();
 ScontrolP5.getTooltip().register("MWPitch/Roll","Attitude / Horizon Simulator")
 ;
 
 HeadingKnob = ScontrolP5.addKnob("MwHeading")
   .setRange(-180,+180)
   .setValue(0)
   .setPosition(140,5)
   .setRadius(25)
   .setLabel("Heading")
   .setColorBackground(color(0, 160, 100))
   .setColorActive(color(255,255,0))
   .setDragDirection(Knob.HORIZONTAL)
   .setGroup(SGAtitude);
  ScontrolP5.getTooltip().register("MwHeading","Heading Simulation")
   ;
   
 Throttle_Yaw = ScontrolP5.addSlider2D("Throttle/Yaw")
         .setPosition(15,9)
         .setSize(50,50)
         .setArrayValue(new float[] {50, 100})
         .setMaxX(2000) 
         .setMaxY(1000) 
         .setMinX(1000) 
         .setMinY(2000)
         .setValueLabel("") 
        .setLabel("")
         .setGroup(SGRadio)
        ;
 ScontrolP5.getController("Throttle/Yaw").getValueLabel().hide();
 ScontrolP5.getTooltip().register("Throttle/Yaw","Ctrl Key to hold position");

UnlockControls =  ScontrolP5.addCheckBox("UnlockControls",83,25);
    UnlockControls.setColorBackground(color(80));
    UnlockControls.setColorActive(color(255,255,0));
    UnlockControls.addItem("UnlockControls1",1);
    UnlockControls.hideLabels();
    UnlockControls.setGroup(SGRadio);

  Pitch_Roll = ScontrolP5.addSlider2D("Pitch/Roll")
         .setPosition(110,9)
         .setSize(50,50)
         .setArrayValue(new float[] {50, 50})
         .setMaxX(2000) 
         .setMaxY(1000) 
         .setMinX(1000) 
         .setMinY(2000)
         .setLabel("")
         .setGroup(SGRadio)
         ;
  ScontrolP5.getController("Pitch/Roll").getValueLabel().hide();
               
s_Altitude = ScontrolP5.addSlider("sAltitude")
  .setPosition(5,10)
  .setSize(8,75)
  .setRange(0,1000)
  .setValue(0)
  .setLabel("Alt")
  .setDecimalPrecision(0)
  .setGroup(SGSensors1);
  ScontrolP5.getController("sAltitude").getValueLabel()
    .setFont(font9);
  ScontrolP5.getController("sAltitude").getCaptionLabel()
    .setFont(font9)
    .align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  ScontrolP5.getTooltip().register("sAltitude","Barometric Altitude Simulation");

s_Vario = ScontrolP5.addSlider("sVario")
  .setPosition(47,10)
  .setSize(8,75)
  .setRange(-40,40)
  .setNumberOfTickMarks(41)
  .showTickMarks(false)
  .setValue(0)
  .setLabel("Vario")
  .setDecimalPrecision(1)
  .setGroup(SGSensors1);
  ScontrolP5.getController("sVario").getValueLabel()
    .setFont(font9);
  ScontrolP5.getController("sVario").getCaptionLabel()
    .setFont(font9)
    .align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  ScontrolP5.getTooltip().register("sVario","Vertical Speed simulation");

s_VBat = ScontrolP5.addSlider("sVBat")
  .setPosition(90,10)
  .setSize(8,75)
  .setRange(7,25.2f)
  .setValue(12.6f)
  .setLabel("VBat")
  .setDecimalPrecision(1)
  .setGroup(SGSensors1);
  ScontrolP5.getController("sVBat").getValueLabel()
    .setFont(font9);
  ScontrolP5.getController("sVBat")
    .getCaptionLabel()
    .setFont(font9)
    .align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0); 
  ScontrolP5.getTooltip().register("sVBat","MWii VBat Simulation");    

s_MRSSI = ScontrolP5.addSlider("sMRSSI")
  .setPosition(130,10)
  .setSize(8,75)
  .setRange(0,1023)
  .setValue(1023)
  .setLabel("RSSI")
  .setDecimalPrecision(0)
  .setGroup(SGSensors1);
  ScontrolP5.getController("sMRSSI").getValueLabel()
     .setFont(font9);
  ScontrolP5.getController("sMRSSI")
    .getCaptionLabel()
    .setFont(font9)
    .align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  ScontrolP5.getTooltip().register("sMRSSI","RSSI via MWii Simulation");
    
  for(int i=0;i<boxids.length ;i++) {
    toggleModeItems[i] = (controlP5.Toggle) hideLabel(ScontrolP5.addToggle("toggleModeItems"+i,false));
    toggleModeItems[i].setPosition(5,3+i*17);
    toggleModeItems[i].setSize(10,10);
    toggleModeItems[i].setGroup(SGModes);
    txtlblModeItems[i] = controlP5.addTextlabel("ModeItems"+i,boxids[i].substring(0, boxids[i].length()-1) ,20,i*17);
    txtlblModeItems[i].setGroup(SGModes);
  } 
} 

public boolean checkKey(int k)
{
  if (keys.length >= k) {
    return keys[k];  
  }
  return false;
}

public void keyPressed()
{ 
  keys[keyCode] = true;
  //println(keyCode);
  if((checkKey(CONTROL) == true) && (checkKey(85) == true)){
  //SketchUploader();
  }
}

public void keyReleased()
{ 
  keys[keyCode] = false; 
  ControlLock();
  
}

public String RightPadd(int inInt,int Places){
  String OutString = nf(inInt,Places).replaceFirst("^0+(?!$)",  " ");
  for(int X=0; X<=3; X++) {
    if (OutString.length() < Places){ OutString = " " + OutString;}
  }
  return OutString;  
  
}


public void ControlLock(){
  Pitch_Roll.setArrayValue(new float[] {500, -500});
  if(checkKey(CONTROL) == false) {
    if(UnlockControls.arrayValue()[0] < 1){
      float A = (2000-Throttle_Yaw.getArrayValue()[1])*-1;
      Throttle_Yaw.setArrayValue(new float[] {500, A});
      s_Vario.setValue(0);
      sVario = 0;
    }
  }    
}


byte[][] raw_font;
PrintWriter  Output;

public PImage LoadFont(String filename) {
  //System.out.println("LoadFont "+filename);
  raw_font = LoadRawFont(filename);
  return RawFontToImage(raw_font);
}


public byte[][] LoadRawFont(String filename) {
  byte[][] raw = new byte[256][54];

  InputStream in = null;
  
  byte[] header = { 'M','A','X','7','4','5','6' };
  boolean inHeader = true;
  int hIndex = 0;
  int bitNo = 0;
  int byteNo = 0;
  int charNo = 0;
  int curByte = 0;
 
  try {
    in = createInput(filename);
    
    while(in.available() > 0) {
      int inB = in.read();
     
      if(inHeader) {
        if(hIndex < header.length && header[hIndex] == inB) {
          hIndex++;
          continue;
        }
        if(hIndex == header.length && (inB == '\r' || inB == '\n')) {
          inHeader = false;
          //System.out.println("done header");
          continue;
        }
        hIndex = 0;
        continue;
      }
      else {
        switch(inB) {
        case '\r':
        case '\n':
          if (bitNo == 0)
            continue; 
          if (bitNo == 8) {
            if (byteNo < 54) {
              raw[charNo][byteNo] = (byte)curByte;
            }
            bitNo = 0;
          
            curByte = 0;
            ++byteNo;
            if(byteNo == 64) {
              byteNo = 0;
              ++charNo;
            }
          }
          break;
        case '0':
        case '1':
          if(bitNo >= 8) {
            throw new Exception("File format error");
          }
          curByte = (curByte << 1) | (inB & 1);
          ++bitNo;
          break;
        }
      }
    }
  }
  catch (FileNotFoundException e) {
      System.out.println("File Not Found "+filename);
  }
  catch (IOException e) {
      System.out.println("IOException");
  }
  catch(Exception e) {
      System.out.println("Exception");
  }
  finally {
    if(in != null)
      try {
        in.close();
      }
      catch (IOException ioe) {
      }
  }
  
  return raw;
}

public PImage RawFontToImage(byte[][] raw) {
  PImage img = createImage(12, 18*256, ARGB);
  img.loadPixels();

  // Pixel values
  int white = 0xFFFFFFFF;
  int black = 0xFF000000;
  int transparent = 0x00000000;
  int gray = color(120);

  for(int charNo = 0; charNo < 256; charNo++) {
    CharImages[charNo].loadPixels();
    for(int byteNo = 0; byteNo < 54; byteNo++) {
      for(int i = 0; i < 4; i++) {
	int index = (charNo*12*18) + (byteNo*4) + (3-i);
        int CharIndex = (byteNo*4) + (3-i);
        int curByte = (int)raw[charNo][byteNo];
	switch((curByte >> (2*i)) & 0x03) {
	case 0x00:
	   img.pixels[index] = black;
            CharImages[charNo].pixels[CharIndex] = black;
	   break; 
	case 0x01:
	   img.pixels[index] = transparent;
            CharImages[charNo].pixels[CharIndex] = gray;
	   break; 
	case 0x02:
	   img.pixels[index] = white;
            CharImages[charNo].pixels[CharIndex] = white;
	   break; 
	case 0x03:
	   img.pixels[index] = transparent;
	   break; 
	}
      }
    }
    CharImages[charNo].updatePixels();
  }

  img.updatePixels();
  return img;
}

public void CreateFontFile(){
  String gray = "01";  // gray in Gui transparrent in OSD
  String black = "00"; //black
  String white = "10";
  int PixelCounter = 0;
  int fullpixels = 0;
  String OutputLine = "";
  
  Output = createWriter("data/MW_OSD_Team.mcm");
  
  Output.println("MAX7456"); // write header
  for(int id = 0; id < 256; id++) {
    for(int byteNo = 0; byteNo < 216; byteNo++) {
      switch(CharImages[id].pixels[byteNo]) {
        case 0xFF000000:
          OutputLine+=black;
          PixelCounter+=1;
        break; 
        case 0xFFFFFFFF:
          OutputLine+=white;
          PixelCounter+=1;
        break;
       default:
         OutputLine+=gray;
         PixelCounter+=1;
        break;  
      }
      
    if(PixelCounter == 4){
      Output.println(OutputLine);
      OutputLine = "";
      PixelCounter = 0;
    }
     
    }
    for(int spacer = 0; spacer < 10; spacer++) {
      Output.println("01010101");
    }
  }
  
 //Output.println("done");
 Output.flush(); // Writes the remaining data to the file
  Output.close();  
  img_Clear = LoadFont("MW_OSD_Team.mcm");  
}


  
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "KV_Team_GUI" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
