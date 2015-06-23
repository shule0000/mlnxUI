package ui.medlinx.com.resource;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class SystemResources {
	public static final int HEIGHT = 65;
	public static final int WIDTH = 65;
	
	public static final ImageIcon MlnxImageIcon = new ImageIcon("res/icon.png");
	
	public static final ImageIcon imageIconFaceDownMotion = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceDown_Motion.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceDown = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceDown.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceLeftMotion = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceLeft_Motion.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceLeft = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceLeft.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceRightMotion = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceRight_Motion.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceRight = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceRight.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceUpMotion = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceUp_Motion.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconFaceUp = new ImageIcon(
			(new ImageIcon("res/patientPose/FaceUp.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconStandMotion = new ImageIcon(
			(new ImageIcon("res/patientPose/Stand_Motion.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon imageIconStand = new ImageIcon(
			(new ImageIcon("res/patientPose/Stand.png")).getImage()
					.getScaledInstance(WIDTH, HEIGHT,
							java.awt.Image.SCALE_SMOOTH));
	public static final ImageIcon[] batteryIconList = {
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_1.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_2.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_3.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_4.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_5.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_6.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_7.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_8.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_9.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_10.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_11.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_12.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_13.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_14.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_15.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/battery/BatteryBG_17.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)) };
	public static final ImageIcon[] leadIconList = {
			new ImageIcon((new ImageIcon("res/lead/LA_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/LA_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/LL_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/LL_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/RA_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/RA_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/RL_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/RL_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V1_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V1_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V2_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V2_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V3_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V3_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V4_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V4_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V5_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V5_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V6_Green.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/lead/V6_Red.png")).getImage()
					.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)) };
	public static final ImageIcon[] wifiIconList = {
			new ImageIcon((new ImageIcon("res/wifi/Wireless_Sig_0bar_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/wifi/Wireless_Sig_1bar_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/wifi/Wireless_Sig_2bar_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/wifi/Wireless_Sig_3bar_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)),
			new ImageIcon((new ImageIcon("res/wifi/Wireless_Sig_4bar_16.png"))
					.getImage().getScaledInstance(30, 15,
							java.awt.Image.SCALE_SMOOTH)), };

	public static final ImageIcon playIcon = new ImageIcon(
			"res/ui_icons/playback_play_icon&32.png");
	public static final ImageIcon pauseIcon = new ImageIcon(
			"res/ui_icons/playback_pause_icon&32.png");
	public static final ImageIcon printIcon = new ImageIcon(
			"res/ui_icons/print_icon&32.png");
	public static final ImageIcon gridIcon = new ImageIcon(
			"res/ui_icons/3x3_grid_icon&32.png");
	public static final ImageIcon soundIcon = new ImageIcon(
			"res/ui_icons/sound_high_icon&32.png");
	public static final ImageIcon muteIcon = new ImageIcon(
			"res/ui_icons/sound_mute_icon&32.png");
	public static final ImageIcon historyIcon = new ImageIcon(
			"res/ui_icons/clipboard_past_icon&32.png");
	public static final ImageIcon heartIcon = new ImageIcon(
			"res/ui_icons/heart_icon.png");
	public static final ImageIcon prevIcon = new ImageIcon(
			"res/ui_icons/br_prev_icon&24.png");
	public static final ImageIcon nextIcon = new ImageIcon(
			"res/ui_icons/br_next_icon&24.png");
	public static final ImageIcon cancelIcon = new ImageIcon(
			"res/ui_icons/bell_x_icon&32.png");
	public static final ImageIcon notCancelIcon = new ImageIcon(
			"res/ui_icons/bell_icon&32.png");
	public static final ImageIcon tempMuteIcon[] = {
			new ImageIcon("res/ui_icons/sound_low_0_icon&32.png"),
			new ImageIcon("res/ui_icons/sound_low_1_icon&32.png"),
			new ImageIcon("res/ui_icons/sound_low_2_icon&32.png") };
	public static final ImageIcon tempCancelIcon[] = {
			new ImageIcon("res/ui_icons/bell_0_icon&32.png"),
			new ImageIcon("res/ui_icons/bell_1_icon&32.png"),
			new ImageIcon("res/ui_icons/bell_2_icon&32.png") };
	public static final String AlarmSoundFile[] = { "",
			"res/900Hz_Alarm_HighPriority.wav",
			"res/900Hz_Alarm_MidPriority.wav",
			"res/900Hz_Alarm_LowPriority.wav" };
	// public static final ImageIcon hardwareWarningIcon = new
	// ImageIcon("res/event_icon/eye_inv_icon&16.png");
	// public static final ImageIcon physicalWarningIcon = new
	// ImageIcon("res/event_icon/emotion_sad_icon&16.png");

	public static final ImageIcon historyPrevIcon = new ImageIcon(
			"res/ui_icons/br_prev_icon&24.png");
	public static final ImageIcon historyNextIcon = new ImageIcon(
			"res/ui_icons/br_next_icon&24.png");
	public static final ImageIcon historyZoomInIcon = new ImageIcon(
			"res/ui_icons/round_plus_icon&24.png");
	public static final ImageIcon historyZoomOutIcon = new ImageIcon(
			"res/ui_icons/round_minus_icon&24.png");

	public static final ImageIcon searchIcon = new ImageIcon(
			"res/ui_icons/zoom_icon&16.png");
	public static final ImageIcon doctorHeadIcon = new ImageIcon(
			"res/ui_icons/dcotor.jpg");
	public static final ImageIcon mlnxLogoIcon = new ImageIcon(
			"res/ui_icons/mlnx_logo.jpg");
	
	// 鼠标图标
	public static final ImageIcon cursorArrowIcon = new ImageIcon(
			"res/ui_icons/cursor_arrow_icon&32.png");
	
	// 日历选择图标
	public static final ImageIcon prevMonthIcon = new ImageIcon(
			"res/ui_icons/prev_month.png");
	public static final ImageIcon prevYearIcon = new ImageIcon(
			"res/ui_icons/prev_year.png");
	public static final ImageIcon nextMonthIcon = new ImageIcon(
			"res/ui_icons/next_month.png");
	public static final ImageIcon nextYearIcon = new ImageIcon(
			"res/ui_icons/next_year.png");
	public static final ImageIcon riliIcon = new ImageIcon(
			"res/ui_icons/rili.jpg");
}
