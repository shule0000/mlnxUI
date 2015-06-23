package ui.medlinx.com.frame.ECGhistory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.databuff.HistoryDataBuffer;

public class HistoryDrawingPanel extends JPanel {
	private HistoryFrame mainFrame;
	private ArrayList<String> channelList;
	private HistoryDataBuffer dataBuffer;
	private int borderSize;
	private int canvasWidth;
	private int canvasHeight;
	private int NChannel;
	private int[] yChannel;
	private int maxHeight;
	private float amplitudeScaling;
	private float timeWindow;
	private int ptPerSecond;
	private int maxIndexDrawPT;
	private float pixelRateF;
	private int maxTimeWindow;
	private int[] xIndex;
	private boolean isDrawBackground = true;
	private float scaleTemp = 0.5f;
	private SettingParameters parameters;
	MyComponentListener resizeListener;
	ArrayList<float[]> displayBufferList;// list of buffers for display
	private int displayStartTime; // the start second of display window
	private boolean windowChanged;
	private int bufferRate; // interval between two data PT in data buffer
	private int gain; // information gain factor
	private Timer repaintTimer;

	public HistoryDrawingPanel(HistoryFrame hf, HistoryDataBuffer dbi) {
		parameters = SettingParameters.getInstance();
		mainFrame = hf;
		dataBuffer = dbi;
		channelList = new ArrayList<String>();
		setupChannelList();
		borderSize = Style.DrawingPanelBorderSize;
		initializeComponents();
		NChannel = dataBuffer.getNChannel();
		yChannel = new int[NChannel];
		maxTimeWindow = dataBuffer.getSecondBuffer(); // max size of draw area
														// is 90 seconds
		maxHeight = 600;
		ptPerSecond = 300;
		gain = 12;
		displayStartTime = 0;
		bufferRate = dataBuffer.getFrequency() / ptPerSecond;
		// initialize the x-axis and y-axis for the canvas
		// use maxTimeWindow=45s, means allocate enough space for adjusting
		xIndex = new int[maxTimeWindow * ptPerSecond];
		this.setBorder(new LineBorder(Style.DrawingPanelBorderColor, borderSize));
		displayBufferList = new ArrayList<float[]>();
		for (int i = 0; i < NChannel; ++i) {
			float[] tempArray = new float[maxTimeWindow * ptPerSecond];
			for (int j = 0; j < maxTimeWindow * ptPerSecond; ++j)
				tempArray[j] = -1;
			displayBufferList.add(tempArray);
			yChannel[i] = 0;
		}
		// prepare the canvas: calculate the length of time window from
		// verticalRang and v2hRatio
		prepareCanvas(this.getWidth(), this.getHeight());
		windowChanged = true;
		repaintTimer = new Timer(1000, new RepaintTask());
		repaintTimer.start();
	}

	public HistoryDataBuffer getDataBuffer() {
		return dataBuffer;
	}

	public void setDataBuffer(HistoryDataBuffer dataBuffer) {
		this.dataBuffer = dataBuffer;
	}

