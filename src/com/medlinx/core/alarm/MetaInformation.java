package com.medlinx.core.alarm;

public class MetaInformation {
    public int heartbeat;// 心率
    public int batteryRemain;// 剩余电量
    public int headerDetached; // 脱落的电极
    public int rssi; // wifi信号强度。
    public int pressure;// 偏压值。

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getBatteryRemain() {
        return batteryRemain;
    }

    public void setBatteryRemain(int batteryRemain) {
        this.batteryRemain = batteryRemain;
    }

    public int getHeaderDetached() {
        return headerDetached;
    }

    public void setHeaderDetached(int headerDetached) {
        this.headerDetached = headerDetached;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

}
