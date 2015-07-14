package com.medlinx.core.databuff;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.dialog.WaitingTipDialog;
import ui.medlinx.com.extra.SettingParameters;

import com.medlinx.core.client.DataClientFactory;
import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.datafactory.DataFactory;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.ServerProcessingException;
import com.mlnx.pms.client.data.RealTimeEcgRequest;
import com.mlnx.pms.client.data.RealTimeEcgResponse;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;
import com.mlnx.pms.core.EcgLead;
import com.mlnx.pms.core.HttpConstants;

public class MultiChannelBuffer implements DataBufferInterface {

	private static final DateFormat dateFormat = new SimpleDateFormat(
			HttpConstants.TIMESTAMP_FORMAT);
	private final EcgLead[] leadsTemp = { EcgLead.I, EcgLead.II, EcgLead.III,
			EcgLead.aVR, EcgLead.aVL, EcgLead.aVF, EcgLead.V1, EcgLead.V2,
			EcgLead.V3, EcgLead.V4, EcgLead.V5, EcgLead.V6 };
	private EcgLead[] leads;
	private DataClient dataClient = null;
	public int frequency;
	public int secondBuffer = 150; // buffer size is 300 sec
	private ArrayList<int[]> bufferList;
	private String connectStr;
	ReadThreadMultiple readthread;
	private boolean reading, exceptionFlag;
	private boolean isLoading;
	private Patient patient;
	private int currentIndex;
	long countPT, startT;
	private long retryIntervalMillis;
	private long retryTimeoutMillis;
	String fileName;
	int[] dataPool;
	int lengthData;
	public int maxElement;
	private Date startTime;
	private Date endTime;
	private int currentChannel;
	// some patient/device status information
	private int bpm; // heart beat per min
	private int motionCode; // code to indicate motion mode
	private Mode deviceMode; // mode of the device
	private DataType deviceDataType; // data type of the device
	/**
	 * value to indicate the time of last call from patient
	 */
	private Date lastCallTime;
	/**
	 * Value is an int (between 0 and 255). See the definition of the
	 * "Wi-Fi Signal Strength" field in the VSTP Specification.
	 */
	private int rssi; // wifi signal strength index
	/**
	 * Value is an int (between 0 and 100). It represents the percentage of
	 * battery remaining. See the definition of the "Battery Remaining (BATT)"
	 * field in the VSTP Specification.
	 */
	private int battery;
	/**
	 * Value is an int. Its lower 10 bits represent the connection status of
	 * each of the 10 electrodes (0: connected, 1: disconnected). See the
	 * definition of the "Electrode Impedance Bits (CxIMP)" field in the VSTP
	 * Specification.
	 */
	private int electrodeHeader;
	private int fidelity;

	public MultiChannelBuffer(Patient _patient) {
		if (_patient == null || _patient.getChannelNum() <= 0) {
			DebugTool.printLogDebug("null patient or zero channel.");
			return;
		}
		fileName = "./data/dataInput.txt";
		lengthData = 2805;
		isLoading = false;
		dataPool = new int[lengthData * 8];
		patient = _patient;
		// frequency is initialized as 300, but determined by mode
		SettingParameters parameters = SettingParameters.getInstance();
		// init device
		frequency = parameters.getDataFrequency();
		if (patient != null && patient.getDevInfo() != null) {
			deviceMode = patient.getDevInfo().getMode();
			deviceDataType = patient.getDevInfo().getDataType();
			frequency = mode2Frequency();
		}
		maxElement = frequency * secondBuffer;
		currentChannel = 0;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		exceptionFlag = false;
		startTime = null;
		endTime = null;
		leads = null;
		// create and initialize mutli-channel buffer
		bufferList = new ArrayList<int[]>();
		bpm = SystemConstant.BPM_INVALIDVALUE;
		motionCode = 71;
		lastCallTime = patient.getLastCallTime();
		rssi = 4;
		battery = 100;
		electrodeHeader = 0;
		fidelity = 0;
		retryTimeoutMillis = 300000;// time out for response
		retryIntervalMillis = 0;// time interval for request, 0 means no retry
		for (int indexC = 0; indexC < patient.getChannelNum(); ++indexC) {
			int[] tempIntArray = new int[maxElement];
			for (int j = 0; j < secondBuffer; ++j) {
				int startP = j * frequency;
				for (int i = startP; i < (startP + frequency); ++i) {
					tempIntArray[i] = SystemConstant.ECG_INVALIDVALUE;
				}
			}
			bufferList.add(tempIntArray);
		}
		reading = true;
	}

