package ui.medlinx.com.extra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import ui.medlinx.com.frame.Main.MLnxClient;

import com.itextpdf.text.Rectangle;
import com.mlnx.pms.core.User;

// TODO: settings file
public class SettingParameters {
	private int timeWindow, hrMax, hrMin, boMax, boMin, fontSize, lineWidth,
			scale;
	private int displayFrequency, dataFrequency;
	private Color lineColor, backgroundColor;
	private String ipString, portString;
	private boolean wifiAlarmOn, batteryAlarmOn, headerAlarmOn, bpmAlarmOn;
	int wifiAlarmBoundary;
	int batteryAlarmBoundary;
	private float verticalRange, v2hRatio;
	private static SettingParameters _instance;
	private int delay;
	private String userID, pwd;
	private boolean existGid;
	private Rectangle paperSize;
	private User loginUser;
	private double winHeight;
	private MLnxClient client;
	private boolean rememberUsrID;
	private float screemPHYW, screemPHYH;
	private float mmPerPixelVertical, mmperPixelHorizontal;
	private Color oMVLineColor, channelDivLineColor;
	private boolean showOMVLine, showChannelDivLine;
	
	public static SettingParameters getInstance() {
		if (_instance == null) {
			_instance = new SettingParameters();
		}
		return _instance;
	}

	public static void set_instance(SettingParameters _instance) {
		SettingParameters._instance = _instance;
	}

	public SettingParameters() {
		// default values
		timeWindow = 5;
		hrMax = 105;
		hrMin = 50;
		boMax = 100;
		boMin = 30;
		displayFrequency = 75; // default display frequency is 100 pt/sec
		dataFrequency = 300; // default data frequency is 300 pt/sec
		fontSize = 13;
		lineWidth = 1;
		lineColor = Color.green;
		backgroundColor = Color.gray;
		ipString = "192.168.1.105";
		portString = "8080";
		scale = 1;// 1:min,2:medium,3:max
		verticalRange = 2.0f; // range of ecg magnitude (y-axis)
		v2hRatio = 0.4f;
		bpmAlarmOn = true;
		headerAlarmOn = true;
		batteryAlarmOn = true;
		wifiAlarmOn = true;
		wifiAlarmBoundary = 0;
		batteryAlarmBoundary = 20;
		delay = -3;
		existGid = true;
		userID = "";
		pwd = "";
		loginUser = null;
		winHeight = 240;
		setPaperSize(Style.nomalPaperSize);
		rememberUsrID = false;

		// 屏幕物理尺寸
		mmPerPixelVertical = 0.28667f;
		mmperPixelHorizontal = 0.28263f;
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		screemPHYW = (float) (dimension.getWidth() * mmperPixelHorizontal);
		screemPHYH = (float) (dimension.getHeight() * mmPerPixelVertical);

		oMVLineColor = Color.RED;
		channelDivLineColor = Color.BLUE;
		showOMVLine = true;
		showChannelDivLine = true;
	}

	public void initSettingParameters(ParamenterStore paramenterStore) {
		hrMax = paramenterStore.getHrMax();
		hrMin = paramenterStore.getHrMin();
		boMax = paramenterStore.getBoMax();
		boMin = paramenterStore.getBoMin();
		fontSize = paramenterStore.getFontSize();
		lineWidth = paramenterStore.getLineWidth();
		lineColor = paramenterStore.getLineColor();
		backgroundColor = paramenterStore.getBackgroundColor();
		ipString = paramenterStore.getIpString();
		portString = paramenterStore.getPortString();
		scale = paramenterStore.getScale();// 1:min,2:medium,3:max
		verticalRange = paramenterStore.getVerticalRange(); // range of ecg
															// magnitude
															// (y-axis)
		v2hRatio = paramenterStore.getV2hRatio();
		userID = paramenterStore.getUserID();
		rememberUsrID = paramenterStore.isRememberUsrID();
		screemPHYW = paramenterStore.getScreemPHYW();
		screemPHYH = paramenterStore.getScreemPHYH();
		mmPerPixelVertical = paramenterStore.getMmPerPixelVertical();
		mmperPixelHorizontal = paramenterStore.getMmperPixelHorizontal();
		
		oMVLineColor = paramenterStore.getoMVLineColor();
		channelDivLineColor = paramenterStore.getChannelDivLineColor();
		showOMVLine = paramenterStore.isShowOMVLine();
		showChannelDivLine = paramenterStore.isShowChannelDivLine();
	}

	public boolean isShowOMVLine() {
		return showOMVLine;
	}

	public void setShowOMVLine(boolean showOMVLine) {
		this.showOMVLine = showOMVLine;
	}

	public boolean isShowChannelDivLine() {
		return showChannelDivLine;
	}

	public void setShowChannelDivLine(boolean showChannelDivLine) {
		this.showChannelDivLine = showChannelDivLine;
	}

