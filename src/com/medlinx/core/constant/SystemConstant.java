package com.medlinx.core.constant;

import java.awt.Color;
import java.util.ArrayList;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;

import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.DataClientBuilder;

public class SystemConstant {
	
	// 背景色
	public final static Color BG_COLOR = new Color(0, 153, 160);

	// 软件版本
	public final static int mVersion = 1;
	public final static int sVersion = 1;

	// file path
	public static String MLNX_LOGO_WHITE_FILE_PATH = "res/bg/mlnx_logo_white.png";
	public static String MLNX_LOGO_FILE_PATH = "res/bg/mlnx_logo.png";
	public static String MAIN_BG_FILE_PATH = "res/bg/main_bg.jpg";
	public static String BUTTON_BG_FILE_PATH = "res/bg/button_bg.jpg";
	public static String USR_BG_FILE_PATH = "res/bg/button_bg.jpg";

	public static int ECGCHANNELNUM = 12; // the number of channels
	public static float ECGREFERENCE = 2.4f; // reference voltage
	public static int ECGBASELINE = 32768; // baseline for ECG signal
	public static int ECGPEAK = 65535; // the value to detect peak of ECG caused
										// by VVI
	public static float ECGPEAK_MV = 15.5f; // the value of peak of ECG(mV)
											// caused by VVI
	// public static
	public static ArrayList<String> ECGLEADNAMES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("I");
			add("II");
			add("III");
			add("aVR");
			add("aVL");
			add("aVF");
			add("V1");
			add("V2");
			add("V3");
			add("V4");
			add("V5");
			add("V6");
		}
	};

	public static final int ORIENTATION_MASK = 0x0E; // 111b; & with this num to
														// obtain the
														// orientation bits
	public static final int MOTION_MASK = 0x80; // x1000000b; & with this num to
												// obtain the motion bits
	// public static final int MOTION =
	public static final int LEFTWARD = 0x7; // 111b;
	public static final int RIGHTWARD = 0x5; // 101b;
	public static final int UPWARD = 0x6; // 110b;
	public static final int DOWNWARD = 0x4; // 100b;
	// public static final int STAND = 0x; //000b 001b 010b 011b
	public static final int ECG_INVALIDVALUE = 65534; // invalid value from
														// server, convert to
														// ECG_INVALID (mV)
	public static final int ECG_NOTDRAW = 0; // invalid value from server,
												// convert to ECG_INVALID (mV)
	public static final int BPM_INVALIDVALUE = -1;
	public static final int ECG_GAIN = 6;
	public static final int ECG_THRESHOLD = 4;
	public static final float ECG_INVALID = -15.0f; // -15mv is invalid value of
													// ECG
	public static final int FAKEPATIENT = -1; // patient id of fake patient(not
												// real data)
	public static final int EXCEPTIONAL_HEARTRATE = 254; // when bpm is 254
															// means some
															// exception occurs
	public static final float VERTICALRANGE = 2.0f;
	public static final int ECG_COMMON_FREQUENCY = 300; // standard frequency of
														// ECG = 300
	public static final int ECG_OPER_FREQUENCY = 75;// operating frequency of
													// ECG = 75
	public static final int ECG_NORMAL_FREQUENCY = 150;// operating frequency of
														// ECG = 150

	public static final Color[] ALL_COLORS = { Color.BLACK, Color.BLUE,
			Color.CYAN, Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
			Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK,
			Color.RED, Color.WHITE, Color.YELLOW };

	public static final float ECGConvertor(int dataBufferV) {
		if (dataBufferV == ECG_INVALIDVALUE)
			return ECG_INVALID;
		if (dataBufferV == ECG_NOTDRAW)
			return ECG_INVALID;
		if (dataBufferV == ECGPEAK)
			return ECGPEAK_MV;
		float output = (dataBufferV - SystemConstant.ECGBASELINE) * 1000
				* SystemConstant.ECGREFERENCE
				/ ((float) Math.pow(2.0, 20) * ECG_GAIN);
		if (output > VERTICALRANGE * 1.5f) {
			// DebugTool.printLogDebug("out of drawing range of the panel");
			output = VERTICALRANGE * 1.5f;
		}
		if (output < -VERTICALRANGE * 1.5f)
			output = -VERTICALRANGE * 1.5f;

		return output;
	}

	public static final float ECGConvertorWOBoundary(int dataBufferV) {
		if (dataBufferV == ECG_INVALIDVALUE)
			return ECG_INVALID;
		if (dataBufferV == ECG_NOTDRAW)
			return ECG_INVALID;
		if (dataBufferV == ECGPEAK) {
			DebugTool.printLogDebug("original peak value detected!");
			return ECGPEAK_MV;
		}
		float output = (dataBufferV - SystemConstant.ECGBASELINE) * 1000
				* SystemConstant.ECGREFERENCE
				/ ((float) Math.pow(2.0, 20) * ECG_GAIN);
		return output;
	}

	public static final DataClient constructDataClient() {
		SettingParameters parameter = SettingParameters.getInstance();
		DataClient dataClient = DataClientBuilder.newBuilder()
				.withServerHostname(parameter.getIpString())
				.withCredentials(parameter.getUserID(), parameter.getPwd())
				.build();
		return dataClient;
	}

	/*
	 * public enum AlarmType { PHYSIOLOGICAL, TECHNICAL }
	 */
	public static final Color HIGHALARMCOLOR = Color.red;
	public static final Color MEDIUMALARMCOLOR = Color.yellow;
	public static final Color LOWALARMCOLOR = Color.yellow;
	/*
	 * public static final Alarm BPMLOWALARM = new
	 * Alarm(1,"心率过低",AlarmType.PHYSIOLOGICAL); public static final Alarm
	 * BPMHIGHALARM = new Alarm(1,"心率过高",AlarmType.PHYSIOLOGICAL); public static
	 * final Alarm BATTERYLOWALARM = new Alarm(3,"电池余量不足",AlarmType.TECHNICAL);
	 * public static final Alarm WIFILOWALARM = new
	 * Alarm(3,"无线信号过低",AlarmType.TECHNICAL); public static final Alarm
	 * HEADERLOSTALARM = new Alarm(2,"电极脱落",AlarmType.TECHNICAL); public static
	 * final Alarm FIDELITYALARM = new Alarm(2,"偏压过高波形失真",AlarmType.TECHNICAL);
	 */
}
