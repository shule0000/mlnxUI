package com.medlinx.core.alarm;
/**
 * 报警类，记录报警的类型={生理报警，技术报警}，级别={高，中，低}，报警信息比如“心率过高”，“电池余量不足”等
 * @author bruce.bei
 *
 */
public class Alert {
    private String alertType;
    private String alertMessage;
    private int level;

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