	/**
	 * given mode, return device frequency
	 */
	private int mode2Frequency() {
		int frequencyTemp = 0;
		if (deviceMode == Mode.ECG_ADVANCED
				|| deviceMode == Mode.ECG_ADVANCED_WITH_SPIKE_DETECT
				|| deviceMode == Mode.ECG_ELECTROCARDIOGRAPH) {
			frequencyTemp = SystemConstant.ECG_COMMON_FREQUENCY;
		}
		if (deviceMode == Mode.ECG_NORMAL) {
			frequencyTemp = SystemConstant.ECG_NORMAL_FREQUENCY;
		}
		if (deviceMode == Mode.ECG_OPERATING_ROOM) {
			frequencyTemp = SystemConstant.ECG_OPER_FREQUENCY;
		}
		return frequencyTemp;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public void start() {
		reading = true;
		readthread = new ReadThreadMultiple();
		readthread.start();
		startT = System.currentTimeMillis();
	}

	// erase all data (set to zero)
	private void eraseData() {
		for (int j = 0; j < bufferList.size(); ++j) {
			for (int i = 0; i < bufferList.get(j).length; ++i)
				bufferList.get(j)[i] = SystemConstant.ECG_INVALIDVALUE;
			;
		}

	}

	public void loadData(Date _startDate) {
		// dateStr = "2013-07-11 00:00:00.000";
		startTime = _startDate;
		endTime = new Date(startTime.getTime() + secondBuffer * (1000) + 2000);// 2s
																				// to
																				// make
																				// sure
																				// enough
																				// data
		System.out.printf("History data for the following patient"
				+ " between %s and %s " + " will be retrieved:\n",
				dateFormat.format(startTime), dateFormat.format(endTime));
	}

	public boolean checkReadingStatus() {
		return reading;
	}

	@Override
	public void end() {

		reading = false;
		try {
			readthread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DebugTool.printLogDebug("readthread.join();");
	}

	@Override
	public void suspend() {

	}

	@Override
	public void resume() {

	}

	@Override
	public int checkStatus() {
		return 0;
	}

	@Override
	public int getCurrentSecond() {
		// TODO Auto-generated method stub
		int outputSecond = currentIndex / frequency;
		outputSecond = outputSecond % secondBuffer;
		return outputSecond;
	}

	@Override
	public int getLocalMeanChannel(int start, int width, int Channel) {
		// TODO Auto-generated method stub
		if (start < 0)
			start += maxElement;
		start = start % (maxElement);
		int meanV = 0;
		for (int i = start; i < (start + width); ++i) {
			if (i % maxElement >= maxElement)
				i++;
			meanV += bufferList.get(Channel)[i % maxElement];
		}
		return meanV / width;
	}

	@Override
	public boolean isExceptionFlag() {
		// TODO Auto-generated method stub
		return exceptionFlag;
	}

	@Override
	public void setExceptionFlag(boolean _exceptionFlag) {
		// TODO Auto-generated method stub
		exceptionFlag = _exceptionFlag;
	}

	@Override
	public void setNChannel(int nChannel) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNChannel() {
		// TODO Auto-generated method stub
		return patient.getChannelNum();
	}

	@Override
	public String getConnectStr() {
		// TODO Auto-generated method stub
		return connectStr;
	}

	@Override
	public void setConnectStr(String _connectStr) {
		// TODO Auto-generated method stub
		connectStr = _connectStr;
	}

	@Override
	public Patient getPatient() {
		// TODO Auto-generated method stub
		return patient;
	}

	@Override
	public void setPatient(Patient p) {
		// TODO Auto-generated method stub
		patient = p;
	}

	@Override
	public int getFrequency() {
		// TODO Auto-generated method stub
		return frequency;
	}

	@Override
	public void setFrequency(int _frequency) {
		// TODO Auto-generated method stub
		// this function will stop the reading thread. So need to call start()
		// again.
		frequency = _frequency;
		end();// stop reading
		// re-allocate memory and initialize data
		currentChannel = 0;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		exceptionFlag = false;
		startTime = null;
		endTime = null;
		leads = null;
		maxElement = frequency * secondBuffer;

		bufferList = new ArrayList<int[]>();
		for (int indexC = 0; indexC < patient.getChannelNum(); ++indexC) {
			int[] tempIntArray = new int[maxElement];
			for (int j = 0; j < secondBuffer; ++j) {
				int startP = j * frequency;
				for (int i = startP; i < (startP + frequency); ++i) {
					tempIntArray[i] = SystemConstant.ECG_INVALIDVALUE;
				}
			}
			bufferList.add(tempIntArray);
		}
		DebugTool.printLogDebug("bufferList.size() = " + bufferList.size());
		reading = true;
		start();
	}

	@Override
	public int getTimeWindow() {
		// TODO Auto-generated method stub
		return secondBuffer;
	}

	@Override
	public void setTimeWindow(int _timesecondBuffer) {
		// TODO Auto-generated method stub
		secondBuffer = _timesecondBuffer;
	}

	// private class for reading, multiple reading each session. i.e. send
	// multiple requests
	// instead of chunk
	private class ReadThreadMultiple extends Thread {
		int countDown = 2000;
		FileWriter out = null;

		public void run() {
			File directory = new File("");// 设定为当前文件夹
			String path = directory.getAbsolutePath();
			File file = new File(path + "/temp.txt");

			try {
				out = new FileWriter(file, true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// construct data client object
			isLoading = true;
			dataClient = DataClientFactory.getLoginDataClient();
			if (dataClient == null || patient == null) {
				DebugTool.printLogDebug("dataClient or patient is null");
				return;
			}
			// construct the leads
			leads = new EcgLead[patient.getChannelNum()];
			int indexLead = 0;
			for (int i = 0; i < leadsTemp.length; ++i) {
				if (patient.channelFlag[i]) {
					leads[indexLead] = leadsTemp[i];
					++indexLead;
				}
			}
			eraseData(); // remove all data points in buffer

			int sleepTime = 200; // send a request every sleepTime
			while (countDown > 0 && reading) {
				try {
					RealTimeEcgRequest request = dataClient.realTimeEcg()
							.withPatientId(patient.getPatientID())
							.addLeads(leads)
							.withRetryInterval(retryIntervalMillis)// no retry
							.withRetryTimeout(retryTimeoutMillis).build();
					RealTimeEcgResponse response = request.get();// may throw
																	// ServerProcessingException
					WaitingTipDialog.closeDialog(); // 已经接收到数据关闭等待对话框

					countDown = 2000;
					exceptionFlag = false;
					while (reading) {
						handleResponse(response);
						// waiting for sleepTime for reading data
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						response = response.next();// //may throw
													// ServerProcessingException

					}
				} catch (ServerProcessingException e) {
					DebugTool.printLogDebug("server processing exception!!");
					exceptionFlag = true;
					--countDown;
					e.printStackTrace();
				} catch (IOException e) {
					DebugTool.printLogDebug("IO exception!!");
					exceptionFlag = true;
					--countDown;
					if (reading) {
						if (dataClient != null) {
							dataClient.close();
							dataClient = null;
						}
						dataClient = DataClientFactory.getLoginDataClient();
					}
					e.printStackTrace();
				}

				// waiting for 200ms for reading data
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			if (dataClient != null)
				dataClient.close();
			dataClient = null;
			DebugTool.printLogDebug("Over~");
		}

		private int handleResponse(RealTimeEcgResponse response)
				throws IOException {
			int countNum = 0; // how many pt red from server(count on one
								// certain channel).
			// Read response header info
			Integer bpmTemp = response.getHeartRate();
			if (bpmTemp != null) {
				if (bpmTemp.intValue() != 0)
					bpm = bpmTemp.intValue();
			} else {
				// DebugTool.printLogDebug("heart rate is null!!");
			}
			Integer motionCodeTemp = response.getPose();
			if (motionCodeTemp != null) {
				if (motionCodeTemp.intValue() != 0)
					motionCode = motionCodeTemp.intValue();
				// DebugTool.printLogDebug("heart rate:"+motionCodeTemp);
			}
			rssi = response.getSignalStrength();
			electrodeHeader = response.getConnectivity();
			battery = response.getBatteryRemaining();
			fidelity = response.getFidelity();
			if (response.getLastEmergencyCallTime() != null)
				lastCallTime = response.getLastEmergencyCallTime();

			/*
			 * need to retrieval the alarm information here
			 */
			DataInputStream dataIn = response.getDataInputStream();
			try {
				while (reading) {
					int value = dataIn.readShort() & 0xFFFF;

					out.append(value + "\r\n");

					out.flush();
					bufferList.get(currentChannel)[currentIndex] = value;
					currentChannel++;
					if (currentChannel == patient.getChannelNum()) {
						currentChannel = 0;
						currentIndex++;
						countNum++;
						currentIndex = currentIndex
								% (frequency * secondBuffer);
					}
					countPT++;
					// eofexception
					if (currentIndex % 1000 == 0) //
					{
						// DebugTool.printLogDebug(" "+ currentIndex+ ":" +
						// value);
						int pps = (int) ((countPT * 1000.0f) / (System
								.currentTimeMillis() - startT));
						// DebugTool.printLogDebug(patient.getPatientID()+"pt per second: "+pps+
						// " ; " + value);
					}
				}

				out.close();
			} catch (EOFException e) {
				// DebugTool.printLogDebug("No more data to read");
				// countDown = 10;
			} finally {
				dataIn.close();
			}
			return countNum;
		}
	}

	@Override
	public int getCurrentPT() {
		// TODO Auto-generated method stub
		return currentIndex;
	}

	@Override
	public int getSecondBuffer() {
		// TODO Auto-generated method stub
		return secondBuffer;
	}

	@Override
	public void setSecondBuffer(int _secondBuffer) {
		// TODO Auto-generated method stub
		secondBuffer = _secondBuffer;
	}

	@Override
	public int getBPM() {
		// TODO Auto-generated method stub
		return bpm;
	}

	@Override
	public void setBPM(int _BPM) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMotionCode() {
		// TODO Auto-generated method stub
		return motionCode;
	}

	@Override
	public void setMotionCode(int _MotionCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWiFiSignalStrength() {

		return rssi;
	}

	@Override
	public int getElectrodeHeader() {
		// TODO Auto-generated method stub
		return electrodeHeader;
	}

	@Override
	public int getBattery() {
		// TODO Auto-generated method stub
		return battery;
	}

	@Override
	public Date getCallTime() {
		// TODO Auto-generated method stub
		return lastCallTime;
	}

	@Override
	public boolean checkAlarm() {
		Date currentTime = Calendar.getInstance().getTime();
		// two conditions need to be satisfied to invoke the alarm
		// this call is after the most recent call time of this patient
		// this call is within the past 1 minute
		long threshold = 30000;
		currentTime = new Date(currentTime.getTime() - threshold);
		Date after5Min = new Date(currentTime.getTime() + threshold);
		if (lastCallTime.after(patient.getLastCallTime())) {
			DebugTool.printLogDebug("after the last call"
					+ lastCallTime.toString());

		}
		if (lastCallTime.after(patient.getLastCallTime())
				&& lastCallTime.after(currentTime)
				&& lastCallTime.before(after5Min)) {
			DebugTool.printLogDebug("alarm on !!!!!!");
			return true;
		}
		return false;
	}

	// reset the last call time
	// indicate that this call has been handled already
	public void syncCallTime() {
		patient.setLastCallTime(lastCallTime);
	}

	public Date getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime(Date lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	@Override
	public int getFidelity() {
		return fidelity;
	}

	@Override
	public int getAmountPoint(int indexBuffer) {
		int tempAmount = (indexBuffer - currentIndex + frequency * secondBuffer)
				% (frequency * secondBuffer);
		if (tempAmount > (frequency * secondBuffer / 2))
			tempAmount = frequency * secondBuffer - tempAmount;
		return tempAmount;
	}

	public Mode getDeviceMode() {
		return deviceMode;
	}

	public void setDeviceMode(Mode deviceMode) {
		if (deviceMode == this.deviceMode)
			return;// no change

		this.deviceMode = deviceMode;

		com.mlnx.pms.core.Device device = patient.getDevInfo().getDevice();
		device.setMode(this.deviceMode);
		DebugTool.printLogDebug("patient id: " + device.getPatientId()
				+ " device id:" + device.getId());
		MlnxDoctorClient.configureDevice(device);

		DebugTool.printLogDebug("old freq:" + this.frequency);
		int newFrequency = mode2Frequency();
		if (newFrequency != frequency)
			setFrequency(newFrequency);
		DebugTool.printLogDebug("new freq:" + this.frequency);
	}

	public DataType getDeviceDataType() {
		return deviceDataType;
	}

	public void setDeviceDataType(DataType deviceDataType) {
		if (deviceDataType == this.deviceDataType)
			return;// no change
		this.deviceDataType = deviceDataType;
		DataClient dataClient = DataClientFactory.getLoginDataClient();
		boolean configException = false;
		try {
			patient.getDevInfo().getDevice().setDataType(this.deviceDataType);
			dataClient.configureDevice(patient.getDevInfo().getDevice());
			System.out.print("set up device: "
					+ patient.getDevInfo().getDevice().getId() + "");
		} catch (ServerProcessingException e) {
			configException = true;
			e.printStackTrace();
		} catch (IOException e) {
			configException = true;
			e.printStackTrace();
		} finally {
			if (configException)
				dataClient.close();
			else
				DataClientFactory.addDataClient(dataClient);
		}
	}
}
