package ui.medlinx.com.frame.heart;

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
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.databuff.HeartRateDataBuffer;





public class HeartRateDrawingPanel extends JPanel{
	private HeartRateFrame mainFrame;
	private HeartRateDataBuffer dataBuffer;
	private int borderSize;
	private int legendBorder;
	private int canvasWidth;
	private int canvasHeight;
	private int maxHeight;
	private float verticalRangePanel;
	private float amplitudeScaling;
	private int maxIndexDrawPT;
	private int[] xIndex;
	private boolean isDrawBackground = true;
	private float scaleTemp = 0.5f;
	private SettingParameters parameters;
	MyComponentListener resizeListener;
	float[] displayBuffer;//list of buffers for display
	private int displayStartTime; // the start minute of display window
	private boolean windowChanged;
	private Timer repaintTimer;

	public HeartRateDrawingPanel(HeartRateFrame hf, HeartRateDataBuffer dbi){
		parameters = SettingParameters.getInstance();
		mainFrame = hf;
		dataBuffer = dbi;
		borderSize = Style.DrawingPanelBorderSize;
		initializeComponents();
		maxHeight = 800;
		maxIndexDrawPT = 10;
		legendBorder = 15;
		verticalRangePanel = 280.0f; //range of ecg magnitude (y-axis)
		displayStartTime = 0;
		//initialize the x-axis and y-axis for the canvas
		// use maxTimeWindow=45s, means allocate enough space for adjusting
		xIndex = new int[maxIndexDrawPT];
		this.setBorder(new LineBorder(Style.DrawingPanelBorderColor, borderSize));
		displayBuffer = new float[maxIndexDrawPT];
		for (int i = 0; i<maxIndexDrawPT; ++i)
		{
			displayBuffer[i] = (float)SystemConstant.BPM_INVALIDVALUE;;
			xIndex[i] = 0;
		}
		//prepare the canvas: calculate the length of time window from verticalRang and v2hRatio
		prepareCanvas(this.getWidth(),this.getHeight());
		windowChanged = true;
		repaintTimer = new Timer(500, new RepaintTask());
		repaintTimer.start();
	}

	public HeartRateDataBuffer getDataBuffer() {
		return dataBuffer;
	}

	public void setDataBuffer(HeartRateDataBuffer dataBuffer) {
		this.dataBuffer = dataBuffer;
	}

	/**
	 * This private class derived from ComponentListener defines behavior when resizing or other standard event occurs
	 * @author jfeng
	 *
	 */
	private class MyComponentListener implements ComponentListener
	{

		@Override
		public void componentHidden(ComponentEvent arg0) {}

		@Override
		public void componentMoved(ComponentEvent arg0) {}

		@Override
		public void componentResized(ComponentEvent arg0) {
			DebugTool.printLogDebug("resize!!");
			//setting for information panel

			// Adjust the layout of information panel. (one column)
			int intervalI = (int) (((float)(getHeight()-220))/9.0); 
			DebugTool.printLogDebug(getHeight());
			if (intervalI>15)
				intervalI = 15;
			if (intervalI<0)
				intervalI = 0;

			prepareCanvas(HeartRateDrawingPanel.this.getWidth(),
					HeartRateDrawingPanel.this.getHeight());
			HeartRateDrawingPanel.this.repaint();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {}

	}

	private void initializeComponents() {
		// TODO Auto-generated method stub
		resizeListener  = new MyComponentListener();
		this.addComponentListener(resizeListener);
		this.setBackground(Color.BLACK);
	}

