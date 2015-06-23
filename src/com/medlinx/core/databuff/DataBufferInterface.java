package com.medlinx.core.databuff;

import java.util.Date;

import com.medlinx.core.patient.Patient;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;

public interface DataBufferInterface {
//control
void start();
void end();

void suspend();
void resume();

int checkStatus();

// data
int getCurrentSecond();
int getCurrentPT();
int getLocalMeanChannel(int start, int width,int Channel);

//setter and getter's
boolean isExceptionFlag();
void setExceptionFlag(boolean _exceptionFlag);

void setNChannel(int nChannel);
int getNChannel();

String getConnectStr();
void setConnectStr(String _conncetStr);

Patient getPatient();
void setPatient(Patient p);

int getFrequency();
void setFrequency(int _frequency);

int getTimeWindow();
void setTimeWindow(int _timesecondBuffer);

int getSecondBuffer();
void setSecondBuffer(int _secondBuffer);

int getBPM();
void setBPM(int _BPM);

int getMotionCode();
void setMotionCode(int _MotionCode);
int getWiFiSignalStrength();
int getElectrodeHeader();
int getBattery();
Date getCallTime();
boolean checkAlarm();
void syncCallTime();
int getFidelity();
int getAmountPoint(int indexBuffer);


Mode getDeviceMode();
void setDeviceMode(Mode deviceMode);
DataType getDeviceDataType();
void setDeviceDataType(DataType deviceDataType);
}
