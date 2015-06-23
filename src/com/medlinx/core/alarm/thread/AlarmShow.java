package com.medlinx.core.alarm.thread;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.Style;

import com.medlinx.core.alarm.AlertManager;


public class AlarmShow{
	
	// delay time延迟时间
	int alarmDelay[][] = {new int[1], Style.highAlarmDelay, Style.mediumAlarmDelay, Style.lowAlarmDelay};
	// visible show or not show
	final boolean alarmdShow[] = {true, false};
	
	private AlarmTask alarmTask;
	private AlertManager alertManager;
	ArrayList<JLabel> alarmLabel;
	Thread thread;

	public AlarmShow(ArrayList<JLabel> alarmLabel, AlertManager alertManager)
	{
		this.alertManager = alertManager;
		this.alarmLabel = alarmLabel;
	}
	
	public void setAlertManager(AlertManager alertManager) {
        this.alertManager = alertManager;
    }

    public void setAlarmLabel(ArrayList<JLabel> alarmLabel) 
	{
		this.alarmLabel = alarmLabel;
	}

	class AlarmTask implements Runnable
	{
		private volatile boolean runFlag = false; 

		public void run() {
			runFlag = true;
			while (runFlag)
			{
				for (int i = 0; i < 2; ++i)
				{
					for (JLabel label:alarmLabel)
					{
						SwingUtilities.invokeLater(new EDTRun(label, i));  //将对象排到事件派发线程的队列中
					}
					try {
						//Thread.sleep(alarmDelay[alarm.getAlarmLevel()][i]);
						Thread.sleep(alarmDelay[alertManager.getAlertLevel()][i]);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				SwingUtilities.invokeLater(new Runnable(){  
					  @Override  
					    public void run() {  
					    }  
					}); 
				//DebugTool.printLogDebug(runFlag);
			}
		}
		
		class EDTRun implements Runnable
		{
			JLabel label;
			int i;
			public EDTRun(JLabel label, int i)
			{
				this.label = label;
				this.i = i;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				label.setVisible(alarmdShow[i]);
			}
			
		}

		public boolean isRunFlag() {
			return runFlag;
		}

		public void setRunFlag(boolean runFlag) {
			this.runFlag = runFlag;
			DebugTool.printLogDebug("set runFlag");
		}
	}
	
	public void start() {
		if (null != thread)
			stop();
		alarmTask = new AlarmTask();
		thread = new Thread(alarmTask);
		thread.start();
	}
	
	public void stop() {
		if (null == thread)
			return;
		alarmTask.setRunFlag(false);
		DebugTool.printLogDebug("stop");
		if (thread.isAlive())
		{
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		thread = null;
	}
}
