package com.medlinx.core.databuff.test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import ui.medlinx.com.debug.DebugTool;

import com.medlinx.core.databuff.DataBufferInterface;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;




public class MultiChannelBufferTestRealData implements DataBufferInterface{

	public int frequency = 300;
	public int secondBuffer = 20;
	private ArrayList<int[]> bufferList;
	private String connectStr;
	ReadThread readthread;
	private boolean reading,exceptionFlag;
	private Patient patient;
	private int currentIndex,indexDataPool;
	long countPT,startT;
	String fileName;
	int[] dataPool;
	int lengthData;
	public int maxElement;
	
	public MultiChannelBufferTestRealData(Patient _patient) {
		// TODO Auto-generated constructor stub
		if (_patient==null||_patient.getChannelNum()<=0)
		{
			DebugTool.printLogDebug("null patient or zero channel.");
			return;
		}
		fileName = "./data/dataInput.txt";
		lengthData = 2805;
		dataPool = new int[lengthData*8];
		patient = _patient;
		maxElement = frequency * secondBuffer;
		currentIndex = 0;
		countPT = 0;
		startT = 0;
		exceptionFlag = false;
		indexDataPool = 0;
		connectStr = "http://localhost:8787";
		// create and initialize mutli-channel buffer
		bufferList = new ArrayList<int[]>();
		int maxV = 8000,minV = 3000;
		for (int indexC=0;indexC<patient.getChannelNum();++indexC)
		{
			int[] tempIntArray = new int[maxElement];
			for (int j = 0; j < secondBuffer; ++j) {
				int startP = j * frequency;
				for (int i = startP; i < (startP + frequency); ++i) {
					tempIntArray[i] = (int)(Math.sin(  (double)(i-startP)/(double)frequency*(2.0*Math.PI))
                           *(maxV - minV)/2) + 32768;//(minV+maxV)/2;
					//DebugTool.printLogDebug("tempIntArray[i] "+tempIntArray[i]);
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
		
		startT = System.currentTimeMillis();
		readDataFromFile();
		
		readthread = new ReadThread();
		readthread.start();

	}
	
	private void readDataFromFile()
	{
		/*
		File file = new File(fileName);
		FileInputStream fin;
		try {
			fin = new FileInputStream(file);
			BufferedInputStream bin = new BufferedInputStream(fin);
		DataInputStream din = new DataInputStream(bin);

		int count = (int) (file.length() / 4);
		if (dataPool.length!=count)
		{
			DebugTool.printLogDebug("error of length of file!"+count);
		}
		else
		{
			for (int i = 0; i < count; i++) {
			    dataPool[i] = din.readInt();
			    DebugTool.printLogDebug(i+" : "+dataPool[i]);
			}
		}
		din.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        
	    }
		
	}
	
	public boolean checkReadingStatus()
	{
		return reading;
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
		public void run()
		{
			while (true)
			{
			long endT = System.currentTimeMillis();
			
			int loadCount = (int) ((endT-startT)*getFrequency()/1000);
			//DebugTool.printLogDebug("startT:"+startT+"endT:"+endT+" update : "+loadCount);
			startT = endT;
			int indexTemp = 0;
			while(loadCount>0)
			{
				--loadCount;
				//DebugTool.printLogDebug(maxElement);
				indexTemp = currentIndex % maxElement;
				for (int Channel = 0;Channel<8;++Channel)
				{
					if(bufferList.size()>Channel)
						bufferList.get(Channel)[indexTemp]=dataPool[indexDataPool];
					++indexDataPool;
					indexDataPool = indexDataPool%(dataPool.length);
				}
				++currentIndex;
				currentIndex = currentIndex%maxElement;
				
			}
			
			try {
				//.sleep(500);
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
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
		return 73;
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
		return new Date();
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
		return 100000;
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
