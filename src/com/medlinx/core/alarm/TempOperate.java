package com.medlinx.core.alarm;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.Timer;

public class TempOperate {
    int countDownCancel,countDownMute,tempMuteFlag, tempCancelFlag;
    Timer countDownMuteTimer, countDownCancelTimer;
    ArrayList<JLabel> highAlarmLabel, mediumAlarmLabel, lowAlarmLabel;
    
    public void turnOnTempMute() {
        countDownMute = tempMuteFlag * 60;
        if (!countDownMuteTimer.isRunning())
            countDownMuteTimer.start();
    }

    public void turnOnTempCancel() {
        countDownCancel = tempCancelFlag * 60;
        if (!countDownCancelTimer.isRunning())
            countDownCancelTimer.start();
        highAlarmLabel.clear();
        mediumAlarmLabel.clear();
        lowAlarmLabel.clear();
    }

    public void turnOffTempMute() {
        if (countDownMuteTimer.isRunning())
            countDownMuteTimer.stop();
        countDownMute = 0;
        tempMuteFlag = 0;
    }

    public void turnOffTempCancel() {
        if (countDownCancelTimer.isRunning())
            countDownCancelTimer.stop();
        countDownCancel = 0;
        tempCancelFlag = 0;
    }

}
