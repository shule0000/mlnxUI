package com.medlinx.core.databuff;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ui.medlinx.com.debug.DebugTool;

import com.medlinx.core.client.DataClientFactory;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.data.EcgRequest;
import com.mlnx.pms.client.data.EcgResponse;
import com.mlnx.pms.client.data.NoDataForPatientException;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;
import com.mlnx.pms.core.EcgLead;
import com.mlnx.pms.core.HttpConstants;

public class HistoryDataBuffer implements DataBufferInterface {

	private static final DateFormat dateFormat = new SimpleDateFormat(
			HttpConstants.TIMESTAMP_FORMAT);
	private final EcgLead[] leadsTemp = { EcgLead.I, EcgLead.II, EcgLead.III,
			EcgLead.aVR, EcgLead.aVL, EcgLead.aVF, EcgLead.V1, EcgLead.V2,
			EcgLead.V3, EcgLead.V4, EcgLead.V5, EcgLead.V6 };
	private EcgLead[] leads;
	public int frequency = 300;
	public int secondBuffer = 150; // buffer size is 300 sec
	private ArrayList<int[]> bufferList;
	private String connectStr;
	ReadThread readthread;
	private boolean reading, exceptionFlag;
	private boolean isLoading;
	private Patient patient;
	private int currentIndex, indexDataPool;
	long countPT, startT;
	String fileName;
	int[] dataPool;
	int lengthData;
	public int maxElement;
	private Date startTime;
	private Date endTime;

	public HistoryDataBuffer(Patient _patient, String _connectStr) {
		if (_patient == null || _patient.getChannelNum() <= 0) {
			DebugTool.printLogDebug("null patient or zero channel.");
			return;
		}
		fileName = "./data/dataInput.txt";
		lengthData = 2805;
		isLoading = false;
		dataPool = new int[lengthData * 8];
		patient = _patient;
		maxElement = frequency * secondBuffer;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		exceptionFlag = false;
		indexDataPool = 0;
		startTime = null;
		endTime = null;
		connectStr = _connectStr;// "http://localhost:8787";
		leads = null;
		// create and initialize mutli-channel buffer
		bufferList = new ArrayList<int[]>();
		int maxV = 8000, minV = 3000;
		for (int indexC = 0; indexC < patient.getChannelNum(); ++indexC) {
			int[] tempIntArray = new int[maxElement];
			for (int j = 0; j < secondBuffer; ++j) {
				int startP = j * frequency;
				for (int i = startP; i < (startP + frequency); ++i) {
					tempIntArray[i] = -1;
				}
			}
			bufferList.add(tempIntArray);
		}
		reading = true;
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
		startT = System.currentTimeMillis();
		readDataFromFile();
		fillData();
	}

