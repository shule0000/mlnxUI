package ui.medlinx.com.extra;

import java.awt.Color;

import com.mlnx.pms.core.User;

public class ParamenterStore {

	private float verticalRange, v2hRatio;
	private Color lineColor, backgroundColor;
	private String ipString, portString;
	private int timeWindow, hrMax, hrMin, boMax, boMin, fontSize, lineWidth, scale;
	private boolean rememberUsrID;
	private String userID;
	private float screemPHYW, screemPHYH;
	private float mmPerPixelVertical, mmperPixelHorizontal;
	private Color oMVLineColor, channelDivLineColor;
	private boolean showOMVLine, showChannelDivLine;
	
	public ParamenterStore(SettingParameters settingParameters) {
		hrMax = settingParameters.getHrMax();
    	hrMin = settingParameters.getHrMin();
    	boMax = settingParameters.getBoMax();
    	boMin = settingParameters.getBoMin();
    	fontSize = settingParameters.getFontSize();
    	lineWidth = settingParameters.getLineWidth();
    	lineColor = settingParameters.getLineColor();
    	backgroundColor = settingParameters.getBackgroundColor();
    	ipString = settingParameters.getIpString();
    	portString = settingParameters.getPortString();
    	scale = settingParameters.getScale();//1:min,2:medium,3:max
    	verticalRange = settingParameters.getVerticalRange(); //range of ecg magnitude (y-axis)
    	v2hRatio = settingParameters.getV2hRatio();
    	rememberUsrID = settingParameters.isRememberUsrID();
    	userID = settingParameters.getUserID();
    	screemPHYW = settingParameters.getScreemPHYW();
    	screemPHYH = settingParameters.getScreemPHYH();
    	mmPerPixelVertical = settingParameters.getMmPerPixelVertical();
    	mmperPixelHorizontal = settingParameters.getMmperPixelHorizontal();
    	
    	oMVLineColor = settingParameters.getoMVLineColor();
		channelDivLineColor = settingParameters.getChannelDivLineColor();
		showOMVLine = settingParameters.isShowOMVLine();
		showChannelDivLine = settingParameters.isShowChannelDivLine();
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

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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

	public String getPortString() {
		return portString;
	}

	public void setPortString(String portString) {
		this.portString = portString;
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

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	
}
