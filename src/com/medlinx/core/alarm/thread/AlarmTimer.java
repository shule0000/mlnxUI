package com.medlinx.core.alarm.thread;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JLabel;

import ui.medlinx.com.debug.DebugTool;

public class AlarmTimer extends Timer
{
	private int period;
	private JLabel labelWarningMessage;
	private AlarmTask alarmTask;
	private String alarmStr;
	private long startTime;
	private int duration;
	/**
	 * main constructor of AlarmTimer
	 * @param period: the period of the schedule of AlarmTimer
	 * @param duration: the duration of this alarm in seconds. 0 means running forever
	 * @param label: the label to show alarm string
	 * @param alarmStr： alarm string
	 */
	public AlarmTimer(int period, int duration, JLabel label, String alarmStr) {
		this.period = period;                                    //报警周期1000
		labelWarningMessage = label;                             //show报警内容
		alarmTask = new AlarmTask(labelWarningMessage);          //实例化
		this.alarmStr = alarmStr;                                //警报
		startTime = System.currentTimeMillis();                  //获得当前时间
		this.duration = duration;                                //报警持续时间，0意味着一直报警
	}

	public class AlarmTask extends TimerTask
	{
		private Color alarmColor = Color.red;
		private JLabel labelWarningMessage;
		public AlarmTask(JLabel label){
			this.labelWarningMessage = label;
		}

		@Override
		public void run() {
			DebugTool.printLogDebug("alarm!!!!!!!!!!!!!!!!!");
			if(duration > 0 && (System.currentTimeMillis() - startTime) > duration*1000)
			{
				DebugTool.printLogDebug("alarm timeout.");
				stop();
			}
			// Light change
			labelWarningMessage.setText(alarmStr);
			labelWarningMessage.setForeground(alarmColor);
			if(alarmColor == Color.red)
				alarmColor = Color.green;
			else
				alarmColor = Color.red;
				playSound("res/beep-1.wav");
		}
		
		/**
	     * @param filename the name of the file that is going to be played
	     */
	    public void playSound(String filename){

	        String strFilename = filename;
	        File soundFile = null;
	        try {
	            soundFile = new File(strFilename);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        AudioInputStream audioStream = null;
	        try {
	            audioStream = AudioSystem.getAudioInputStream(soundFile);
	        } catch (Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }

	        AudioFormat audioFormat = audioStream.getFormat();

	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	        SourceDataLine sourceLine = null;
	        try {
	            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	            sourceLine.open(audioFormat);
	        } catch (LineUnavailableException e) {
	            e.printStackTrace();
	            System.exit(1);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        sourceLine.start();

	        int nBytesRead = 0;
	        //这是缓冲
	        byte[] abData = new byte[1024];
	        while (nBytesRead != -1) {
	            try {
	                nBytesRead = audioStream.read(abData, 0, abData.length);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            if (nBytesRead >= 0) {
	                @SuppressWarnings("unused")
	                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
	            }
	        }

	        sourceLine.drain();
	        sourceLine.close();
	    }
	}

	public void start() {
		this.schedule(alarmTask, 0, period);
	}

	public void stop() {
		this.labelWarningMessage.setText("");
		this.cancel();
	}	
}