	// Call this every time the frame changes
	private void prepareCanvas(int width, int height)
	{
		DebugTool.printLogDebug("prepareCanvas");
		//every time to reconstruct the canvas, will start a new screen
		//		firstRound = true;
		//		indexDrawPT = 0;

		canvasWidth = width -(borderSize*2);
		canvasHeight = height -(borderSize*2)-legendBorder;

		float heightPerChannel = (float)canvasHeight;

		DebugTool.printLogDebug("heightPerChannel " + heightPerChannel);
		if (heightPerChannel>maxHeight)
		{
			heightPerChannel = maxHeight;
			DebugTool.printLogDebug("heightPerChannel is too large");
		}else if (heightPerChannel < Style.DrawingPanelMinHeight){
			heightPerChannel = Style.DrawingPanelMinHeight;
			DebugTool.printLogDebug("heightPerChannel is too small");
		}
		amplitudeScaling = (float)(heightPerChannel)/(verticalRangePanel);
		float pixelRateF = (float)canvasWidth/(maxIndexDrawPT);

		for (int i  = 0; i<maxIndexDrawPT ; ++i)
			xIndex[i] = (int)((i*pixelRateF)+(pixelRateF/2));
	}
	/**
	 * paint function for this component
	 */
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2D = (Graphics2D)g;
		if (g==null)
			return;
		if (isDrawBackground )
			drawGrid(g2D,parameters.getBackgroundColor());
		drawInformation(g2D,parameters.getBackgroundColor());
		// draw ECG curves
		if(windowChanged)
		{
			// load data from data pool into data buffer
			loadDisplayBufferList();
		}
		g2D.setColor(parameters.getLineColor());
		g2D.setColor(Color.red);
		g2D.setStroke(new BasicStroke(parameters.getLineWidth()));
		int y1=0,y2=0;
		float heightPerChannel = (float)canvasHeight;
		for (int i = 1;i<=maxIndexDrawPT-1;++i)
		{
			//DebugTool.printLogDebug(""+i+":"+indexDrawPT+":"+maxIndexDrawPT+":");
			y1 = (int)(displayBuffer[i-1]*amplitudeScaling);
			y2 = (int)(displayBuffer[i]*amplitudeScaling);
			y1 = (int)(heightPerChannel - y1);
			y2 = (int)(heightPerChannel - y2);
			String bpmStr = String.format("%d b/min", (int)displayBuffer[i-1]);
			
			if (displayBuffer[i-1]==SystemConstant.BPM_INVALIDVALUE
					||displayBuffer[i]==SystemConstant.BPM_INVALIDVALUE)
				continue;
			g2D.drawLine(xIndex[i-1]+borderSize, y1, xIndex[i]+borderSize,y2);
			if(displayBuffer[i-1]>=0)
				g2D.drawString(bpmStr, xIndex[i-1]-10, y1-10);
		}
		String bpmStr = String.format("%d b/min", (int)displayBuffer[maxIndexDrawPT-1]);
		if(displayBuffer[maxIndexDrawPT-1]>=0)
			g2D.drawString(bpmStr, xIndex[maxIndexDrawPT-1]-10, y2-10);

	}
	
	private void loadDisplayBufferList() {
		// load data needed to show on screen
		// displayStart is the start point in second
		// TODO: put ecg values into displayBufferList(jianqiao)
		int tempI = 0;
		float tempF = 0.0f;

		for(int j=0;j<maxIndexDrawPT;++j)
		{
			if ((j+displayStartTime)>dataBuffer.getMaxElement())
				break;
			tempI = dataBuffer.getHeartRate(j+displayStartTime);
			tempF = (float)tempI;
			//DebugTool.printLogDebug("display: "+j+":"+indexDrawPT+":"+maxIndexDrawPT+":"+tempF);
			displayBuffer[j] = tempF;
		}
	}