	public Color getoMVLineColor() {
		return oMVLineColor;
	}

	public void setoMVLineColor(Color oMVLineColor) {
		this.oMVLineColor = oMVLineColor;
	}

	public Color getChannelDivLineColor() {
		return channelDivLineColor;
	}

	public void setChannelDivLineColor(Color channelDivLineColor) {
		this.channelDivLineColor = channelDivLineColor;
	}

	public float getScreemPHYW() {
		return screemPHYW;
	}

	public void setScreemPHYW(float screemPHYW) {
		this.screemPHYW = screemPHYW;
	}

	public float getScreemPHYH() {
		return screemPHYH;
	}

	public void setScreemPHYH(float screemPHYH) {
		this.screemPHYH = screemPHYH;
	}

	public float getMmPerPixelVertical() {
		return mmPerPixelVertical;
	}

	public void setMmPerPixelVertical(float mmPerPixelVertical) {
		this.mmPerPixelVertical = mmPerPixelVertical;
	}

	public float getMmperPixelHorizontal() {
		return mmperPixelHorizontal;
	}

	public void setMmperPixelHorizontal(float mmperPixelHorizontal) {
		this.mmperPixelHorizontal = mmperPixelHorizontal;
	}

	public boolean isRememberUsrID() {
		return rememberUsrID;
	}

	public void setRememberUsrID(boolean rememberUsrID) {
		this.rememberUsrID = rememberUsrID;
	}

	public boolean isExistGid() {
		return existGid;
	}

	public void setExistGid(boolean existGid) {
		this.existGid = existGid;
	}

	public Rectangle getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(Rectangle paperSize) {
		if (paperSize.getWidth() < paperSize.getHeight())
			this.paperSize = new Rectangle(paperSize.getHeight(),
					paperSize.getWidth());
		else
			this.paperSize = paperSize;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public float getVerticalRange() {
		return verticalRange;
	}

	public void setVerticalRange(float verticalRange) {
		this.verticalRange = verticalRange;
	}

	public float getV2hRatio() {
		return v2hRatio;
	}

	public void setV2hRatio(float v2hRatio) {
		this.v2hRatio = v2hRatio;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public int getHrMax() {
		return hrMax;
	}

	public void setHrMax(int hrMax) {
		this.hrMax = hrMax;
	}

	public int getHrMin() {
		return hrMin;
	}

	public void setHrMin(int hrMin) {
		this.hrMin = hrMin;
	}

	public int getBoMax() {
		return boMax;
	}

	public void setBoMax(int boMax) {
		this.boMax = boMax;
	}

	public int getBoMin() {
		return boMin;
	}

	public void setBoMin(int boMin) {
		this.boMin = boMin;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getIpString() {
		return ipString;
	}

	public void setIpString(String ipString) {
		this.ipString = ipString;
	}

	public void setClient(MLnxClient client) {
		this.client = client;
	}

	public double getWinHeight() {
		return winHeight;
	}

	public String getPortString() {
		return portString;
	}

	public void setPortString(String portString) {
		this.portString = portString;
	}

	public int getDisplayFrequency() {
		return displayFrequency;
	}

	public void setDisplayFrequency(int displayFrequency) {
		this.displayFrequency = displayFrequency;
	}

	public boolean isWifiAlarmOn() {
		return wifiAlarmOn;
	}

	public void setWifiAlarmOn(boolean wifiAlarmOn) {
		this.wifiAlarmOn = wifiAlarmOn;
	}

	public boolean isBatteryAlarmOn() {
		return batteryAlarmOn;
	}

	public void setBatteryAlarmOn(boolean batteryAlarmOn) {
		this.batteryAlarmOn = batteryAlarmOn;
	}

	public boolean isHeaderAlarmOn() {
		return headerAlarmOn;
	}

	public void setHeaderAlarmOn(boolean headerAlarmOn) {
		this.headerAlarmOn = headerAlarmOn;
	}

	public boolean isBpmAlarmOn() {
		return bpmAlarmOn;
	}

	public void setBpmAlarmOn(boolean bpmAlarmOn) {
		this.bpmAlarmOn = bpmAlarmOn;
	}

	public int getWifiAlarmBoundary() {
		return wifiAlarmBoundary;
	}

	public void setWifiAlarmBoundary(int wifiAlarmBoundary) {
		this.wifiAlarmBoundary = wifiAlarmBoundary;
	}

	public int getBatteryAlarmBoundary() {
		return batteryAlarmBoundary;
	}

	public void setBatteryAlarmBoundary(int batteryAlarmBoundary) {
		this.batteryAlarmBoundary = batteryAlarmBoundary;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User user) {
		this.loginUser = user;
	}

	public int getDataFrequency() {
		return dataFrequency;
	}

	public void setDataFrequency(int dataFrequency) {
		this.dataFrequency = dataFrequency;
	}

}
