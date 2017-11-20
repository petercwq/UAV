
#define POS_MASK        0x01FF
#define PAL_MASK        0x0003
#define PAL_SHFT             9
#define DISPLAY_MASK    0xC000
#define DISPLAY_ALWAYS  0xC000
#define DISPLAY_NEVER   0x0000
#define DISPLAY_COND    0x4000
#define DISPLAY_CONDR   0x8000

#define POS(pos, pal_off, disp)  (((pos)&POS_MASK)|((pal_off)<<PAL_SHFT)|(disp))

const uint16_t screenPosition[] PROGMEM = {
  POS(LINE02+2,  0, DISPLAY_ALWAYS), // GPS_numSatPosition
  POS(LINE03+14, 0, DISPLAY_ALWAYS), // GPS_directionToHomePosition
  POS(LINE02+24, 0, DISPLAY_ALWAYS), // GPS_distanceToHomePosition
  //POS(LINE03+24, 0, DISPLAY_ALWAYS), // speedPosition
  POS(LINE07+3, 0, DISPLAY_ALWAYS), // speedPosition
  POS(LINE04+12, 0, DISPLAY_ALWAYS), // GPS_angleToHomePosition
  POS(LINE10+19, 0, DISPLAY_ALWAYS), // MwGPSAltPosition
  //POS(LINE03+24, 0, DISPLAY_ALWAYS), // MwGPSAltPosition
  POS(LINE03+2,  0, DISPLAY_ALWAYS), // sensorPosition
  POS(LINE02+19, 0, DISPLAY_ALWAYS), // MwHeadingPosition
  POS(LINE02+10, 0, DISPLAY_ALWAYS), // MwHeadingGraphPosition
  POS(LINE07+24,  1, DISPLAY_ALWAYS), // MwAltitudePosition
  //POS(LINE07+22, 1, DISPLAY_ALWAYS), // MwClimbRatePosition
  POS(LINE07+21, 1, DISPLAY_ALWAYS), // MwClimbRatePosition
  POS(LINE12+25, 2, DISPLAY_ALWAYS), // CurrentThrottlePosition
  POS(LINE09+26, 2, DISPLAY_ALWAYS), // ThrottleGraphPosition
  POS(LINE13+22, 2, DISPLAY_ALWAYS), // flyTimePosition
  POS(LINE13+22, 2, DISPLAY_ALWAYS), // onTimePosition
  POS(LINE12+11, 2, DISPLAY_ALWAYS), // motorArmedPosition
  POS(LINE10+2,  2, DISPLAY_CONDR),  // MwGPSLatPosition
  POS(LINE10+15, 2, DISPLAY_CONDR),  // MwGPSLonPosition
  POS(LINE12+2,  2, DISPLAY_ALWAYS), // rssiPosition
  POS(LINE11+2,  0, DISPLAY_ALWAYS), // temperaturePosition
  POS(LINE13+3,  2, DISPLAY_ALWAYS), // voltagePosition
  POS(LINE12+3,  2, DISPLAY_ALWAYS), // vidvoltagePosition
  POS(LINE13+10, 2, DISPLAY_ALWAYS), // amperagePosition
  POS(LINE13+16, 2, DISPLAY_ALWAYS), // pMeterSumPosition
  POS(LINE05+8,  1, DISPLAY_CONDR),  // horizonPosition
  POS(LINE12+10, 2, DISPLAY_ALWAYS), // CallSign Position
  
};

uint16_t getPosition(uint8_t pos) {
  uint16_t val = (uint16_t)pgm_read_word(&screenPosition[pos]);
  uint16_t ret = val&POS_MASK;

  if(Settings[S_VIDEOSIGNALTYPE]) {
    ret += LINE * ((val >> PAL_SHFT) & PAL_MASK);
  }

  return ret;
}

uint8_t fieldIsVisible(uint8_t pos) {
  uint16_t val = (uint16_t)pgm_read_word(&screenPosition[pos]);
  switch(val & DISPLAY_MASK) {
  case DISPLAY_ALWAYS:
    return 1;
  case DISPLAY_NEVER:
    return 0;
  case DISPLAY_COND:
    return !!(MwSensorActive&mode_osd_switch);
  case DISPLAY_CONDR:
    return !(MwSensorActive&mode_osd_switch);
  }
}
