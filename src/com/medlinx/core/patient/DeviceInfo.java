package com.medlinx.core.patient;

import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;

public class DeviceInfo implements Cloneable{
	
	private com.mlnx.pms.core.Device device;
	private DataType dataType;
	private String deviceID;
	private Mode mode;
//	private Position position;
	
	public static final int ECG_COMMON_FREQUENCY = 300; //standard frequency of ECG = 300
	public static final int ECG_OPER_FREQUENCY = 75;// operating frequency of ECG = 75
	public static final int ECG_NORMAL_FREQUENCY = 150;// operating frequency of ECG = 150
	
	public static String[] modeStrs = { "高精度ECG", "尖峰监测", "心电图机", "普通监护",
	"手术监护" };
	public static Mode[] modeOptions = {Mode.ECG_ADVANCED,Mode.ECG_ADVANCED_WITH_SPIKE_DETECT,
			Mode.ECG_ELECTROCARDIOGRAPH,Mode.ECG_NORMAL,Mode.ECG_OPERATING_ROOM};
	
	public DeviceInfo(com.mlnx.pms.core.Device device) {

		this.device = device;
		dataType = device.getDataType();
		deviceID = device.getId();
		mode = device.getMode();
//		position = device.getPosition();
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public com.mlnx.pms.core.Device getDevice() {
		return device;
	}

	public void setDevice(com.mlnx.pms.core.Device device) {
		this.device = device;
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getModeName() {
		for (int i = 0; i < modeOptions.length; ++i)
		{
			if (mode == modeOptions[i])
				return modeStrs[i];
		}
		return "无";
	}
	
	public int getFrequency()
	{
		int frequencyTemp = 0;
		if(mode==Mode.ECG_ADVANCED||
				mode==Mode.ECG_ADVANCED_WITH_SPIKE_DETECT||
						mode==Mode.ECG_ELECTROCARDIOGRAPH)
		{
			frequencyTemp = ECG_COMMON_FREQUENCY;
		}
		if(mode==Mode.ECG_NORMAL)
		{
			frequencyTemp = ECG_NORMAL_FREQUENCY;
		}
		if(mode==Mode.ECG_OPERATING_ROOM)
		{
			frequencyTemp = ECG_OPER_FREQUENCY;
		}
		return frequencyTemp;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}