	private void eraseData() {
		for (int j = 0; j < bufferList.size(); ++j) {
			for (int i = 0; i < bufferList.get(j).length; ++i)
				bufferList.get(j)[i] = -1;
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
		readDataFromServer();
		// replaced by the code to read data from server
	}

	private class SwingWorkerReading extends SwingWorker<Void, Void> {

		public SwingWorkerReading() {
		}

		@Override
		protected Void doInBackground() throws Exception {
			isLoading = true;
			DataClient dataClient = DataClientFactory.getLoginDataClient();
			if (dataClient == null || patient == null) {
				DebugTool.printLogDebug("dataClient or patient is null");
				return null;
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
			EcgRequest request = dataClient.ecg()
					.withPatientId(patient.getPatientID()).addLeads(leads)
					.withStartTimestamp(startTime).withEndTimestamp(endTime)
					.build();
			EcgResponse response = null;
			int indexBuffer = 0;
			indexLead = 0;
			int counter = 0;
			boolean noData = false;
			try {
				// Execute the request, which returns the first response
				response = request.get();
				do {
					// The list of leads returned should be the same as
					// requested
					System.out.printf("Leads=%s\n", response.getLeads());
					System.out.printf("StartTimestamp=%s, EndTimestamp=%s\n",
							dateFormat.format(response.getStartTimestamp()),
							dateFormat.format(response.getEndTimestamp()));
					// Get an input stream for the ECG data, which should be
					// closed
					// after use
					int leadValue;
					try (DataInputStream dataIn = response.getDataInputStream()) {
						// Each data point has values for the selected leads
						while (true) {
							for (; indexBuffer < bufferList.get(0).length; ++indexBuffer) {
								for (; indexLead < bufferList.size(); ++indexLead) {
									leadValue = dataIn.readShort() & 0xFFFF;
									bufferList.get(indexLead)[indexBuffer] = leadValue;
									// System.out.printf("%d ", leadValue);
								}
								indexLead = 0;
								counter++;
							}
							leadValue = dataIn.readShort() & 0xFFFF;
							// DebugTool.printLogDebug();
						}
					} catch (EOFException e) {

						// When the input stream is exhausted, close the current
						// response, and ask MLNX Server for the next response
						response.close();
						response = response.next();
					}
					DebugTool.printLogDebug("counter: " + counter);
				} while (response != null);
			} catch (NoDataForPatientException nodataException) {
				noData = true;
				JOptionPane.showMessageDialog(null, "没有找到相关数据");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (response != null) {
					response.close();
				}
				DataClientFactory.addDataClient(dataClient);
				DebugTool.printLogDebug("in sumL " + counter);
				isLoading = false;
			}

			if (indexBuffer < (bufferList.get(0).length) && !noData) {
				DebugTool.printLogDebug(" " + indexBuffer + " "
						+ (bufferList.get(0).length) + " " + indexLead + " "
						+ bufferList.size());
				JOptionPane.showMessageDialog(null, "部分数据缺失");
			}
			isLoading = false;
			return null;
		}

	}

	private void readDataFromServer() {
		DebugTool.printLogDebug("reading from server!");
		SwingWorkerReading sws = new SwingWorkerReading();
		sws.execute();
	}

	private void readDataFromFile() {
		int indexLine = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			while (line != null) {
				dataPool[indexLine] = Integer.parseInt(line);
				// DebugTool.printLogDebug(indexLine + " : " + dataPool[indexLine]);
				indexLine++;
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}

	private void fillData() {
		int loadCount = maxElement;
		while (loadCount > 0) {
			--loadCount;
			// DebugTool.printLogDebug(maxElement);
			int indexTemp = currentIndex % maxElement;
			for (int Channel = 0; Channel < 8; ++Channel) {
				if (bufferList.size() > Channel)
					bufferList.get(Channel)[indexTemp] = dataPool[indexDataPool];
				++indexDataPool;
				indexDataPool = indexDataPool % (dataPool.length);
			}
			++currentIndex;
			currentIndex = currentIndex % maxElement;
		}
	}

	public boolean checkReadingStatus() {
		return reading;
	}

	public void end() {
		reading = false;
	}

	public void suspend() {

	}

	public void resume() {

	}

	public int checkStatus() {
		return 0;
	}

	public int getCurrentSecond() {
		int outputSecond = currentIndex / frequency;
		outputSecond = outputSecond % secondBuffer;
		return outputSecond;
	}

	public int getLocalMeanChannel(int start, int width, int Channel) {
		if (start < 0)
			start += maxElement;
		start = start % (maxElement);
		int meanV = 0;
		for (int i = start; i < (start + width); ++i) {
			meanV += bufferList.get(Channel)[i % maxElement];
		}
		return meanV / width;
	}

	public boolean isExceptionFlag() {
		return exceptionFlag;
	}

	public void setExceptionFlag(boolean _exceptionFlag) {
		exceptionFlag = _exceptionFlag;
	}

	public void setNChannel(int nChannel) {

	}

	public int getNChannel() {
		return patient.getChannelNum();
	}

	public String getConnectStr() {
		return connectStr;
	}

	public void setConnectStr(String _connectStr) {
		connectStr = _connectStr;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient p) {
		patient = p;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int _frequency) {
		frequency = _frequency;
	}

	public int getTimeWindow() {
		return secondBuffer;
	}

	public void setTimeWindow(int _timesecondBuffer) {
		secondBuffer = _timesecondBuffer;
	}

	public void getOffset(int offsetTime) {
		DebugTool.printLogDebug("call: get offset, offset" + offsetTime);
		DebugTool.printLogDebug("secondBuffer: " + secondBuffer);
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		String startTimeStr = df.format(startTime);
		String endTimeStr = df.format(endTime);
		DebugTool.printLogDebug("before: " + startTimeStr + " to " + endTimeStr);
		startTime = new Date(startTime.getTime() + offsetTime * (1000));
		endTime = new Date(startTime.getTime() + secondBuffer * 1000 + 2000);// 2s
																				// to
																				// make
																				// sure
																				// enough
																				// data
		startTimeStr = df.format(startTime);
		endTimeStr = df.format(endTime);
		DebugTool.printLogDebug("before: " + startTimeStr + " to " + endTimeStr);
		readDataFromServer();
	}

	public void getNext() {
		DebugTool.printLogDebug("start time:" + startTime.getDay() + " "
				+ startTime.getHours());
		startTime = new Date(startTime.getTime() + secondBuffer * (10000));
		endTime = new Date(startTime.getTime() + secondBuffer * (1000l));
		this.fillData();
	}

	public void getPre() {
		DebugTool.printLogDebug("start time:" + startTime.getDay() + " "
				+ startTime.getHours());
		startTime = new Date(startTime.getTime() - secondBuffer * (10000));
		endTime = new Date(startTime.getTime() + secondBuffer * (10001));
		this.fillData();
		this.fillData();
	}

	// private class for reading (single thread)
	private class ReadThread extends Thread {
		public void run() {
			while (true) {
				long endT = System.currentTimeMillis();

				int loadCount = (int) ((endT - startT) * getFrequency() / 1000);
				// DebugTool.printLogDebug("startT:"+startT+"endT:"+endT+" update : "+loadCount);
				startT = endT;
				int indexTemp = 0;
				while (loadCount > 0) {
					--loadCount;
					// DebugTool.printLogDebug(maxElement);
					indexTemp = currentIndex % maxElement;
					for (int Channel = 0; Channel < 8; ++Channel) {
						if (bufferList.size() > Channel)
							bufferList.get(Channel)[indexTemp] = dataPool[indexDataPool];
						++indexDataPool;
						indexDataPool = indexDataPool % (dataPool.length);
					}
					++currentIndex;
					currentIndex = currentIndex % maxElement;

				}

				try {
					// .sleep(500);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getCurrentPT() {
		return currentIndex;
	}

	public int getSecondBuffer() {
		return secondBuffer;
	}

	public void setSecondBuffer(int _secondBuffer) {
		secondBuffer = _secondBuffer;
	}

	public int getBPM() {
		return 72;
	}

	public void setBPM(int _BPM) {

	}

	public int getMotionCode() {
		return 71;
	}

	public void setMotionCode(int _MotionCode) {

	}

	public int getWiFiSignalStrength() {

		return 0;
	}

	public int getElectrodeHeader() {
		return 0;
	}

	public int getBattery() {
		return 0;
	}

	public Date getCallTime() {
		return null;
	}

	public boolean checkAlarm() {
		return false;
	}

	public void syncCallTime() {

	}

	public int getFidelity() {
		return 0;
	}

	public int getAmountPoint(int indexBuffer) {
		return 0;
	}

	public Mode getDeviceMode() {
		return null;
	}

	public void setDeviceMode(Mode deviceMode) {

	}

	public DataType getDeviceDataType() {
		return null;
	}

	public void setDeviceDataType(DataType deviceDataType) {

	}
}
