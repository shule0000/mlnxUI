package com.medlinx.core.alarm;

public class Alarm {
    private int alarmLevel;
    private String alarmMessage;
    private AlarmType type;
    public int getAlarmLevel() {
        return alarmLevel;
    }
    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }
    public String getAlarmMessage() {
        return alarmMessage;
    }
    public void setAlarmMessage(String alarmMessage) {
        this.alarmMessage = alarmMessage;
    }
    public AlarmType getType() {
        return type;
    }
    public void setType(AlarmType type) {
        this.type = type;
    }
    
    public enum AlarmType {
        PHYSIOLOGICAL, TECHNICAL
    }
    
    public Alarm(int _alarmLevel, String _alarmMessage, AlarmType _type)
    {
        alarmLevel = _alarmLevel;
        alarmMessage = _alarmMessage;
        type = _type;
    }
    
    public static final Alarm BPMLOWALARM = new Alarm(1,"心率过低",AlarmType.PHYSIOLOGICAL);
    public static final Alarm BPMHIGHALARM = new Alarm(1,"心率过高",AlarmType.PHYSIOLOGICAL);
    public static final Alarm BATTERYLOWALARM = new Alarm(3,"电池余量不足",AlarmType.TECHNICAL);
    public static final Alarm WIFILOWALARM = new Alarm(3,"无线信号过低",AlarmType.TECHNICAL);
    public static final Alarm HEADERLOSTALARM = new Alarm(2,"电极脱落",AlarmType.TECHNICAL);
    public static final Alarm FIDELITYALARM = new Alarm(2,"偏压过高波形失真",AlarmType.TECHNICAL);
}