	/**
	 * This private class derived from ComponentListener defines behavior when
	 * resizing or other standard event occurs
	 * 
	 * @author jfeng
	 * 
	 */
	private class MyComponentListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent arg0) {
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			DebugTool.printLogDebug("resize!!");
			// setting for information panel

			// Adjust the layout of information panel. (one column)
			int intervalI = (int) (((float) (getHeight() - 220)) / 9.0);
			DebugTool.printLogDebug(getHeight());
			if (intervalI > 15)
				intervalI = 15;
			if (intervalI < 0)
				intervalI = 0;

			prepareCanvas(HistoryDrawingPanel.this.getWidth(),
					HistoryDrawingPanel.this.getHeight());
			HistoryDrawingPanel.this.repaint();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
		}

	}

	private void initializeComponents() {
		// TODO Auto-generated method stub
		resizeListener = new MyComponentListener();
		this.addComponentListener(resizeListener);
		this.setBackground(Color.BLACK);
	}

	// Call this every time the frame changes
	private void prepareCanvas(int width, int height) {
		DebugTool.printLogDebug("prepareCanvas");
		// every time to reconstruct the canvas, will start a new screen
		// firstRound = true;
		// indexDrawPT = 0;

		canvasWidth = width - (borderSize * 2);
		canvasHeight = height - (borderSize * 2);

		float heightPerChannel = (float) canvasHeight / (float) NChannel;
		for (int i = 0; i < NChannel; ++i) {
			yChannel[i] = (int) (i * heightPerChannel);
		}

		DebugTool.printLogDebug("heightPerChannel " + heightPerChannel);
		if (heightPerChannel > maxHeight) {
			heightPerChannel = maxHeight;
			DebugTool.printLogDebug("heightPerChannel is too large");
		} else if (heightPerChannel < Style.DrawingPanelMinHeight) {
			heightPerChannel = Style.DrawingPanelMinHeight;
			DebugTool.printLogDebug("heightPerChannel is too small");
		}
		SettingParameters parameters = SettingParameters.getInstance();
		float mmPermV = 10.0f;
		float v2hRatioPanel = 0.4f;
		amplitudeScaling = mmPermV / parameters.getMmPerPixelVertical();
		float vPerPix = parameters.getMmperPixelHorizontal() / mmPermV;
		timeWindow = canvasWidth * vPerPix * v2hRatioPanel;
		maxIndexDrawPT = (int) (timeWindow * ptPerSecond);

		pixelRateF = ((float) canvasWidth / (float) maxIndexDrawPT);
		for (int i = 0; i < maxIndexDrawPT && i < xIndex.length; ++i)
			xIndex[i] = (int) (i * pixelRateF);
		// DebugTool.printLogDebug("prepare canvas:"+maxIndexDrawPT+":"+canvasWidth+":"+canvasHeight+":"+amplitudeScaling);
	}

	/**
	 * set up the list of channel names, i.e. "I", "II", "III"
	 */
	private void setupChannelList() {
		if (dataBuffer == null)
			return;
		channelList.clear();
		for (int i = 0; i < SystemConstant.ECGCHANNELNUM; ++i) {
			if (dataBuffer.getPatient().getChannelFlag(i))
				channelList.add(SystemConstant.ECGLEADNAMES.get(i));
		}
	}

	/**
	 * paint function for this component
	 */
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2D = (Graphics2D) g;
		if (g == null)
			return;
		if (isDrawBackground)
			drawGrid(g2D, parameters.getBackgroundColor());
		drawInformation(g2D, parameters.getBackgroundColor());
		// draw ECG curves
		if (windowChanged) {
			// load data from data pool into data buffer
			loadDisplayBufferList();
		}
		g2D.setColor(parameters.getLineColor());
		g2D.setStroke(new BasicStroke(parameters.getLineWidth()));
		int y1 = 0, y2 = 0;
		float heightPerChannel = (float) canvasHeight / (float) NChannel;
		for (int indexC = 0; indexC < NChannel; ++indexC)
			for (int i = 1; i <= maxIndexDrawPT - 1; ++i) {
				// DebugTool.printLogDebug(""+i+":"+indexDrawPT+":"+maxIndexDrawPT+":");
				y1 = (int) (displayBufferList.get(indexC)[i - 1] * amplitudeScaling);
				y2 = (int) (displayBufferList.get(indexC)[i] * amplitudeScaling);
				y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
				y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
				if (displayBufferList.get(indexC)[i - 1] == SystemConstant.ECG_INVALID
						|| displayBufferList.get(indexC)[i] == SystemConstant.ECG_INVALID)
					continue;
				g2D.drawLine(xIndex[i - 1] + borderSize, y1, xIndex[i]
						+ borderSize, y2);
			}

	}

	// convert value from data buffer to mv
	private float convert2V(int dataBufferV) {
		if (dataBufferV < 0)
			return SystemConstant.ECG_INVALID;
		float output = (dataBufferV - SystemConstant.ECGBASELINE) * 1000
				* SystemConstant.ECGREFERENCE
				/ ((float) Math.pow(2.0, 20) * gain);
		return output;
	}

	private void loadDisplayBufferList() {
		// load data needed to show on screen
		// displayStart is the start point in second
		// TODO: put ecg values into displayBufferList(jianqiao)
		int tempI = 0;
		float tempF = 0.0f;
		for (int i = 0; i < NChannel; ++i) {
			for (int j = 0; j < maxIndexDrawPT; ++j) {
				tempI = dataBuffer.getLocalMeanChannel(displayStartTime
						* dataBuffer.getFrequency() + (j * bufferRate),
						bufferRate, i);
				tempF = convert2V(tempI);
				// DebugTool.printLogDebug("display: "+j+":"+indexDrawPT+":"+maxIndexDrawPT+":"+tempF);
				displayBufferList.get(i)[j % maxIndexDrawPT] = tempF;
			}

		}

	}

	/**
	 * Drawing patient information and title of leads
	 * 
	 * @param g
	 *            graphics object for drawing
	 * @param c
	 *            color of background
	 */
	private void drawInformation(Graphics2D g, Color c) {

		if (timeWindow > maxTimeWindow)
			return;
		Color drawInformationColor = Color.white;// or parameters.getLineColor()

		float heightPerChannel = (float) canvasHeight / (float) NChannel;
		int heightTemp = canvasHeight;
		g.setFont(new Font("TimesRoman", Font.PLAIN, parameters.getFontSize()));
		g.setColor(parameters.getLineColor());
		// draw the unit sample signal
		float pos = 10 + amplitudeScaling;
		g.drawLine(5, (int) pos, 5,
				(int) (pos - amplitudeScaling));
		g.drawLine(5, (int) (pos - amplitudeScaling),
				xIndex[ptPerSecond],
				(int) (pos - amplitudeScaling));
		g.drawLine(xIndex[ptPerSecond],
				(int) (pos - amplitudeScaling),
				xIndex[ptPerSecond], (int) (pos));
		g.setColor(drawInformationColor);
		for (int i = 0; i < channelList.size() && i < yChannel.length; ++i) {
			g.drawString(channelList.get(i), canvasWidth / 2 - 10,
					yChannel[i] + 20);
		}

		// drawing the unit and start time
		Date startTime = dataBuffer.getStartTime();
		if (startTime == null)
			startTime = Calendar.getInstance().getTime();
		Date currentScreenTime = new Date(startTime.getTime()
				+ (displayStartTime * 1000));
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日HH时 mm分ss秒");
		String startTimeStr = df.format(currentScreenTime);
		String strTemp = String.format("%s  显示比例：%.1f(mv) x %.2f (s) 每格",
				startTimeStr, scaleTemp, 0.2);
		int fontSizeT = 15;
		g.setColor(drawInformationColor);
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.drawString(strTemp, xIndex[ptPerSecond], 15);

		// seconds x-axis legend
		fontSizeT = 15;
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.setColor(drawInformationColor);
		df = new SimpleDateFormat("mm分");
		SimpleDateFormat dfSecond = new SimpleDateFormat("ss秒");
		for (int i = 0; i < timeWindow; ++i) {
			currentScreenTime = new Date(startTime.getTime()
					+ ((displayStartTime + i) * 1000));
			if (currentScreenTime.getTime() % (60 * 1000) < 1000)
				startTimeStr = df.format(currentScreenTime);
			else
				startTimeStr = dfSecond.format(currentScreenTime);

			g.drawString(startTimeStr, xIndex[i * ptPerSecond], heightTemp - 5);
		}

		// drawing the wifi signal icon

		// drawing the patient information
		String patientInformation = "";
		if (dataBuffer.getPatient().getPatientID() == SystemConstant.FAKEPATIENT)
			patientInformation = "演示数据";
		else
			patientInformation = "病人编号："
					+ dataBuffer.getPatient().getPatientID() + " 姓名："
					+ dataBuffer.getPatient().getPatientName() + " 性别："
					+ dataBuffer.getPatient().getGender();
		g.drawString(patientInformation, canvasWidth - 250, 15);
	}

	/**
	 * draw background, e.g. for ECG we draw grid as background
	 * 
	 * @param g
	 *            graphics object for drawing
	 * @param c
	 *            color of background
	 */
	private void drawGrid(Graphics2D g, Color c) {
		// small grid = 0.5v
		float timeInterval = amplitudeScaling * scaleTemp;
		int heightTemp = canvasHeight;
		g.setColor(Color.black);
		g.fillRect(borderSize, borderSize, canvasWidth, heightTemp);
		g.setColor(c.darker());
		g.setStroke(new BasicStroke(1F));
		// To draw thin lines
		float startIndex = borderSize;
		// DebugTool.printLogDebug("painting grids:"+timeInterval+":"+startIndex+":"+canvasWidth+":"+borderSize);
		while (startIndex < canvasWidth + borderSize) {
			g.drawLine((int) startIndex, borderSize, (int) startIndex,
					heightTemp);
			startIndex += timeInterval;
		}

		startIndex = borderSize;
		while (startIndex < heightTemp + borderSize) {
			g.drawLine(borderSize, (int) startIndex, canvasWidth,
					(int) startIndex);
			startIndex += timeInterval;
		}

		int strokeWidth = 1;
		g.setColor(c);
		g.setStroke(new BasicStroke(strokeWidth));
		// To draw bold lines
		startIndex = borderSize;
		while (startIndex < canvasWidth + borderSize) {
			g.drawLine((int) startIndex, borderSize, (int) startIndex,
					heightTemp);
			startIndex += (timeInterval * 5);
		}
		startIndex = borderSize;
		while (startIndex < heightTemp + borderSize) {
			g.drawLine(borderSize, (int) startIndex, canvasWidth,
					(int) startIndex);
			startIndex += (timeInterval * 5);
		}

	}

	public int getDisplayStartPoint() {
		return displayStartTime;
	}

	public void setDisplayStartPoint(int value) {
		displayStartTime = value;
	}

	public boolean next() {
		displayStartTime += timeWindow;
		if (displayStartTime > (maxTimeWindow - timeWindow)) {
			dataBuffer.getOffset(displayStartTime);
			displayStartTime = 0;
			return true;
		}
		return false;
	}

	public boolean prev() {
		displayStartTime -= timeWindow;
		if (displayStartTime < 0) {
			System.out
					.println("displayStartTime negative: " + displayStartTime);
			dataBuffer.getOffset(displayStartTime);
			displayStartTime = 0;
			return true;
		}
		return false;
	}

	public Date getCurrentTime() {
		if (dataBuffer != null)
			return null;
		return dataBuffer.getStartTime();
	}

	private class RepaintTask implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			HistoryDrawingPanel.this.loadDisplayBufferList();
			HistoryDrawingPanel.this.repaint();
		}
	}
}
