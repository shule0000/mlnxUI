package ui.medlinx.com.doctor_tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import ui.medlinx.com.debug.DebugTool;

public class RecordPlayer {
	private ByteArrayOutputStream byteArrayOutputStream;// 录音数据对象
	private int totaldatasize = 0; // 录音数据大小
	private TargetDataLine targetDataLine; // 输入设备
	private AudioInputStream audioInputStream; // 播放数据对象
	private SourceDataLine sourceDataLine; // 输出设备
	private boolean stopCapture = false; // 控制录音标志
	private boolean pausePlay = false;
	private boolean stopPlay = false;

	private String controlThread = "";

	DoctorCommentDialog doctorCommentDialog;

	public RecordPlayer(DoctorCommentDialog doctorCommentDialog) {
		this.doctorCommentDialog = doctorCommentDialog;
	}

	public void StartRecord() {
		if (byteArrayOutputStream != null)
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		byteArrayOutputStream = new ByteArrayOutputStream();
		CaptureSound();
	}

	public void ResumRecord() {
		CaptureSound();
	}

	public void PauseRecord() {
		StopRecord();
	}

	public void StopRecord() {
		stopRecord();
	}

	public void StartPlay() {
		play();
	}

	public void PausePlay() {
		synchronized (this) {
			setPausePlay(true);
		}
	}

	public void ResumPlay() {
		synchronized (this) {
			setPausePlay(false);
		}
		synchronized (controlThread) {
			controlThread.notify();
		}
	}

	public void StopPlay() {
		synchronized (RecordPlayer.this) {
			setStopPlay(true);
		}
		synchronized (this) {
			setPausePlay(false);
		}
		synchronized (controlThread) {
			controlThread.notify();
		}
	}

	private synchronized boolean isStopCapture() {
		return stopCapture;
	}

	private synchronized void setStopCapture(boolean stopCapture) {
		this.stopCapture = stopCapture;
	}

	private synchronized boolean isStopPlay() {
		return stopPlay;
	}

	private synchronized void setStopPlay(boolean stopPlay) {
		this.stopPlay = stopPlay;
	}

	private synchronized boolean isPausePlay() {
		return pausePlay;
	}

	private synchronized void setPausePlay(boolean pausePlay) {
		this.pausePlay = pausePlay;
	}

	public void ReadSoundFile(String fileName) throws IOException {
		File f = new File(fileName);
		InputStream input = null;
		input = new FileInputStream(f);
		byte[] b = new byte[1024];
		int cnt = 0;
		byteArrayOutputStream = new ByteArrayOutputStream();
		while ((cnt = input.read(b)) > 0) {
			byteArrayOutputStream.write(b, 0, cnt);
		}

	}

	// 录音事件，保存到ByteArrayOutputStream中
	private void CaptureSound() {
		try {
			// 打开录音
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			// 创建独立线程进行录音
			Thread captureThread = new Thread(new CaptureThread());
			captureThread.start();
		} catch (Exception e) {
			e.printStackTrace();

			JOptionPane.showMessageDialog(doctorCommentDialog,
					"录音出错,请检查是否有支持该功能的设备");
			doctorCommentDialog.closeDialog();
		}
	}

	// 播放ByteArrayOutputStream中的数据
	private void play() {
		try {
			// 取得录音数据
			byte audioData[] = byteArrayOutputStream.toByteArray();
			// 转换成输入流
			InputStream byteArrayInputStream = new ByteArrayInputStream(
					audioData);
			AudioFormat audioFormat = getAudioFormat();
			audioInputStream = new AudioInputStream(byteArrayInputStream,
					audioFormat, audioData.length / audioFormat.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			// 创建独立线程进行播放
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// 停止录音
	private void stopRecord() {
		synchronized (this) {
			setStopCapture(true);
		}
	}

	// 保存文件
	public void SaveRecordSound(String folder) {
		// 取得录音输入流
		AudioFormat audioFormat = getAudioFormat();
		byte audioData[] = byteArrayOutputStream.toByteArray();
		InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
		audioInputStream = new AudioInputStream(byteArrayInputStream,
				audioFormat, audioData.length / audioFormat.getFrameSize());
		// 写入文件
		try {
			File file = new File(folder);
			AudioSystem
					.write(audioInputStream, AudioFileFormat.Type.AIFC, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 取得AudioFormat
	private AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 2;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	class CaptureThread extends Thread {
		// 临时数组
		byte tempBuffer[] = new byte[1000];

		public void run() {
			totaldatasize = 0;
			int level = 0;
			setStopCapture(false);
			try {// 循环执行，直到按下停止录音按钮
				while (!isStopCapture()) {
					// 读取10000个数据
					int cnt = targetDataLine.read(tempBuffer, 0,
							tempBuffer.length);
					if (cnt > 0) {
						// 保存该数据
						byteArrayOutputStream.write(tempBuffer, 0, cnt);
						totaldatasize += cnt;
					}
					level = 0;
					for (int i = 0; i < cnt; ++i) {
						level += tempBuffer[i] * tempBuffer[i];
					}
					level /= cnt;
					level = (int) Math.sqrt(level);
					level = (level - 45) < 0 ? 0 : (level - 45);
					DebugTool.printLogDebug(level);
					RecordPlayer.this.doctorCommentDialog.ShowSoundLevel(level);
				}
				targetDataLine.close();
				targetDataLine = null;
				RecordPlayer.this.doctorCommentDialog.ShowSoundLevel(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	class PlayThread extends Thread {
		byte tempBuffer[] = new byte[1000];

		public void run() {
			try {
				int cnt;
				int level = 0;
				// 读取数据到缓存数据
				while (!isStopPlay()) {
					synchronized (controlThread) {
						while (isPausePlay()) {
							RecordPlayer.this.doctorCommentDialog
									.ShowSoundLevel(0);
							controlThread.wait();
						}
						if (isStopPlay())
							break;
					}
					if ((cnt = audioInputStream.read(tempBuffer, 0,
							tempBuffer.length)) > 0) {
						// 写入缓存数据
						sourceDataLine.write(tempBuffer, 0, cnt);
					} else
						break;

					level = 0;
					for (int i = 0; i < cnt; ++i) {
						level += tempBuffer[i] * tempBuffer[i];
					}
					level /= cnt;
					level = (int) Math.sqrt(level);
					level = (level - 45) < 0 ? 0 : (level - 45);
					DebugTool.printLogDebug(level);
					RecordPlayer.this.doctorCommentDialog.ShowSoundLevel(level);
				}
				if (!isStopPlay()) {
					// Block等待临时数据被输出为空
					sourceDataLine.drain();
					RecordPlayer.this.doctorCommentDialog.PlayBackEnd();
				}
				setStopPlay(false);
				setPausePlay(false);
				sourceDataLine.close();
				RecordPlayer.this.doctorCommentDialog.ShowSoundLevel(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

}