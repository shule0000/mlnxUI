package com.medlinx.core.patient;

import java.util.Calendar;
import java.util.Date;

import ui.medlinx.com.frame.Main.DrawingPanel;
import ui.medlinx.com.frame.Main.MLnxClient;

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.databuff.MultiChannelBuffer;
import com.mlnx.pms.core.Patient.Gender;

/**
 * The data structure to store patient's information
 * 
 * @author jfeng
 * 
 */
public class Patient implements Cloneable {

	// infor
	private PatientInfo patientInfo;
	private DeviceInfo devInfo;
	private PatientData patientData;
	private PatientSort patientSort;
	private DrawingPanel drawingPanel;

	private Long groupID;
	public boolean[] channelFlag;
	private Date lastCallTime;
	private boolean isSelected;
	private boolean isOnline;

	public void setDrawingPanel(DrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
	}

	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	public Date getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime(Date lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	public PatientSort getPatientSort() {
		return patientSort;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected, MLnxClient mLnxClient) {
		if (this.isSelected == isSelected)
			return;
		this.isSelected = isSelected;
		if (isSelected) {
			// 开启databuffer
			this.patientData.start();
			drawingPanel = new DrawingPanel(mLnxClient, this.patientData.getDataBuffer());
		} else {
			// 关闭databuffer
			this.patientData.end();
			drawingPanel.endDraw();
			drawingPanel = null;
		}
	}

	public void setPatientData(PatientData patientData) {
		this.patientData = patientData;
	}

	public PatientData getPatientData() {
		return patientData;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public DeviceInfo getDevInfo() {
		return devInfo;
	}

	public void setDevInfo(DeviceInfo devInfo) {
		this.devInfo = devInfo;
	}

	/**
	 * constructor
	 */
	public Patient() {
		this(new PatientInfo(), (long) 0);
	}

	/**
	 * constructor [NY] Constructs a Patient from an existing PatientInfo
	 */
	public Patient(PatientInfo info, Long groupID) {
		patientInfo = info;
		patientData = new PatientData(this);
		this.groupID = groupID;
		channelFlag = new boolean[SystemConstant.ECGCHANNELNUM]; // 12 channel
		for (int i = 0; i < channelFlag.length; i++) {
			channelFlag[i] = true;
		}
		Date currentT = Calendar.getInstance().getTime();
		// set last call time as 10 min before to make sure it expires already
		lastCallTime = new Date(currentT.getTime() - (10 * 60 * 1000));
		patientSort = new PatientSort(this);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Patient temp = (Patient) super.clone();
		temp.patientInfo = (PatientInfo) patientInfo.clone();

		boolean[] flag = new boolean[SystemConstant.ECGCHANNELNUM];
		for (int i = 0; i < flag.length; ++i)
			flag[i] = this.channelFlag[i];
		temp.channelFlag = flag;
		return temp;
	}

	public String getGender() {
		return patientInfo.getGender().toString();
	}

	public boolean getChannelFlag(int channel) {
		if (channel >= SystemConstant.ECGCHANNELNUM)
			return false;
		return channelFlag[channel];
	}

	public void setChannelFlag(int channel) {
		if (channel >= SystemConstant.ECGCHANNELNUM)
			return;
		channelFlag[channel] = true;
	}

	/**
	 * turn off a channel (not read this channel)
	 * 
	 * @param channel
	 *            the index of channel need to turn off
	 */
	public void cancelChannelFlag(int channel) {
		if (channel >= SystemConstant.ECGCHANNELNUM)
			return;
		channelFlag[channel] = false;
	}

	public void setGender(String gender) {
		patientInfo.setGender(Gender.valueOf(gender));
	}

	@Override
	public String toString() {
		String temp = "";
		temp += this.getPatientName();
		temp += "(" + this.getPatientID() + ")";
		return temp;
	}

	public int getPatientID() {
		return patientInfo.getId();
	}

	public void setPatientID(int patientID) {
		patientInfo.setId(patientID);
	}

	public String getPatientName() {
		return patientInfo.getName();
	}

	public void setPatientName(String patientName) {
		patientInfo.setName(patientName);
	}

	public int getPatientAge() {
		return patientInfo.getAge();
	}

	public String getContact() {
		return patientInfo.getContact();
	}

	public String getLastFourNumber() {
		return patientInfo.getLastFourNumber();
	}

	public Long getGroupID() {
		return groupID;
	}

	public int getChannelNum() {
		int countChannel = 0;
		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			if (channelFlag[i])
				countChannel++;
		}
		return countChannel;
	}

	public String getLeadStr() {
		String tempStr = "";
		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			if (channelFlag[i])
				tempStr = tempStr + SystemConstant.ECGLEADNAMES.get(i) + ",";
		}
		tempStr = tempStr.substring(0, tempStr.length() - 1);
		return tempStr;
	}

	/**
	 * whether this patient is the same to compareP, i.e. same patient ID and
	 * same Channels: I change the strategy, only compare the id now
	 * 
	 * @param compareP
	 *            Another patient to be compared
	 * @return boolean whether they are the same
	 */
	public boolean sameCurve(Patient compareP) {
		if (compareP.getPatientID() != this.getPatientID()) {
			return false;
		}
		return true;
	}

	public boolean isSame(Patient compareP) {
		if (compareP.getPatientID() != this.getPatientID()) {
			return false;
		}
		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			if (channelFlag[i] && !compareP.getChannelFlag(i))
				return false;
			if (!channelFlag[i] && compareP.getChannelFlag(i))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Patient) {
			Patient patient = (Patient) obj;
			if (this.groupID == patient.getGroupID()
					&& this.getPatientID() == patient.getPatientID())
				return true;
			else
				return false;
		} else
			return false;
	}
}
