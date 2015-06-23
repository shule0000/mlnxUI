package com.medlinx.core.alarm.thread;

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

import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.alarm.Alarm;
import com.medlinx.core.alarm.Alert;
import com.medlinx.core.alarm.AlertManager;

public class AlarmTimerSound extends Timer {

	private AlarmTask alarmTask;
	private boolean play = false;
	private Alarm alarm;
	private Alert alert;

	public AlarmTimerSound(Alarm alarm) {
		this.alarm = alarm;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	class AlarmTask extends TimerTask {
		public void run() {
			// alarm sound
			// if(DrawingPanel.soundOn){
			// playSound(SystemResources.AlarmSoundFile[alarm.getAlarmLevel()]);
			playSound(SystemResources.AlarmSoundFile[alert.getLevel()]);
			// }
		}

		/**
		 * @param filename
		 *            the name of the file that is going to be played
		 */
		public void playSound(String filename) {

			String strFilename = filename;
			File soundFile = null;
			try {
				soundFile = new File(strFilename);
			} catch (Exception e) {
				e.printStackTrace();
			}

			AudioInputStream audioStream = null;
			try {
				audioStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (Exception e) {
				e.printStackTrace();
			}

			AudioFormat audioFormat = audioStream.getFormat();

			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFormat);
			SourceDataLine sourceLine = null;
			try {
				sourceLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceLine.open(audioFormat);
			} catch (IllegalArgumentException e) {
//				DebugTool.printLogDebug("没有设备支持播放音频");
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (sourceLine == null)
				return;
			sourceLine.start();

			int nBytesRead = 0;
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

	public void start(int period) {
		if (play)
			return;
		play = true;
		alarmTask = new AlarmTask();
		this.schedule(alarmTask, 0, period);
	}

	public void stop() {
		play = false;
		if (alarmTask != null)
			alarmTask.cancel();
		alarmTask = null;
	}

}
