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

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.DataClientBuilder;
import com.mlnx.pms.client.data.HeartRateRequest;
import com.mlnx.pms.client.data.HeartRateResponse;
import com.mlnx.pms.client.data.NoDataForPatientException;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;
import com.mlnx.pms.core.HttpConstants;




public class HeartRateDataBuffer implements DataBufferInterface{

	private static final DateFormat dateFormat = new SimpleDateFormat(
			HttpConstants.TIMESTAMP_FORMAT);
	private DataClient dataClient;
	private int[] buffer;
	private boolean[] dataFlag;
	private String connectStr;
	private boolean reading,exceptionFlag;
	private com.mlnx.pms.core.Patient patient;
	private float skipMin;
	private int currentIndex,indexDataPool;
	long countPT,startT;
	String fileName;
	int[] dataPool;
	int lengthData;
	public int maxElement;
	private boolean isLoading;
	private Date startTime;
	private Date endTime;

	public int[] getBuffer() {
		return buffer;
	}
	public void setBuffer(int[] buffer) {
		this.buffer = buffer;
	}
	public int getMaxElement() {
		return maxElement;
	}
	public void setMaxElement(int _maxElement) {
		this.maxElement = _maxElement;
		// create and initialize mutli-channel buffer
		buffer = new int[maxElement];
		for (int i = 0; i < maxElement; ++i) {
			buffer[i] = SystemConstant.BPM_INVALIDVALUE;
		}
		dataFlag = new boolean[maxElement];
		for (int i = 0; i < maxElement; ++i) {
			dataFlag[i] = true;;
		}
	}
	public float getSkipMin() {
		return skipMin;
	}
	public void setSkipMin(int skipMin) {
		this.skipMin = skipMin;
	}