	/**
	 * Drawing patient information and title of leads
	 * @param g graphics object for drawing
	 * @param c color of background
	 */
	private void drawInformation(Graphics2D g, Color c)
	{
		Color drawInformationColor = Color.white;//or parameters.getLineColor() 
		float heightPerChannel = (float)canvasHeight;
		int heightTemp = canvasHeight;
		g.setFont(new Font("TimesRoman", Font.PLAIN, parameters.getFontSize()));
		g.setColor(parameters.getLineColor());
		g.setColor(drawInformationColor);
		//seconds x-axis legend
		int fontSizeT = 10;
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.setColor(drawInformationColor);
		Date startTime = dataBuffer.getStartTime();
		if (startTime == null)
			startTime = Calendar.getInstance().getTime();
		for (int i = 0; i<maxIndexDrawPT; ++i)
		{
			Date currentScreenTime = new Date(startTime.getTime()+ 
					((displayStartTime+i)*((int)(dataBuffer.getSkipMin()*60*1000))));
			DateFormat df = new SimpleDateFormat("HH时mm分");
			String startTimeStr = df.format(currentScreenTime);
			String strTemp = String.format("%s ",startTimeStr);
			g.drawString(strTemp, xIndex[i]-15, heightTemp+10);
		}

		// drawing the unit and start time
		
		Date currentScreenTime = new Date(startTime.getTime()+ 
				(displayStartTime*1000l));
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		String startTimeStr = df.format(currentScreenTime);
		String strTemp = String.format("日期：%s ",startTimeStr);
		fontSizeT = 13;
		g.setColor(drawInformationColor);
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.drawString(strTemp,15,15);

		//drawing the wifi signal icon 

		//drawing the patient information
		if (dataBuffer==null||dataBuffer.getPatientInfo()==null)
			return;
		
		String patientInformation = "";
		if (dataBuffer.getPatient().getPatientID()>=0)
		{
			patientInformation = "病人编号："+dataBuffer.getPatientInfo().getId()+" 姓名："
				+dataBuffer.getPatientInfo().getName()+" 性别："
				+dataBuffer.getPatientInfo().getGender().toString();
		}
		else
			patientInformation = "尚未选择病人";
		g.drawString(patientInformation,canvasWidth-250,15);
	}

	/**
	 * draw background, e.g. for ECG we draw grid as background
	 * @param g graphics object for drawing
	 * @param c color of background
	 */
	private void drawGrid(Graphics2D g, Color c)
	{

		// small grid = 0.5v
		float timeInterval = amplitudeScaling*scaleTemp;
		int heightTemp = canvasHeight;
		g.setColor(Color.black);
		g.fillRect(borderSize, borderSize, canvasWidth,heightTemp);
		g.setColor(c.darker());
		g.setStroke(new BasicStroke(1F));
		//To draw bold lines
		int startX = xIndex[0]/2;
		int startXLegend = startX-15; 
		if (startX<15)
		{
			startX = xIndex[0];
			startXLegend = 0;
		}
		//vertical line for time
		for (int i=0;i<xIndex.length;++i)
		{
			g.drawLine((int)xIndex[i], borderSize+20, (int)xIndex[i], canvasHeight);
		}
		int yTemp = 0;
		
		startX = xIndex[0];
		for (int i = 0;i<12;++i)//0~260
		{
			yTemp = canvasHeight - (int)((i*20)*amplitudeScaling);
			g.drawLine(startX, yTemp, canvasWidth, yTemp);
			g.drawString((i*20)+"", startXLegend, yTemp);
		}
		g.drawString("BPM", startXLegend, 20+borderSize);

	}

	public int getDisplayStartPoint() {
		return displayStartTime;
	}

	public void setDisplayStartPoint(int value) {
		displayStartTime = value;
//		if (displayStartTime>(dataBuffer.getMaxElement()-maxIndexDrawPT))
//		{
//			dataBuffer.getOffset(displayStartTime);
//			displayStartTime = 0;
//		}
	}

	public boolean next() {
		displayStartTime += maxIndexDrawPT;
		if (displayStartTime>(dataBuffer.getMaxElement()-maxIndexDrawPT))
		{
			dataBuffer.getOffset(displayStartTime);
			displayStartTime = 0;
			return true;
		}
		return false;
	}

	public boolean prev() {
		displayStartTime -= maxIndexDrawPT;
		if (displayStartTime<0)
		{
			dataBuffer.getOffset(displayStartTime);
			displayStartTime = 0;
			return true;
		}
		return false;
	}
	private class RepaintTask implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			HeartRateDrawingPanel.this.loadDisplayBufferList();
			HeartRateDrawingPanel.this.repaint();
		}
	}
}
