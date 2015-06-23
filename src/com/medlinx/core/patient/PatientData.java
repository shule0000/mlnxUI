package com.medlinx.core.patient;

import ui.medlinx.com.extra.SettingParameters;

import com.medlinx.core.databuff.DataBufferInterface;
import com.medlinx.core.databuff.MultiChannelBuffer;
import com.mlnx.pms.core.User;

public class PatientData {
	private Patient patient;
	private DataBufferInterface databuffer;

	public PatientData(Patient p) {
		patient = p;
	}

	public int getBpm() {
		if (databuffer != null)
			return databuffer.getBPM();
		else
			return -1;
	}

	/**
	 * 返回电池余量，例如20表示20%电池剩余
	 * 
	 * @return
	 */
	public int getBattery() {
		if (databuffer != null)
			return databuffer.getBattery();
		else
			return 0;
	}

	/**
	 * strength of signal: [0,4]，4表示满格信号，0表示没有信号。
	 * 
	 * @return
	 */
	public int getWiFiSignalStrength() {
		if (databuffer != null)
			return databuffer.getWiFiSignalStrength();
		else
			return 0;
	}

	/**
	 * 返回导联脱落标志位，从低到高对应导联为 LA, LL, RA, RL, V1, V2, V3, V4, V5, V6
	 * 例如，0x1010101010表示LL, RL, V2, V4, V6脱落
	 * 参见DrawingPanel.updateElectrodeHeader
	 * 
	 * @return
	 */
	public int getElectrodeHeader() {
		if (databuffer != null)
			return databuffer.getElectrodeHeader();
		else
			return 0;
	}

	/**
	 * 体态数据标志位：motionCode 参见MLnXUI中DrawingPanel.updateMotionCode
	 * 
	 * @return
	 */
	public int getMotionCode() {
		if (databuffer != null)
			return databuffer.getMotionCode();
		else
			return 0;
	}

	public void start() {
		if (databuffer != null) {
			databuffer.end();
			databuffer = null;
		}
		databuffer = new MultiChannelBuffer(patient);
		databuffer.setConnectStr(SettingParameters.getInstance().getIpString());
		databuffer.setPatient(patient);
		databuffer.start();

	}

	public void end() {
		if (databuffer != null) {
			databuffer.end();
			databuffer = null;
		}
	}

	public DataBufferInterface getDataBuffer() {
		return databuffer;
	}
}