	public HeartRateDataBuffer(com.mlnx.pms.core.Patient _patient, String _connectStr) {
		patient = _patient;
		fileName = "./data/dataInput.txt";
		lengthData = 2805;
		dataPool = new int[lengthData*8];
		maxElement = 50;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		skipMin = 3;
		exceptionFlag = false;
		indexDataPool = 0;
		startTime = null;
		endTime = null;
		isLoading = false;
		connectStr = _connectStr;//"http://localhost:8787";
		// create and initialize mutli-channel buffer
		buffer = new int[maxElement];
		for (int i = 0; i < maxElement; ++i) {
			buffer[i] = SystemConstant.BPM_INVALIDVALUE;
		}
		dataFlag = new boolean[maxElement];
		for (int i = 0; i < maxElement; ++i) {
			dataFlag[i] = true;;
		}
		reading = true;
		constructDataClient();
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
	private void constructDataClient()
	{
		dataClient = DataClientBuilder.newBuilder()
				.withServerHostname(connectStr).build();

	}
	public void start() {
		startT = System.currentTimeMillis();
		readDataFromFile();
		fillData();
	}

	public void loadData(Date _startDate,float _skipMin)
	{
		//dateStr = "2013-07-11 00:00:00.000";
		startTime = _startDate;
		skipMin = _skipMin;
		endTime = new Date(startTime.getTime()+maxElement*((int)(skipMin*(60*1000)))
				+60000);
		readDataFromServer();
	}
	public void loadData(Date _startDate,Date _endDate, float _skipMin)
	{
		startTime = _startDate;
		maxElement = (int) ((_endDate.getTime()-_startDate.getTime())/(_skipMin*60*1000));
		skipMin = _skipMin;
		setMaxElement(maxElement);
		endTime = new Date(startTime.getTime()+maxElement*((int)(skipMin*(60*1000)))
				+60000);
		readDataFromServer();
	}
	public void loadData(Date _startDate,Date _endDate, float _skipMin,ArrayList<Date> startTimes, ArrayList<Date> endTimes)
	{
		startTime = _startDate;
		maxElement = (int) ((_endDate.getTime()-_startDate.getTime())/(_skipMin*60*1000));
		skipMin = _skipMin;
		setMaxElement(maxElement);
		endTime = new Date(startTime.getTime()+maxElement*((int)(skipMin*(60*1000)))
				+60000);
		
		for (int i = 0; i < maxElement; ++i) {
			dataFlag[i] = false;;
		}
		for (int i = 0;i<startTimes.size();++i)
		{
			int startIndex = time2Index(startTimes.get(i));
			int endIndex = time2Index(endTimes.get(i));
			if(endIndex>=maxElement)
				endIndex = maxElement-1;
			if(startIndex<0)
				startIndex = 0;
			if(startIndex>endIndex)
				continue;
			for(int j = startIndex;j<=endIndex;++j)
				dataFlag[j] = true;
		}
		readDataFromServer();
	}
	private void eraseData()
	{
		for (int j = 0; j<buffer.length; ++j)
		{
			buffer[j] = SystemConstant.BPM_INVALIDVALUE;
		}

	}
	private class SwingWorkerReading extends SwingWorker<Void, Void>{

		public SwingWorkerReading(){
		}
		@Override
		protected Void doInBackground() throws Exception {
			isLoading = true;
			eraseData();
			dataClient = SystemConstant.constructDataClient();
			if (dataClient==null || patient==null)
			{
				DebugTool.printLogDebug("dataClient or patient is null");
				return null;
			}
			// Construct an instance of EcgRequest for retrieving historic ECG data
			// for the selected patient on the selected leads during the selected
			// period of time
			System.out.printf("Heart rate data for the following patient"
					+ " between %s and %s, interval = %f" + " will be retrieved:\n",
					dateFormat.format(startTime),
					dateFormat.format(endTime), skipMin);
			System.out.printf("id=%d, name=%s, gender=%s\n", patient.getId(),
					patient.getName(), patient.getGender());
			int interval = (int)(60*skipMin);
			int indexWrite2Buffer = 0,counter = 0;
			boolean noData = false;
			HeartRateRequest request = dataClient.heartRates()
					.withPatientId(patient.getId())
					.withStartTimestamp(startTime)
					.withEndTimestamp(endTime).withInterval(interval).build();
			HeartRateResponse response = null;
			long a=System.currentTimeMillis();
			boolean isFirstResponse = true;
			try {
				// Execute the request, which returns the first response
				response = request.get();
				do {
					System.out.printf("StartTimestamp=%s, EndTimestamp=%s\n",
							dateFormat.format(response.getStartTimestamp()),
							dateFormat.format(response.getEndTimestamp()));
					if(isFirstResponse)
					{
						startTime = response.getStartTimestamp();
						indexWrite2Buffer = time2Index(response.getStartTimestamp());
						isFirstResponse = false;
					}
					else
					{
						indexWrite2Buffer = time2Index(response.getStartTimestamp());
						DebugTool.printLogDebug("indexWrite2Buffer:"+indexWrite2Buffer
								+" out of maxElement:"+maxElement);
					}
					if(indexWrite2Buffer<0)
						indexWrite2Buffer = 0;
					// The interval returned should be the same as requested
					System.out.printf("Interval=%d\n", response.getInterval());
					// Get an input stream for the heart rate data, which should be
					// closed after use
					try (DataInputStream dataIn = response.getDataInputStream()) {
						int dataPointNumber = 1;
						while (true) {
							System.out.printf("%3d: ", dataPointNumber++);
							// For each data point, read its value (an 8-bit
							// integer)
							int heartRateValue = dataIn.readByte()& 0xFF;
							if (indexWrite2Buffer<buffer.length)
							{
								if (heartRateValue == SystemConstant.EXCEPTIONAL_HEARTRATE)
								{
									// if bpm == 255, it means some exception occurs
									buffer[indexWrite2Buffer] = SystemConstant.BPM_INVALIDVALUE;
								}
								else
								{
									buffer[indexWrite2Buffer] = heartRateValue;
									counter++;
								}
							}
							else
							{
								break;
							}
							++indexWrite2Buffer;
							System.out.printf("%d\n", heartRateValue);
						}
					} catch (EOFException e) {
						// When the input stream is exhausted, close the current
						// response, and ask MLNX Server for the next response
						response.close();
						response = response.next();
					}
				} while (response != null);
			}catch(NoDataForPatientException nodataException) 
			{
				noData = true;
				JOptionPane.showMessageDialog(null,"没有找到相关数据");
			}
			catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (response != null) {
					response.close();
				}
				dataClient.close();
				isLoading = false;
			}
			isLoading = false;
			DebugTool.printLogDebug("执行耗时 : "+(System.currentTimeMillis()-a)+" ms, count:" +
					+counter+" out of "+maxElement);
			if(counter<(buffer.length-1) &&!noData)
				JOptionPane.showMessageDialog(null,"部分数据缺失");
			return null;
		}

	}
	public int time2Index(Date startTimestamp)
	{
		long interval = ((long)(60*skipMin*1000));
		long timeDiff = startTimestamp.getTime()-startTime.getTime();
		int ptDiff = (int)((float)timeDiff/(float)interval+0.5);
		return ptDiff;
	}
	private void readDataFromServer()
	{
		DebugTool.printLogDebug("reading from server!");
		SwingWorkerReading sws = new SwingWorkerReading();
		sws.execute();
	}
	private void readDataFromFile()
	{
		int indexLine = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			while (line != null) {
				dataPool[indexLine] = Integer.parseInt(line);
				//DebugTool.printLogDebug(indexLine + " : " + dataPool[indexLine]);
				indexLine++;
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}

	private void fillData()
	{
		//DebugTool.printLogDebug(maxElement);
		//int indexTemp = currentIndex % maxElement;
		for (int i = 0;i<buffer.length;++i)
		{
			buffer[i]=dataPool[indexDataPool];
			++indexDataPool;
			indexDataPool = indexDataPool%(dataPool.length);
		}
	}
	public boolean checkReadingStatus()
	{
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
	public int getHeartRate(int index)
	{
		return buffer[index%maxElement];
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
		return 1;
	}

	public String getConnectStr() {
		return connectStr;
	}

	public void setConnectStr(String _connectStr) {
		connectStr = _connectStr;
	}

	public Patient getPatient() {
		Patient p = new Patient();
		p.setPatientID(patient.getId());
		return p;
	}

	public void setPatient(Patient p) {
	}

	public com.mlnx.pms.core.Patient getPatientInfo()
	{
		return patient;
	}

	public void setPatientInfo(com.mlnx.pms.core.Patient p)
	{
		patient = p;
	}
	public void getOffset(int offset)
	{
		startTime = new Date(startTime.getTime()+offset*(60*1000));
		endTime = new Date(startTime.getTime()+maxElement*((int)(skipMin*60*(1000)))
				+60000);//extra 1min to make sure enough data
		readDataFromServer();
	}

	public int getCurrentPT() {
		return currentIndex;
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
	public int getCurrentSecond() {
		return 0;
	}
	public int getLocalMeanChannel(int start, int width, int Channel) {
		return 0;
	}
	public int getFrequency() {
		return 0;
	}
	public void setFrequency(int _frequency) {

	}
	public int getTimeWindow() {
		return 0;
	}
	public void setTimeWindow(int _timesecondBuffer) {

	}
	public int getSecondBuffer() {
		return 0;
	}
	public void setSecondBuffer(int _secondBuffer) {
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
		return 1000000;
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
