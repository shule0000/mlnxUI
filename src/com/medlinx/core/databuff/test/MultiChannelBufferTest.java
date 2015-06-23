package com.medlinx.core.databuff.test;


import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ui.medlinx.com.debug.DebugTool;

import com.medlinx.core.databuff.DataBufferInterface;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;





public class MultiChannelBufferTest implements DataBufferInterface{

	public int frequency = 300;
	public int secondBuffer = 10;
	private ArrayList<int[]> bufferList;
	private String connectStr;
	ReadThread readthread;
	private boolean reading,exceptionFlag;
	private Patient patient;
	private int currentChannel;
	private int currentIndex;
	long countPT,startT;
	
	public MultiChannelBufferTest(Patient _patient) {
		// TODO Auto-generated constructor stub
		if (_patient==null||_patient.getChannelNum()<=0)
		{
			DebugTool.printLogDebug("null patient or zero channel.");
			return;
		}
		patient = _patient;
		int MaxElements = frequency * secondBuffer;
		patient = null;
		currentChannel = 0;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		exceptionFlag = false;
		connectStr = "http://localhost:8787";
		// create and initialize mutli-channel buffer
		bufferList = new ArrayList<int[]>();
		int maxV = 8000,minV = 3000;
		for (int indexC=0;indexC<patient.getChannelNum();++indexC)
		{
			int[] tempIntArray = new int[MaxElements];
			for (int j = 0; j < secondBuffer; ++j) {
				int startP = j * frequency;
				for (int i = startP; i < (startP + frequency); ++i) {
					tempIntArray[i] = (int)(Math.sin(  (double)(i-startP)/(double)frequency*(2.0*Math.PI))
                           *(maxV - minV)/2) + minV;
				}
			}
			bufferList.add(tempIntArray);
		}
		reading = true;
	}
	
	
	
	@Override
	public void start() {
		// TODO Auto-generated method stub

		reading = true;
		//readthread = new ReadThread();
		//readthread.start();
		startT = System.currentTimeMillis();

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		reading = false;
	}

	@Override
	public void suspend() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int checkStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentSecond() {
		// TODO Auto-generated method stub
		int outputSecond = currentIndex/frequency;
		outputSecond = outputSecond%secondBuffer;
		return outputSecond;
	}

	@Override
	public int getLocalMeanChannel(int start, int width, int Channel) {
		// TODO Auto-generated method stub
		int maxElement = frequency * secondBuffer;
		if (start < 0)
			start += maxElement;
		start = start % (maxElement);
		int meanV = 0;
		for (int i = start; i < (start + width); ++i) {
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
		frequency = _frequency;
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
	

	
	//private class for reading (single thread)
	private class ReadThread extends Thread{
		int countDown = 2000;
		int waitingTime = 2000;
		private boolean checkResponse(HttpResponse response) throws ClientProtocolException
		{
			if (response.getStatusLine().getStatusCode() != 200) {
				DebugTool.printLogDebug("404!"+response.getStatusLine().getStatusCode());
				return false;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new ClientProtocolException(
						"No content found in the response");
			}
			return true;
		}
		public void run()
		{
			HttpClient httpClient=null;
			while(countDown>0&&reading){
				try {
					httpClient= new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(connectStr);
					//httpClient.execute(httpGet, new BinaryDataResponseHandler());
					HttpResponse response = httpClient.execute(httpGet);
					if(checkResponse(response))
					{
						countDown=2000;
						exceptionFlag = false;
						handleResponse(response);
					}
					else
					{
						exceptionFlag = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					httpClient.getConnectionManager().shutdown();
				}

				try {
					Thread.sleep(waitingTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				--countDown;
			}
			DebugTool.printLogDebug("Over~");
		}

		private void handleResponse(HttpResponse response)
		{
			HttpEntity entity = response.getEntity();

			try {
				DataInputStream dataIn = new DataInputStream(entity.getContent());
				while (reading){
					int value = dataIn.readShort() & 0xFFFF;
					bufferList.get(currentChannel)[currentIndex] = value;
					currentChannel++;
					if(currentChannel == patient.getChannelNum())
					{
						currentChannel = 0;
						currentIndex++;
						currentIndex = currentIndex%(frequency * secondBuffer);
					}
					countPT++;
					if (countPT%900==0 && patient.getPatientID()==123) // 
					{
						//int pps = (int) ((countPT*1000.0f)/(System.currentTimeMillis()-startT));
						//DebugTool.printLogDebug(patient.getPatientID()+"pt per second: "+pps+ " ; " + value);
					}

				}
				dataIn.close();
			} catch (EOFException e) {
				DebugTool.printLogDebug("No more data to read");
				//countDown = 10;
			}
			catch (IOException e)
			{
				DebugTool.printLogDebug("IOException!");
			}

		}
	}



	@Override
	public int getCurrentPT() {
		// TODO Auto-generated method stub
		return 0;
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
		return 70;
	}



	@Override
	public void setBPM(int _BPM) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getMotionCode() {
		// TODO Auto-generated method stub
		return 71;
	}



	@Override
	public void setMotionCode(int _MotionCode) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getWiFiSignalStrength() {
		
		return 0;
	}



	@Override
	public int getElectrodeHeader() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getBattery() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public Date getCallTime() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean checkAlarm() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void syncCallTime() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getFidelity() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getAmountPoint(int indexBuffer) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public Mode getDeviceMode() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setDeviceMode(Mode deviceMode) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public DataType getDeviceDataType() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setDeviceDataType(DataType deviceDataType) {
		// TODO Auto-generated method stub
		
	}
}
