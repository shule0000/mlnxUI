package ui.medlinx.com.frame.Main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.dialog.ChoseLeadDialog;
import ui.medlinx.com.dialog.PrintPreviewDialog;
import ui.medlinx.com.dialog.WaitingTimeDialog;
import ui.medlinx.com.doctor_tool.DoctorCommentDialog;
import ui.medlinx.com.doctor_tool.PrintPatientInfor;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;
import ui.medlinx.com.frame.ECGhistory.HistoryFrame;
import ui.medlinx.com.frame.heart.HeartRateFrame;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.alarm.Alert;
import com.medlinx.core.alarm.AlertManager;
import com.medlinx.core.alarm.MetaInformation;
import com.medlinx.core.alarm.thread.AlarmShow;
import com.medlinx.core.alarm.thread.AlarmTimer;
import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.databuff.DataBufferInterface;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.core.Device.DataType;
import com.mlnx.pms.core.Device.Mode;

/**
 * This class is the component for drawing ECG Curves and displaying
 * informations
 * 
 * @author Jianqiao Feng
 * 
 */
public class DrawingPanel extends JPanel
		implements
			ActionListener,
			ItemListener {

	private static final long serialVersionUID = 1L;
	private static final int maxDataFrequency = 300;
	private static final float WARNINGTH = 0.2f; // if writing pointer is
													// "warningth" in front of
													// reading, then need to
													// jump backward
	private static final float WARNINGTHLAG = -2.5f;// threshold need to jump
													// forward
	private static final int[] channelNumList = {1, 7, 12};
	private static final int ALARM_INTERVAL = 1000;

	/* fields related to drawing */
	private int indexDataBuffer; // 记录后台网络传输的databuff 获取过程中的位置
	private int indexDrawPT; // 显示buff当前写入的位置
	private int maxIndexDrawPT;// max index of
								// data
								// point in
								// display
								// buffer
	// (size of complete display buffer)
	private int canvasWidth; // width of the canvas (drawing area) **
	private int canvasHeight; // Height of the canvas (drawing area) **
	private int maxTimeWindow; // max value of time window (45s)
	private int ptPerSecond; // #PT per second for displaying **
	private int borderSize; // border width of this component
	private int gain; // signal gain factor
	private int delay; // seconds that reading pointer(display buffer) lap
						// writing pointer(data buffer)
	private float timeWindow; // length of time for data displayed in the whole
								// screen,can be 1.3s
	private float bufferRate; // interval between two data PT in data buffer,
								// change to float on 04/23/2014
	private float pixelRateF; // interval(pixel) between two data PT in display
								// buffer

	// Color lineColor;
	private Color informationColor;
	private int BPMStatus, BPMStatusPre;
	private int fidelity;
	private int widthInformationPanel; // width of information panel
	private float amplitudeScaling;
	private float verticalRangePanel, v2hRatioPanel;
	private float mmPermV;

	// components in this panel
	private JButton settingButton, buttonPause, buttonPrint;
	private JTextField screemHeighTextField;
	private JButton buttonGrid, buttonSound, buttonHistory, buttonHeartRate;
	private JButton tempMuteButton, cancelAllAlarmButton, tempCancelButton;

	private JPanel informationPanel, controlPanel, displayPanel;
	private JLabel tempTitle, tempLabel, poseIcon;
	private JLabel labelBeat, labelPerMin, devBattIcon;
	private JButton leadSelectButton; // 导联选择
	private JLabel labelWarningMessage;
	private JLabel rssiLabel, batteryLabel, electrodeHeaderLabel;
	private JLabel pressureLabel, oxygenLabel;
	private JLabel labelVerticalRange, labelV2hRatio, labelMode;
	private JLabel bmpAlarmLabel, batteryAlarmLabel, wifiAlarmLabel,
			headerAlarmLabel, waveformDistortionAlarmLabel;// 偏压过高波形失真
	private JComboBox boxVerticalRange, boxV2hRatio, boxMode;
	private HashSet<JLabel> highAlarmLabelMap, mediumAlarmLabelMap,
			lowAlarmLabelMap;

	// buffer.
	private long updateStartT = 0, updateCurrentT = 0;
	private long currentPT = 0; // 总共获取的显示的数据量，用来更新下次需要更新的数据量是多少

	// status of patient
	private int motionCode, bpmTemp;

	// alarm
	private int tempMuteFlag, tempCancelFlag; // 暂时静音时间 暂时取消生理报警时间
	private int level;
	private float scaleTemp = 0.5f;
	private boolean startFlag; // flag indicates whether already started
	private boolean cancelFlag; // 关闭所有报警

	// different modes of device
	private String[] modeStrs = {"高精度ECG", "尖峰监测", "心电图机", "普通监护", "运动监护"};
	private Mode[] modeOptions = {Mode.ECG_ADVANCED,
			Mode.ECG_ADVANCED_WITH_SPIKE_DETECT, Mode.ECG_ELECTROCARDIOGRAPH,
			Mode.ECG_NORMAL, Mode.ECG_OPERATING_ROOM};
	private String[] v2hRatioStrs = {"6.25mm/s", "12.5mm/s", "25mm/s", "50mm/s"};
	private float[] v2hRatioOptions = {6.25f, 12.5f, 25f, 50f};
	private String[] verticalScaleStrs = {"2.5mm/mV", "5mm/mV", "10mm/mV",
			"15mm/mV", "20mm/mV"};
	private float[] verticalScaleOptions = {2.5f, 5.0f, 10.0f, 15.0f, 20.0f};
	private DataType[] channelNumOptions = {DataType.ECG_1CH, DataType.ECG_3CH,
			DataType.ECG_8CH};

	private boolean initializeFlag = true;
	private boolean[] peakFlag;
	private boolean firstRound, suspendFlag, isMax, freezeFlag, connectFlag,
			callFromPatient;
	private boolean isDrawBackground;// configuration for drawing curves
	private boolean soundOn;
	private boolean alarmOn = false;

	/* read data from data buffer */
	private ReadDatabufferTask readDataTask; // time-task for refresh this panel
												// itself
	private Timer readDataTimer; // timer to schedule the time-task (read data
									// from data buffer)

	private ArrayList<JLabel> leadLabels;
	private ArrayList<JLabel> highAlarmLabel, mediumAlarmLabel, lowAlarmLabel;
	private ArrayList<Integer> alarmList;

	// all buff channel
	private ArrayList<float[]> displayBufferList;// list of buffers for display
	private int NChannel; // #channel in data buffer

	// select buff channel
	private float heightPerChannel;
	private int validSelectChanelNumb = 0;
	private boolean[] selectChannelFlag; // 记住选择的channel
	private List<String> selectChanelNameList;
	private ArrayList<float[]> selectDisplayBufferList; // 记住选择的buffList
	private int[] xIndex;
	private int[] yChannel;

	private MLnxClient mainFrame; // the main window contain this component
	private DataBufferInterface dataBuffer;// MultiChannelBuffer dataBuffer;
	private Patient patient;
	private SettingParameters parameters;
	private AlarmShow highAlarmShow, mediumAlarmShow, lowAlarmShow;
	private AlarmTimer alarmTimer; // timer to show alarm light
	private MetaInformation information = new MetaInformation();
	private AlertManager alertManager = new AlertManager();
	private Alert alert = new Alert();

	private MyComponentListener resizeListener;

	private int model = 1;
	private JPanel devInfoBottom;

	/**
	 * constructor
	 * 
	 * @param _mainFrame
	 *            parent frame window
	 * @param outBuffer
	 *            data buffer
	 */
	public DrawingPanel(MLnxClient _mainFrame, DataBufferInterface outBuffer) {

		connectFlag = true;
		mainFrame = _mainFrame;
		parameters = SettingParameters.getInstance();// setup all parameters
		alertManager.setMetaInformation(information);
		alertManager.setSettingParameters(parameters);
		// set the vertical range and vertical to horizontal ratio to the
		// default value
		verticalRangePanel = parameters.getVerticalRange();
		// v2hRatioPanel = parameters.getV2hRatio();
		// timeWindow = parameters.getTimeWindow();
		isMax = false;

		// databuff patient
		dataBuffer = outBuffer;
		patient = dataBuffer.getPatient();

		isDrawBackground = true;
		informationColor = Style.InfoAreaBackgroundColor;
		tempMuteFlag = 0;
		tempCancelFlag = 0;
		mmPermV = 5.0f;
		cancelFlag = false;
		initializeComponents();
		buttonPause.disable();
		bufferTime(); // 4 seconds for buffering data
		initialMode();
		delay = parameters.getDelay();
		fidelity = 0;
		// read data from data buffer
		readDataTask = new ReadDatabufferTask();
		readDataTimer = new javax.swing.Timer(30, readDataTask);
		ptPerSecond = dataBuffer.getFrequency();
		bufferRate = 1;
		startFlag = false;
		firstRound = true;
		freezeFlag = true;
		suspendFlag = false;
		BPMStatus = 0;
		BPMStatusPre = 0;
		canvasWidth = 500;
		canvasHeight = 400;
		borderSize = Style.DrawingPanelBorderSize;
		maxTimeWindow = 90; // max size of draw area is 90 seconds
		gain = 6;
		widthInformationPanel = 450;
		callFromPatient = false;
		highAlarmLabel = new ArrayList<JLabel>();
		mediumAlarmLabel = new ArrayList<JLabel>();
		lowAlarmLabel = new ArrayList<JLabel>();
		highAlarmLabelMap = new HashSet<JLabel>();
		mediumAlarmLabelMap = new HashSet<JLabel>();
		lowAlarmLabelMap = new HashSet<JLabel>();
		alarmList = new ArrayList<Integer>();

		highAlarmShow = new AlarmShow(null, null);
		mediumAlarmShow = new AlarmShow(null, null);
		lowAlarmShow = new AlarmShow(null, null);
		highAlarmShining(true);
		mediumAlarmShining(true);
		lowAlarmShining(true);
		level = 4;
		// initialize the x-axis and y-axis for the canvas
		// use maxTimeWindow=45s, means allocate enough space for adjusting
		xIndex = new int[maxTimeWindow * maxDataFrequency];
		peakFlag = new boolean[maxTimeWindow * maxDataFrequency];

		initChanelAndBuff();

		this.setBorder(new LineBorder(Style.DrawingPanelBorderColor, borderSize));
		prepareCanvas(this.getWidth(), this.getHeight());

		buttonPause.setEnabled(false);
		if (patient.getPatientID() > 0) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							resumeDraw();
							buttonPause.setEnabled(true);
						}
					});
				}
			}).start();
		}
	}

	/**
	 * 初始化通道和buff
	 */
	private void initChanelAndBuff() {
		NChannel = dataBuffer.getNChannel();
		displayBufferList = new ArrayList<float[]>();
		selectDisplayBufferList = new ArrayList<float[]>();
		selectChanelNameList = new ArrayList<String>();
		for (int i = 0; i < NChannel; ++i) {
			float[] tempArray = new float[maxTimeWindow * maxDataFrequency];
			for (int j = 0; j < maxTimeWindow * ptPerSecond; ++j) {
				tempArray[j] = SystemConstant.ECG_INVALID;
				peakFlag[j] = false;
			}
			displayBufferList.add(tempArray);
			selectDisplayBufferList.add(tempArray);
			selectChanelNameList.add(SystemConstant.ECGLEADNAMES.get(i));
		}
		yChannel = new int[NChannel];
		for (int j = 0; j < yChannel.length; j++) {
			yChannel[j] = 0;
		}
		validSelectChanelNumb = NChannel;
		selectChannelFlag = new boolean[NChannel];
		for (int i = 0; i < selectChannelFlag.length; i++) {
			selectChannelFlag[i] = true;
			yChannel[i] = 0;
		}
	}

	/**
	 * 选择显示的导联
	 */
	public void selectChannel(boolean[] selectChannelFlag, int model) {

		this.model = model;
		endDraw();
		while (!finishTTimesDrawECG);
		int sumSelectLeads = 0;
		ArrayList<float[]> selectDisplayBufferList = new ArrayList<float[]>();
		selectChanelNameList = new ArrayList<String>();
		for (int i = 0; i < selectChannelFlag.length; i++) {
			if (selectChannelFlag[i]) {
				sumSelectLeads++;
				selectDisplayBufferList.add(displayBufferList.get(i));
				selectChanelNameList.add(SystemConstant.ECGLEADNAMES.get(i));
			}
		}
		yChannel = new int[sumSelectLeads];
		for (int j = 0; j < yChannel.length; j++) {
			yChannel[j] = 0;
		}
		this.selectDisplayBufferList = selectDisplayBufferList;
		this.validSelectChanelNumb = sumSelectLeads;
		this.selectChannelFlag = selectChannelFlag;

		devInfoBottom.removeAll();
		if (model == 1) {
			for (int i = 0; i < leadLabels.size(); i++) {
				leadLabels.get(i).setSize(new Dimension(20, 20));
				leadLabels.get(i).setBackground(informationColor);
				leadLabels.get(i).setForeground(Style.InfoAreaForegroundColor);
				devInfoBottom.add(leadLabels.get(i));
			}
		} else {
			for (int i = 0; i < leadLabels.size(); i++) {
				if (i != 4 && i != 5 && i != 6 && i != 7 && i != 9) {
					leadLabels.get(i).setSize(new Dimension(20, 20));
					leadLabels.get(i).setBackground(informationColor);
					leadLabels.get(i).setForeground(
							Style.InfoAreaForegroundColor);
					devInfoBottom.add(leadLabels.get(i));
				}
			}

		}

		restartDraw();
	}

	/**
	 * to initialize the mode of device
	 */
	private void initialMode() {
		int selectedIndex = 0;
		for (int i = 0; i < modeOptions.length; ++i)
			if (dataBuffer.getDeviceMode() == modeOptions[i])
				selectedIndex = i;
		initializeFlag = true;
		boxMode.setSelectedIndex(selectedIndex);
		initializeFlag = false;
	}

	private void startAlarm() {
		DebugTool.printLogDebug("startAlarm");
		alarmOn = true;
		alarmTimer = new AlarmTimer(ALARM_INTERVAL, 0, labelWarningMessage,
				"警报");
		alarmTimer.start();
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm");

		final JOptionPane optionPane = new JOptionPane(sim.format(dataBuffer
				.getCallTime())
				+ "病人"
				+ dataBuffer.getPatient().getPatientID()
				+ dataBuffer.getPatient().getPatientName() + "呼叫!",
				JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = new JDialog(mainFrame, "病人呼叫", true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				DebugTool
						.printLogDebug("Thwarted user attempt to close window.");
			}
		});
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (dialog.isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					// If you were going to check something
					// before closing the window, you'd do
					// it here.
					dialog.setVisible(false);
				}
			}
		});
		Rectangle main = mainFrame.getBounds();
		Rectangle bounds = this.getBounds();
		dialog.setBounds(main.x + bounds.x + bounds.width / 2 - 50, main.y
				+ bounds.y + bounds.height / 2 - 40, 100, 80);
		dialog.pack();
		dialog.setVisible(true);

		int value = ((Integer) optionPane.getValue()).intValue();
		if (value == JOptionPane.OK_OPTION) {
			callFromPatient = false;
			endAlarm();
			DebugTool.printLogDebug("Good.");
		}
	}

	private void endAlarm() {
		alarmOn = false;
		alarmTimer.stop();
	}

	private JButton addControlButton(ImageIcon icon, String tip, JPanel panel) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setSize(Style.InfoButtonDimension);
		button.setBackground(informationColor);
		button.setForeground(Style.InfoAreaForegroundColor);
		button.addActionListener(this);
		button.setToolTipText(tip);
		button.setPreferredSize(Style.InfoButtonDimension);
		removeBorder(button);
		panel.add(button);
		return button;
	}

	/**
	 * initialize all UI components
	 */
	// @SuppressWarnings("deprecation")
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void initializeComponents() {
		setLayout(null);
		settingButton = new JButton("显示设置");
		settingButton.setBounds(getWidth() - 50, 10, 60, 30);
		settingButton.addActionListener(this);
		resizeListener = new MyComponentListener();
		this.addComponentListener(resizeListener);

		// information panel
		informationPanel = new JPanel();
		informationPanel.setLayout(new BoxLayout(informationPanel,
				BoxLayout.PAGE_AXIS));
		informationPanel.setBounds((getWidth() - Style.InfoAreaWidth), 0,
				Style.InfoAreaWidth, getHeight() + 50);

		informationPanel.setForeground(Style.InfoAreaForegroundColor);
		informationPanel.setBackground(informationColor);
		informationPanel.setBorder(new LineBorder(
				Style.DrawingPanelBorderColor, borderSize));
		this.add(informationPanel);

		/**
		 * 控制区域
		 */
		// controlPanel = new JPanel();
		java.awt.Image image = new ImageIcon("res/bc3.png").getImage();
		controlPanel = new BackgroundPanel(image);
		controlPanel
				.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		controlPanel.setForeground(Style.InfoAreaForegroundColor);
		controlPanel.setBackground(informationColor);
		TitledBorder topButtonBorder = BorderFactory.createTitledBorder("控制区");
		topButtonBorder.setTitleColor(Color.GREEN);
		topButtonBorder.setTitleFont(Style.InfoTitleFont);
		controlPanel.setBorder(topButtonBorder);
		controlPanel.setPreferredSize(Style.ControlPanelDimension);
		controlPanel.setMinimumSize(new Dimension(150, 200));

		// 控制按钮
		{
			JPanel ctlBar = new JPanel();
			GridLayout gl = new GridLayout(0, 4);
			ctlBar.setLayout(gl);
			ctlBar.setForeground(Style.InfoAreaForegroundColor);
			ctlBar.setBackground(informationColor);
			controlPanel.add(ctlBar);

			buttonPause = this.addControlButton(SystemResources.playIcon, "开始",
					ctlBar);//
			buttonPrint = this.addControlButton(SystemResources.printIcon,
					"打印", ctlBar);
			buttonGrid = this.addControlButton(SystemResources.gridIcon,
					"关闭网格", ctlBar);
			buttonHistory = addControlButton(SystemResources.historyIcon,
					"历史数据", ctlBar);
			buttonSound = addControlButton(SystemResources.soundIcon, "音量开",
					ctlBar);
			tempMuteButton = addControlButton(SystemResources.tempMuteIcon[0],
					"暂时静音", ctlBar);
			cancelAllAlarmButton = addControlButton(
					SystemResources.notCancelIcon, "禁止生理报警", ctlBar);
			tempCancelButton = addControlButton(
					SystemResources.tempCancelIcon[0], "暂时禁止生理报警", ctlBar);
		}

		// 比例控制
		{
			JPanel scaleBar = new JPanel();
			scaleBar.setLayout(new BoxLayout(scaleBar, BoxLayout.LINE_AXIS));
			scaleBar.setForeground(Style.InfoAreaForegroundColor);
			scaleBar.setBackground(informationColor);
			scaleBar.setPreferredSize(Style.ScaleBarDimension);
			controlPanel.add(scaleBar);

			labelVerticalRange = new JLabel("增益");
			labelVerticalRange.setFont(Style.InfoSubTitleFont);
			// labelVerticalRange.setBounds(172, 115, 89, 23);
			labelVerticalRange.setForeground(Style.InfoAreaForegroundColor);
			labelVerticalRange.setBackground(informationColor);

			labelV2hRatio = new JLabel("显示速度");
			labelV2hRatio.setFont(Style.InfoSubTitleFont);
			labelV2hRatio.setForeground(Style.InfoAreaForegroundColor);
			labelV2hRatio.setBackground(informationColor);

			boxVerticalRange = new JComboBox(verticalScaleStrs);
			boxVerticalRange.addActionListener(new ComboBoxListener());
			boxVerticalRange.setBackground(informationColor);
			boxVerticalRange.setForeground(Style.InfoAreaForegroundColor);
			boxVerticalRange.setSelectedIndex(2);
			mmPermV = verticalScaleOptions[2];

			boxV2hRatio = new JComboBox(v2hRatioStrs);
			boxV2hRatio.setBackground(informationColor);
			boxV2hRatio.addActionListener(new ComboBoxListener());
			boxV2hRatio.setSelectedIndex(2);
			boxV2hRatio.setForeground(Style.InfoAreaForegroundColor);
			v2hRatioPanel = mmPermV
					/ v2hRatioOptions[boxV2hRatio.getSelectedIndex()];

			scaleBar.add(labelVerticalRange);
			scaleBar.add(boxVerticalRange);
			scaleBar.add(labelV2hRatio);
			scaleBar.add(boxV2hRatio);

		}
		// 模式选择
		{
			String[] ChannelOptions = new String[channelNumList.length];
			for (int i = 0; i < channelNumList.length; ++i)
				ChannelOptions[i] = Integer.toString(channelNumList[i]);

			JPanel serverControlBar = new JPanel();
			serverControlBar.setLayout(new BoxLayout(serverControlBar,
					BoxLayout.LINE_AXIS));
			serverControlBar.setForeground(Style.InfoAreaForegroundColor);
			serverControlBar.setBackground(informationColor);
			serverControlBar.setPreferredSize(Style.ScaleBarDimension);

			labelMode = new JLabel("模式");
			labelMode.setFont(Style.InfoSubTitleFont);
			// labelVerticalRange.setBounds(172, 115, 89, 23);
			labelMode.setForeground(Style.InfoAreaForegroundColor);
			labelMode.setBackground(informationColor);

			boxMode = new JComboBox(modeStrs);
			boxMode.setPrototypeDisplayValue("xxxxx");
			boxMode.setBackground(informationColor);
			boxMode.addActionListener(new ComboBoxListener());
			boxMode.setSize(50, 20);
			boxMode.setSelectedIndex(1);
			boxMode.setForeground(Style.InfoAreaForegroundColor);

			serverControlBar.add(labelMode);
			serverControlBar.add(boxMode);

			soundOn = true;
			controlPanel.add(serverControlBar);
		}
		// 选择导联
		{
			leadSelectButton = new JButton("导联选择");
			leadSelectButton.setBackground(Color.CYAN);
			leadSelectButton.addActionListener(this);
			JPanel panel = new JPanel(new GridLayout(1, 1));
			panel.add(leadSelectButton);
			controlPanel.add(panel);
		}

		/**
		 * 显示信息区域
		 */
		displayPanel = new BackgroundPanel(image);
		displayPanel
				.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));
		displayPanel.setForeground(Style.InfoAreaForegroundColor);
		displayPanel.setBackground(informationColor);
		TitledBorder midDisplayBorder = BorderFactory.createTitledBorder("显示区");
		midDisplayBorder.setTitleColor(Color.GREEN);
		midDisplayBorder.setTitleFont(Style.InfoTitleFont);
		displayPanel.setBorder(midDisplayBorder);

		devBattIcon = new JLabel();
		devBattIcon.setFont(new Font("宋体", Font.PLAIN, 11));
		devBattIcon.setForeground(Style.InfoAreaForegroundColor);
		devBattIcon.setBackground(informationColor);
		devBattIcon.setIcon(SystemResources.batteryIconList[0]);

		// 生理信号
		{
			JPanel physiologicalSignalPanel = new JPanel();
			displayPanel.add(physiologicalSignalPanel);
			physiologicalSignalPanel
					.setForeground(Style.InfoAreaForegroundColor);
			physiologicalSignalPanel.setBackground(informationColor);
			TitledBorder heartRatePanelBorder = BorderFactory
					.createTitledBorder("生理信息");
			heartRatePanelBorder.setTitleColor(Style.InfoAreaForegroundColor);
			heartRatePanelBorder.setTitleFont(Style.InfoSubTitleFont);
			physiologicalSignalPanel.setBorder(heartRatePanelBorder);
			physiologicalSignalPanel.setLayout(new BoxLayout(
					physiologicalSignalPanel, BoxLayout.Y_AXIS));

			// 心率 血氧
			{
				JPanel hspPanel = new JPanel(new GridLayout(1, 2));
				physiologicalSignalPanel.add(hspPanel);
				// 心率
				{
					JPanel panel = new JPanel(new BorderLayout());
					hspPanel.add(panel);
					panel.setBackground(Color.BLACK);
					TitledBorder titledBorder = BorderFactory
							.createTitledBorder("ECG");
					titledBorder.setTitleColor(Color.GREEN);
					panel.setBorder(titledBorder);
					{
						JPanel panel2 = new JPanel(new FlowLayout(
								FlowLayout.RIGHT));
						panel2.setBackground(Color.BLACK);
						JLabel label = new JLabel("bpm");
						label.setForeground(Color.GREEN);
						panel2.add(label);
						panel.add(panel2, BorderLayout.NORTH);
					}
					labelBeat = new JLabel("80", JLabel.CENTER);
					labelBeat.setForeground(Color.GREEN);
					labelBeat.setFont(new Font("Arial Black", Font.BOLD, 30));
					panel.add(labelBeat, BorderLayout.CENTER);
				}
				// 血氧
				{
					JPanel panel = new JPanel(new BorderLayout());
					panel.setEnabled(false);
					hspPanel.add(panel);
					panel.setBackground(Color.BLACK);
					TitledBorder titledBorder = BorderFactory
							.createTitledBorder("SPO");
					titledBorder.setTitleColor(Color.RED);
					panel.setBorder(titledBorder);
					{
						JPanel panel2 = new JPanel(new FlowLayout(
								FlowLayout.RIGHT));
						panel2.setBackground(Color.BLACK);
						JLabel label = new JLabel("%");
						label.setForeground(Color.RED);
						panel2.add(label);
						panel.add(panel2, BorderLayout.NORTH);
					}
					oxygenLabel = new JLabel("未连接", JLabel.CENTER);
					oxygenLabel.setForeground(Color.RED);
					oxygenLabel.setFont(new Font("楷体", Font.BOLD, 25));
					panel.add(oxygenLabel, BorderLayout.CENTER);
				}
			}

			{
				JPanel hspPanel = new JPanel(new GridLayout(1, 2));
				physiologicalSignalPanel.add(hspPanel);
				// 无创血压
				{
					JPanel panel = new JPanel(new BorderLayout());
					panel.setEnabled(false);
					panel.setBackground(Color.BLACK);
					hspPanel.add(panel);
					TitledBorder titledBorder = BorderFactory
							.createTitledBorder("血压");
					titledBorder.setTitleColor(Color.YELLOW);
					panel.setBorder(titledBorder);
					{
						JPanel panel2 = new JPanel(new FlowLayout(
								FlowLayout.RIGHT));
						panel2.setBackground(Color.BLACK);
						JLabel label = new JLabel("mmHg");
						label.setForeground(Color.YELLOW);
						panel2.add(label);
						panel.add(panel2, BorderLayout.NORTH);
					}
					pressureLabel = new JLabel("未连接", JLabel.CENTER);
					pressureLabel.setForeground(Color.YELLOW);
					pressureLabel.setFont(new Font("楷体", Font.BOLD, 25));
					panel.add(pressureLabel, BorderLayout.CENTER);
				}
				// 体温
				{
					JPanel panel = new JPanel(new BorderLayout());
					panel.setEnabled(false);
					panel.setBackground(Color.BLACK);
					hspPanel.add(panel);
					TitledBorder titledBorder = BorderFactory
							.createTitledBorder("体温");
					titledBorder.setTitleColor(Color.WHITE);
					panel.setBorder(titledBorder);
					{
						JPanel panel2 = new JPanel(new FlowLayout(
								FlowLayout.RIGHT));
						panel2.setBackground(Color.BLACK);
						JLabel label = new JLabel("°C");
						label.setForeground(Color.WHITE);
						panel2.add(label);
						panel.add(panel2, BorderLayout.NORTH);
					}
					tempLabel = new JLabel("未连接", JLabel.CENTER);
					tempLabel.setForeground(Color.WHITE);
					tempLabel.setFont(new Font("楷体", Font.BOLD, 25));
					panel.add(tempLabel, BorderLayout.CENTER);
				}
			}
		}

		// 报警
		{
			JPanel analysisPanel = new JPanel();
			analysisPanel.setLayout(new BoxLayout(analysisPanel,
					BoxLayout.Y_AXIS));
			displayPanel.add(analysisPanel);
			analysisPanel.setForeground(Style.InfoAreaForegroundColor);
			analysisPanel.setBackground(informationColor);
			TitledBorder analysisPanelBorder = BorderFactory
					.createTitledBorder("报警");
			analysisPanelBorder.setTitleColor(Style.InfoAreaForegroundColor);
			analysisPanelBorder.setTitleFont(Style.InfoSubTitleFont);
			analysisPanel.setBorder(analysisPanelBorder);
			analysisPanel.setPreferredSize(Style.InfoSubSectionDimension);

			{
				labelWarningMessage = addAlarmLabel("网络存在问题");
				JPanel panel = new JPanel(new GridLayout(1, 1));
				panel.setBackground(Color.BLACK);
				panel.add(labelWarningMessage);
				analysisPanel.add(panel);
			}

			{
				JPanel panel = new JPanel(new GridLayout(2, 2, 4, 4));
				panel.setBackground(Color.BLACK);

				bmpAlarmLabel = addAlarmLabel("心率过高");
				batteryAlarmLabel = addAlarmLabel("电量不足");
				wifiAlarmLabel = addAlarmLabel("无线信号弱");
				headerAlarmLabel = addAlarmLabel("导联脱落");

				panel.add(bmpAlarmLabel);
				panel.add(batteryAlarmLabel);
				panel.add(wifiAlarmLabel);
				panel.add(headerAlarmLabel);
				analysisPanel.add(panel);
			}

			{
				waveformDistortionAlarmLabel = addAlarmLabel("偏压过高波形失真");
				JPanel panel = new JPanel(new GridLayout(1, 1));
				panel.setBackground(Color.BLACK);
				panel.add(waveformDistortionAlarmLabel);
				analysisPanel.add(panel);
			}
		}

		poseIcon = new JLabel(SystemResources.imageIconFaceDownMotion);
		poseIcon.setFont(new Font("宋体", Font.PLAIN, 11));
		poseIcon.setForeground(Style.InfoAreaForegroundColor);
		poseIcon.setBackground(informationColor);
		poseIcon.setIcon(SystemResources.imageIconFaceDownMotion);

		// add device information
		rssiLabel = new JLabel();
		batteryLabel = new JLabel();

		electrodeHeaderLabel = new JLabel();
		JPanel devInfoPanel = new JPanel();
		devInfoPanel.setForeground(Style.InfoAreaForegroundColor);
		devInfoPanel.setBackground(informationColor);
		TitledBorder devInfoPanelBorder = BorderFactory
				.createTitledBorder("设备");
		devInfoPanelBorder.setTitleColor(Style.InfoAreaForegroundColor);
		devInfoPanelBorder.setTitleFont(Style.InfoSubTitleFont);
		devInfoPanel.setBorder(devInfoPanelBorder);

		// devInfoBottom: the panel contains all lead icons,
		// located at bottom of devInfoPanel
		devInfoBottom = new JPanel();
		GridLayout gridLayout = new GridLayout(0, 5);
		devInfoBottom.setLayout(gridLayout);
		leadLabels = new ArrayList<JLabel>();
		for (int i = 0; i < 10; ++i) {
			JLabel labelTemp = new JLabel();
			labelTemp.setIcon(SystemResources.leadIconList[i * 2]);
			leadLabels.add(labelTemp);
		}

		for (JLabel labelTemp : leadLabels) {
			labelTemp.setSize(new Dimension(20, 20));
			labelTemp.setBackground(informationColor);
			labelTemp.setForeground(Style.InfoAreaForegroundColor);
			devInfoBottom.add(labelTemp);

		}
		devInfoBottom.setForeground(Style.InfoAreaForegroundColor);
		devInfoBottom.setBackground(informationColor);
		devInfoPanel.setLayout(new FlowLayout());
		rssiLabel.setIcon(SystemResources.wifiIconList[0]);
		batteryLabel.setIcon(SystemResources.batteryIconList[0]);
		devInfoPanel.add(rssiLabel);
		devInfoPanel.add(batteryLabel);
		devInfoPanel.add(devInfoBottom);

		JPanel posePanel = new JPanel();
		posePanel.setForeground(Style.InfoAreaForegroundColor);
		posePanel.setBackground(informationColor);
		TitledBorder posePanelBorder = BorderFactory.createTitledBorder("体态");
		posePanelBorder.setTitleColor(Style.InfoAreaForegroundColor);
		posePanelBorder.setTitleFont(Style.InfoSubTitleFont);
		posePanel.setBorder(posePanelBorder);
		posePanel.setPreferredSize(Style.InfoSubSectionDimension);
		posePanel.add(poseIcon);

		displayPanel.add(posePanel);
		displayPanel.add(devInfoPanel);

		informationPanel.add(controlPanel);
		informationPanel.add(displayPanel);

		this.setBackground(Color.BLACK);
		settingButton.setBackground(Color.white);
		this.addMouseListener(new ECGMouseListener());
		this.addMouseMotionListener(new ECGMouseMotionListener());
	}

	private JLabel addAlarmLabel(String string) {
		JLabel label = new JLabel(string, JLabel.CENTER);
		label.setForeground(Color.RED);
		label.setFont(new Font("楷体", Font.BOLD, 15));
		label.setBackground(informationColor);
		label.setEnabled(false);

		TitledBorder titledBorder = BorderFactory.createTitledBorder("");
		label.setBorder(titledBorder);

		return label;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * This function is to set a buffer time before plotting the curve
	 */
	private void bufferTime() {
		(new Thread() {
			public void run() {
				int secondsInt = 4;
				// do stuff
				try {
					Thread.sleep(1000 * secondsInt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buttonPause.enable();
			}
		}).start();
	}

	private void removeBorder(JButton button) {
		// TODO Auto-generated method stub
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		button.setContentAreaFilled(false);
	}

	/**
	 * stop drawing, this will also stop the data buffer associated with this
	 * component
	 */
	public void endDraw() {
		if (startFlag) {
			readDataTimer.stop();
			startFlag = false;
		}
	}

	/**
	 * suspend drawing, this will not stop data buffer, either display buffer.
	 * This is useful when data buffer throws some exceptions
	 */
	public void suspendDraw() {
		if (suspendFlag)
			return;
		suspendFlag = true;
	}

	public void freezeDraw() {
		if (freezeFlag)
			return;
		freezeFlag = true;
		// buttonPause.setText("继续");
		buttonPause.setIcon(SystemResources.playIcon);
		buttonPause.setToolTipText("继续");
	}

	/**
	 * resume drawing corresponding to suspend drawing.
	 */
	public void resumeDraw() {
		if (!suspendFlag && !freezeFlag)
			return;
		DebugTool.printLogDebug("resume!");
		startFlag = false;
		suspendFlag = false;
		freezeFlag = false;
		startDraw();
		buttonPause.setToolTipText("暂停");
		buttonPause.setIcon(SystemResources.pauseIcon);
	}

	/**
	 * XXX Start drawing
	 */
	public void startDraw() {
		if (!startFlag) {
			prepareCanvas(this.getWidth(), this.getHeight());
			indexDataBuffer = ((dataBuffer.getCurrentSecond() + delay) % dataBuffer
					.getSecondBuffer()) * dataBuffer.getFrequency();
			/*
			 * DebugTool.printLogDebug("start: dataBuffer.getCurrentSecond()"+
			 * dataBuffer .getCurrentSecond()+
			 * "delay"+delay+"dataBuffer.getSecondBuffer()"+
			 * dataBuffer.getSecondBuffer()+"dataBuffer.getFrequency()"
			 * +dataBuffer.getFrequency()+"indexBuffer"+indexDataBuffer);
			 */
			indexDrawPT = 0;
			updateStartT = System.currentTimeMillis();
			updateCurrentT = 0;
			currentPT = 0;
			startFlag = true;
			firstRound = true;
			readDataTimer.start();
		}
	}

	/**
	 * erase all data in buffer
	 * 
	 */

	public void eraseData() {
		for (int i = 0; i < NChannel; ++i) {
			for (int j = 0; j < maxTimeWindow * ptPerSecond; ++j) {
				peakFlag[j] = false;
				displayBufferList.get(i)[j] = SystemConstant.ECG_INVALID;
			}
		}
	}

	/**
	 * XXX Restart drawing
	 */
	public void restartDraw() {
		eraseData();
		prepareCanvas(this.getWidth(), this.getHeight());
		indexDataBuffer = ((dataBuffer.getCurrentSecond() + delay) % dataBuffer
				.getSecondBuffer()) * dataBuffer.getFrequency();
		indexDrawPT = 0;
		updateStartT = System.currentTimeMillis();
		updateCurrentT = 0;
		currentPT = 0;
		startFlag = true;
		firstRound = true;

		if (!readDataTimer.isRunning())
			readDataTimer.start();
	}

	// XXX Call this every time the frame changes
	private void prepareCanvas(int width, int height) {
		firstRound = true;
		indexDrawPT = 0;

		canvasWidth = width - widthInformationPanel - (borderSize * 2);
		canvasHeight = height - (borderSize * 2);

		heightPerChannel = (float) canvasHeight / (float) validSelectChanelNumb;
		for (int i = 0; i < validSelectChanelNumb; ++i) {
			yChannel[i] = (int) (i * heightPerChannel);
		}

		if (heightPerChannel < Style.DrawingPanelMinHeight) {
			heightPerChannel = Style.DrawingPanelMinHeight;
		}

		SettingParameters parameters = SettingParameters.getInstance();
		amplitudeScaling = mmPermV / parameters.getMmPerPixelVertical();// (float)(heightPerChannel)/(verticalRangePanel*2);
		float vPerPix = parameters.getMmperPixelHorizontal() / mmPermV;// (verticalRangePanel*2)/heightPerChannel;
		timeWindow = (float) (canvasWidth * vPerPix * v2hRatioPanel);
		maxIndexDrawPT = (int) (timeWindow * ptPerSecond);
		pixelRateF = ((float) canvasWidth / (float) maxIndexDrawPT);
		timeWindow -= 0.3f;
		maxIndexDrawPT = (int) (timeWindow * ptPerSecond);
		int startIndexDrawPT = (int) (ptPerSecond * pixelRateF * 6 / 25);
		for (int i = 0; i < maxIndexDrawPT && i < xIndex.length; ++i) {
			xIndex[i] = (int) (i * pixelRateF) + startIndexDrawPT;
		}

		mouseMovedProcess();
	}

	/**
	 * setter of the delay (second), which means how many seconds this drawing
	 * components delays from data buffer.
	 * 
	 * @param delayOut
	 */
	public void setDelay(int delayOut) {
		delay = delayOut;
	}

	boolean finishTTimesDrawECG = true; // 标记是否完成ecg的绘制工作

	/**
	 * paint function for this component
	 */
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2D = (Graphics2D) g;
		if (g == null)
			return;
		drawGrid(g2D, parameters.getBackgroundColor(), isDrawBackground);
		drawInformation(g2D, parameters.getBackgroundColor());
		g2D.setColor(parameters.getLineColor());

		if (startFlag || freezeFlag) {
			finishTTimesDrawECG = false;
			// erase previous two PT
			g2D.setColor(parameters.getLineColor());
			g2D.setStroke(new BasicStroke(parameters.getLineWidth()));
			int y1 = 0, y2 = 0;
			for (int indexC = 0; indexC < validSelectChanelNumb; ++indexC)
				for (int i = 1; i <= indexDrawPT; ++i) {
					if (selectDisplayBufferList.get(indexC)[i - 1] == SystemConstant.ECG_INVALID
							|| selectDisplayBufferList.get(indexC)[i] == SystemConstant.ECG_INVALID)
						continue;
					y1 = (int) (selectDisplayBufferList.get(indexC)[i - 1] * amplitudeScaling);
					y2 = (int) (selectDisplayBufferList.get(indexC)[i] * amplitudeScaling);
					y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
					y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);

					if (selectDisplayBufferList.get(indexC)[i - 1] == SystemConstant.ECGPEAK_MV) {
						// DebugTool.printLogDebug("converted peak value detected!");
						if (i == 1) {
							y1 = (int) (selectDisplayBufferList.get(indexC)[i] * amplitudeScaling);
						} else {
							y1 = (int) ((selectDisplayBufferList.get(indexC)[i - 2] + selectDisplayBufferList
									.get(indexC)[i]) / 2.0f * amplitudeScaling);
						}
						y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
						g2D.drawLine(xIndex[i - 1] + borderSize, y1, xIndex[i]
								+ borderSize, y2);
						// g2D.fillOval(xIndex[i-1]+borderSize, y1, 2, 2);
						int maxY = (int) (heightPerChannel / 4 + yChannel[indexC]);
						if (maxY < y1)
							maxY = y1;
					} else {
						g2D.drawLine(xIndex[i - 1] + borderSize, y1, xIndex[i]
								+ borderSize, y2);
					}
				}
			if (!firstRound) {
				int skipPT = (int) (0.15 * ptPerSecond);
				for (int indexC = 0; indexC < validSelectChanelNumb; ++indexC)
					for (int i = indexDrawPT + skipPT; i < maxIndexDrawPT - 1; ++i) {
						if (selectDisplayBufferList.get(indexC)[i] == SystemConstant.ECG_INVALID
								|| selectDisplayBufferList.get(indexC)[i + 1] == SystemConstant.ECG_INVALID)
							continue;
						y1 = (int) (selectDisplayBufferList.get(indexC)[i] * amplitudeScaling);
						y2 = (int) (selectDisplayBufferList.get(indexC)[i + 1] * amplitudeScaling);
						y1 = (int) (heightPerChannel / 2 - y1 + yChannel[indexC]);
						y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
						if (selectDisplayBufferList.get(indexC)[i + 1] == SystemConstant.ECGPEAK_MV) {
							System.out
									.println("converted peak value detected!");
							// g2D.setColor(Color.red);
							if ((i + 1) == maxIndexDrawPT - 1) {
								y2 = (int) (selectDisplayBufferList.get(indexC)[i + 1] * amplitudeScaling);
								selectDisplayBufferList.get(indexC)[i + 1] = selectDisplayBufferList
										.get(indexC)[i];
							} else {
								y2 = (int) ((selectDisplayBufferList
										.get(indexC)[i + 2] + selectDisplayBufferList
										.get(indexC)[i]) / 2.0f * amplitudeScaling);
							}
							y2 = (int) (heightPerChannel / 2 - y2 + yChannel[indexC]);
							// g2D.fillOval(xIndex[i]+borderSize, y1, 2, 2);
							int maxY = (int) (heightPerChannel / 4 + yChannel[indexC]);
							if (maxY < y2)
								maxY = y2;
							/*
							 * g2D.drawLine(xIndex[i+1]+borderSize,
							 * (int)(heightPerChannel/2+yChannel[indexC]),
							 * xIndex[i+1]+borderSize, maxY);
							 * g2D.drawString("Peak value!",
							 * xIndex[i+1]+borderSize, maxY);
							 * g2D.setColor(parameters.getLineColor());
							 */
						} else {
							g2D.drawLine(xIndex[i] + borderSize, y1,
									xIndex[i + 1] + borderSize, y2);
						}
					}
			}
			drawMouseM(g2D);
			finishTTimesDrawECG = true;
		}
	}

	/**
	 * 鼠标指向的波形电压
	 * 
	 * @param g
	 */
	private void drawMouseM(Graphics2D g) {
		if (mouseInECGArea) {
			if (mouseInChannel >= selectDisplayBufferList.size())
				return;
			float waveW = selectDisplayBufferList.get(mouseInChannel)[mouseXIndex];
			g.setColor(Color.YELLOW);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
			g.drawString(String.format("%.2f", waveW) + "(mv)", mouseX, mouseY);
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
		int heightTemp = canvasHeight;

		// lead Name
		for (int i = 0; i < selectChanelNameList.size() && i < yChannel.length; ++i) {
			g.drawString(selectChanelNameList.get(i), canvasWidth / 2 - 10,
					yChannel[i] + 20);
		}

		// seconds x-axis legend
		int fontSizeT = 10;
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.setColor(drawInformationColor);

		for (int i = 0; i < timeWindow; ++i) {
			g.drawString("" + i + "s", xIndex[i * ptPerSecond], heightTemp - 5);
		}

		// drawing the unit

		String strTemp = String.format("%.1f (mv) x %.2f (s)每格", scaleTemp,
				(scaleTemp * v2hRatioPanel));
		fontSizeT = 13;
		g.setColor(drawInformationColor);
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSizeT));
		g.drawString(strTemp, 15, 15);
	}

	/**
	 * draw background, e.g. for ECG we draw grid as background
	 * 
	 * @param g
	 *            graphics object for drawing
	 * @param c
	 *            color of background
	 */
	private void drawGrid(Graphics2D g, Color c, boolean isDrawBackground) {

		if (isDrawBackground) {
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

		// 显示每个通道的分割线
		for (int i = 0; i < yChannel.length; i++) {

			float zeroPos = heightPerChannel / 2 + yChannel[i];
			int pix = (int) (pixelRateF * ptPerSecond / 5);

			// 每个通道的分割线
			if (parameters.isShowChannelDivLine()) {
				if (i > 0) {
					g.setColor(parameters.getChannelDivLineColor());
					g.setStroke(new BasicStroke(2));
					g.drawLine(pix * 6 / 5 + borderSize, yChannel[i],
							canvasWidth, yChannel[i]);
				}
			}
			// 0mv线
			if (parameters.isShowOMVLine()) {
				g.setColor(parameters.getoMVLineColor());
				g.setStroke(new BasicStroke(1));
				g.drawLine(pix * 6 / 5 + borderSize, (int) zeroPos,
						canvasWidth, (int) zeroPos);
			}

			// draw the unit sample signal
			g.setFont(new Font("TimesRoman", Font.PLAIN, parameters
					.getFontSize()));
			g.setStroke(new BasicStroke(parameters.getLineWidth()));
			g.setColor(Color.RED);
			g.drawLine(borderSize, (int) zeroPos, pix / 5 + borderSize,
					(int) zeroPos);
			g.drawLine(pix / 5 + borderSize, (int) zeroPos, pix / 5
					+ borderSize, (int) (zeroPos - amplitudeScaling));
			g.drawLine(pix / 5 + borderSize,
					(int) (zeroPos - amplitudeScaling), pix * 4 / 5
							+ borderSize, (int) (zeroPos - amplitudeScaling));
			g.drawLine(pix * 4 / 5 + borderSize,
					(int) (zeroPos - amplitudeScaling), pix * 4 / 5
							+ borderSize, (int) (zeroPos));
			g.drawLine(pix * 4 / 5 + borderSize, (int) (zeroPos), pix
					+ borderSize + borderSize, (int) (zeroPos));
		}
	}

	private void popupLogin() throws IOException {
		mainFrame.login();
	}

	private void resumeInfoPanel() {
		String strTemp = "--";
		bpmTemp = 72; // when exception detected, no need to alarm
		labelBeat.setText(strTemp);

		highAlarmLabel.clear();
		mediumAlarmLabel.clear();
		lowAlarmLabel.clear();
		alarmList.clear();

		updateAlarmSoundLevel();
	}

	public void updateInfoPanel() {
		if (!startFlag || (dataBuffer.isExceptionFlag())) {
			resumeInfoPanel();
			return;
		}

		alarmList.clear();
		updateBPM(); // 心率
		updateMotionCode(); //
		updateRssi(); // 无线信号
		updateElectrodeHeader(); // 电极
		updateBattery(); // 电量
		updatePressure(); // 偏压
		updateCallTime();
		updateAlarmSoundLevel();
		updateWarningMessage();
	}

	public void updateWarningMessage() {
		String warningMessage = "";

		// 禁止生理报警
		if (cancelFlag)
			warningMessage = "生理报警已全部禁止";
		else if (tempCancelFlag > 0) {
			if (alertManager.getCountDownCancel() <= 0) {
				tempCancelFlag = 0;
				tempCancelButton
						.setIcon(SystemResources.tempCancelIcon[tempCancelFlag]);
				tempMuteButton.setToolTipText("暂时禁止报警");
			} else {
				warningMessage = "禁止生理报警：" + alertManager.getCountDownCancel()
						+ "秒";
			}
		} else {
			if (!soundOn)
				warningMessage = "完全静音";
			// 暂时关闭报警声音已经结束
			if (tempMuteFlag > 0 && alertManager.getCountDownMute() <= 0) {
				tempMuteFlag = 0;
				tempMuteButton.setIcon(SystemResources.tempMuteIcon[0]);
				tempMuteButton.setToolTipText("暂时静音");
			}
			// 暂时关闭报警声音还在继续
			if (tempMuteFlag > 0)
				warningMessage = "暂时静音：" + alertManager.getCountDownMute()
						+ "秒";
		}
		if (warningMessage.length() == 0) {
			labelWarningMessage.setEnabled(false);
			labelWarningMessage.setText("无警告");
		} else {
			labelWarningMessage.setEnabled(true);
			labelWarningMessage.setText(warningMessage);
		}
	}

	private void updatePressure() {
		int pressure = dataBuffer.getFidelity();
		if (pressure > 0) {
			// physical alarm not affected by
			if (!mediumAlarmLabel.contains(waveformDistortionAlarmLabel)) {
				waveformDistortionAlarmLabel
						.setForeground(SystemConstant.MEDIUMALARMCOLOR);
				waveformDistortionAlarmLabel.setText("偏压过高波形失真");
				waveformDistortionAlarmLabel.setEnabled(true);
				mediumAlarmLabel.add(waveformDistortionAlarmLabel);

			}
			alarmList.add(alertManager.getAlertLevel());
		} else {
			if (mediumAlarmLabel.contains(waveformDistortionAlarmLabel)) {
				mediumAlarmLabel.remove(waveformDistortionAlarmLabel);
				waveformDistortionAlarmLabel.setVisible(true);
				waveformDistortionAlarmLabel.setEnabled(false);
			}
		}
	}

	public void updateAlarmSoundLevel() {
		int minAlarmLevel = 4;
		for (Integer integer : alarmList) {
			// if mute then all alarm level = 1, or 2 will be mute
			if (integer.intValue() < minAlarmLevel)
				minAlarmLevel = integer.intValue();
		}
		// 全部静音，或者禁止报警，则没有声音输出
		if (!soundOn || cancelFlag || tempMuteFlag > 0 || tempCancelFlag > 0
				|| minAlarmLevel == 4)
			minAlarmLevel = 4;
		level = minAlarmLevel;
	}

	private void updateBPM() {
		int heartRateTemp = dataBuffer.getBPM();
		if (heartRateTemp != 0) {
			bpmTemp = heartRateTemp;
		}
		String strTemp = "";
		strTemp = String.format("%d", dataBuffer.getBPM());

		if (bpmTemp == SystemConstant.EXCEPTIONAL_HEARTRATE
				|| bpmTemp == SystemConstant.BPM_INVALIDVALUE) {
			strTemp = "--";
			bpmTemp = 72; // when exception detected, no need to alarm
		}

		// 心率报警
		labelBeat.setText(strTemp);
		if (bpmTemp < parameters.getHrMin() && parameters.isBpmAlarmOn()
				&& !cancelFlag && tempCancelFlag == 0) {
			bmpAlarmLabel.setText("心率过低");
			bmpAlarmLabel.setEnabled(true);
			if (!highAlarmLabel.contains(bmpAlarmLabel)) {
				bmpAlarmLabel.setForeground(SystemConstant.HIGHALARMCOLOR);
				highAlarmLabel.add(bmpAlarmLabel);
			}
			if (!highAlarmLabel.contains(labelBeat)) {
				highAlarmLabel.add(labelBeat);
			}

			alarmList.add(alertManager.getAlertLevel());
			return;
		}
		if (bpmTemp > parameters.getHrMax() && parameters.isBpmAlarmOn()
				&& !cancelFlag && tempCancelFlag == 0) {
			bmpAlarmLabel.setText("心率过高");
			bmpAlarmLabel.setEnabled(true);
			if (!highAlarmLabel.contains(bmpAlarmLabel)) {
				bmpAlarmLabel.setForeground(SystemConstant.HIGHALARMCOLOR);
				highAlarmLabel.add(bmpAlarmLabel);
			}
			if (!highAlarmLabel.contains(labelBeat)) {
				highAlarmLabel.add(labelBeat);
			}
			alarmList.add(alertManager.getAlertLevel());
			return;
		}

		// 不需要心率报警
		labelBeat.setVisible(true);
		bmpAlarmLabel.setVisible(true);
		bmpAlarmLabel.setEnabled(false);
		bmpAlarmLabel.setText("心率正常");
		if (highAlarmLabel.contains(bmpAlarmLabel)
				&& (bpmTemp <= parameters.getHrMax() && bpmTemp >= parameters
						.getHrMin())) {
			highAlarmLabel.remove(bmpAlarmLabel);
		}
		if (highAlarmLabel.contains(labelBeat)
				&& bpmTemp <= parameters.getHrMax()
				&& bpmTemp >= parameters.getHrMin()) {
			highAlarmLabel.remove(labelBeat);
		}
		// 暂时取消报警
		if (!parameters.isBpmAlarmOn() || cancelFlag || (tempCancelFlag != 0)) {
			if (highAlarmLabel.contains(bmpAlarmLabel))
				highAlarmLabel.remove(bmpAlarmLabel);
			if (highAlarmLabel.contains(labelBeat))
				highAlarmLabel.remove(labelBeat);
		}
	}

	private void updateCallTime() {
		if (dataBuffer.checkAlarm() && !callFromPatient) {
			callFromPatient = true;
			// startAlarm();

			SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm");

			final JOptionPane optionPane = new JOptionPane(
					sim.format(dataBuffer.getCallTime()) + "病人"
							+ dataBuffer.getPatient().getPatientID()
							+ dataBuffer.getPatient().getPatientName() + "呼叫!",
					JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);

			final JDialog dialog = new JDialog(mainFrame, "病人呼叫", true);
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					System.out
							.println("Thwarted user attempt to close window.");
				}
			});
			optionPane.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					String prop = e.getPropertyName();

					if (dialog.isVisible() && (e.getSource() == optionPane)
							&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
						// If you were going to check something
						// before closing the window, you'd do
						// it here.
						dialog.setVisible(false);
					}
				}
			});
			Rectangle main = mainFrame.getBounds();
			Rectangle bounds = this.getBounds();
			dialog.setBounds(main.x + bounds.x + bounds.width / 2 - 50, main.y
					+ bounds.y + bounds.height / 2 - 40, 100, 80);
			dialog.pack();
			dialog.setVisible(true);

			int value = ((Integer) optionPane.getValue()).intValue();
			if (value == JOptionPane.OK_OPTION) {
				callFromPatient = false;
				// endAlarm();
				DebugTool.printLogDebug("Good.");
			}

			dataBuffer.syncCallTime();
		}
	}

	private void updateElectrodeHeader() {
		// DebugTool.printLogDebug("header: "+header);
		int header = dataBuffer.getElectrodeHeader();
		int j = 1;
		boolean headerDetached = false;
		if (model == 1) {
			for (int i = 0; i < 10; i++) {
				if ((header & j) != 0) {
					leadLabels.get(i).setIcon(
							SystemResources.leadIconList[i * 2 + 1]);
					headerDetached = true;
				} else {
					leadLabels.get(i).setIcon(
							SystemResources.leadIconList[i * 2]);
				}
				j = j << 1;
				leadLabels.get(i).revalidate();
				leadLabels.get(i).repaint();
			}
		} else {
			for (int i = 0; i < 10; i++) {
				if ((header & j) != 0 && i != 4 && i != 5 && i != 6 && i != 7
						&& i != 9) {
					leadLabels.get(i).setIcon(
							SystemResources.leadIconList[i * 2 + 1]);
					headerDetached = true;
				} else {
					leadLabels.get(i).setIcon(
							SystemResources.leadIconList[i * 2]);
				}
				j = j << 1;
				leadLabels.get(i).revalidate();
				leadLabels.get(i).repaint();
			}
		}
		if (headerDetached) {
			// physical alarm not affected by
			if (!mediumAlarmLabel.contains(headerAlarmLabel)) {
				headerAlarmLabel.setForeground(SystemConstant.MEDIUMALARMCOLOR);
				headerAlarmLabel.setEnabled(true);
				mediumAlarmLabel.add(headerAlarmLabel);
			} else
				DebugTool
						.printLogDebug("medium alarm label contains header lost");
			alarmList.add(alertManager.getAlertLevel());
		} else {
			if (mediumAlarmLabel.contains(headerAlarmLabel)) {
				mediumAlarmLabel.remove(headerAlarmLabel);
			}
			headerAlarmLabel.setVisible(true);
			headerAlarmLabel.setEnabled(false);
		}
	}

	private void updateBattery() {
		int battery = dataBuffer.getBattery();
		boolean batteryLow = false;
		if (battery < parameters.getBatteryAlarmBoundary())
			batteryLow = true;
		float bin = (float) (battery / 100.0);
		int level = (int) (bin * 17);
		// DebugTool.printLogDebug("battery:"+battery);
		/*
		 * this.batteryLabel.setIcon(new ImageIcon( (new
		 * ImageIcon("res/battery/BatteryBG_"+level+".png")).getImage()
		 * .getScaledInstance(30,15,java.awt.Image.SCALE_SMOOTH)));
		 */
		if (level > 17)
			level = 17;
		if (level <= 0)
			level = 1;
		this.batteryLabel.setIcon(SystemResources.batteryIconList[level - 1]);
		batteryLabel.setToolTipText("当前电量" + battery + "%");
		informationPanel.revalidate();
		informationPanel.repaint();
		if (batteryLow) {
			if (!lowAlarmLabel.contains(batteryAlarmLabel)) {
				batteryAlarmLabel.setText("电量不足");
				batteryAlarmLabel.setEnabled(true);
				batteryAlarmLabel.setForeground(SystemConstant.LOWALARMCOLOR);
				lowAlarmLabel.add(batteryAlarmLabel);
			}
			alarmList.add(alertManager.getAlertLevel());

		} else {
			if (lowAlarmLabel.contains(batteryAlarmLabel)) {
				lowAlarmLabel.remove(batteryAlarmLabel);
			}
			batteryAlarmLabel.setVisible(true);
			batteryAlarmLabel.setEnabled(false);
		}
	}

	private void updateRssi() {
		// strength of signal: [0,4]
		int rs = dataBuffer.getWiFiSignalStrength();
		rssiLabel.setToolTipText("当前无线信号强度" + rs + "dB");
		if (rs <= parameters.getWifiAlarmBoundary()) {
			if (!lowAlarmLabel.contains(wifiAlarmLabel)) {
				wifiAlarmLabel.setText("无线信号弱");
				wifiAlarmLabel.setEnabled(true);
				wifiAlarmLabel.setForeground(SystemConstant.LOWALARMCOLOR);
				lowAlarmLabel.add(wifiAlarmLabel);
			}
			alarmList.add(alertManager.getAlertLevel());

		} else {
			if (lowAlarmLabel.contains(wifiAlarmLabel)) {
				lowAlarmLabel.remove(wifiAlarmLabel);
			}
			wifiAlarmLabel.setVisible(true);
			wifiAlarmLabel.setEnabled(false);
		}
		if (rs < 0)
			rs = 0;
		if (rs > 4)
			rs = 4;
		rssiLabel.setIcon(SystemResources.wifiIconList[rs]);
		rssiLabel.revalidate();
	}

	public void highAlarmShining(boolean showFlag) {
		if (showFlag) {
			highAlarmShow.setAlertManager(alertManager);
			highAlarmShow.setAlarmLabel(highAlarmLabel);
			highAlarmShow.start();
		} else {
			highAlarmShow.stop();
		}
	}

	public void mediumAlarmShining(boolean showFlag) {
		if (showFlag) {
			mediumAlarmShow.setAlertManager(alertManager);
			mediumAlarmShow.setAlarmLabel(mediumAlarmLabel);
			mediumAlarmShow.start();
		} else {
			mediumAlarmShow.stop();
		}
	}

	public void lowAlarmShining(boolean showFlag) {
		if (showFlag) {
			lowAlarmShow.setAlertManager(alertManager);
			lowAlarmShow.setAlarmLabel(lowAlarmLabel);
			lowAlarmShow.start();
		} else {
			lowAlarmShow.stop();
		}
	}

	public void updateMotionCode() {
		if (dataBuffer.getMotionCode() != 0)
			motionCode = dataBuffer.getMotionCode();
		// change icon for posePanel
		ImageIcon imageT;
		if ((motionCode & SystemConstant.MOTION_MASK) > 0) {// motion detected
			imageT = SystemResources.imageIconStandMotion;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.DOWNWARD)
				imageT = SystemResources.imageIconFaceDownMotion;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.UPWARD)
				imageT = SystemResources.imageIconFaceUpMotion;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.LEFTWARD)
				imageT = SystemResources.imageIconFaceLeftMotion;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.RIGHTWARD)
				imageT = SystemResources.imageIconFaceRightMotion;
		} else {// no motion detected
			imageT = SystemResources.imageIconStand;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.DOWNWARD)
				imageT = SystemResources.imageIconFaceDown;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.UPWARD)
				imageT = SystemResources.imageIconFaceUp;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.LEFTWARD)
				imageT = SystemResources.imageIconFaceLeft;
			if ((motionCode & SystemConstant.ORIENTATION_MASK) == SystemConstant.RIGHTWARD)
				imageT = SystemResources.imageIconFaceRight;
		}
		if (imageT != null) {
			poseIcon.setIcon(imageT);
			informationPanel.revalidate();
			informationPanel.repaint();
		}
	}

	// / ************* Action listeners private classes or methods
	// **************
	@Override
	public void itemStateChanged(ItemEvent e) {

	}

	/**
	 * This class derived from ActionListener specifies how to update display
	 * buffer from data buffer. Only need to care about override function
	 * actionPerformed
	 * 
	 * @author jfeng
	 * 
	 */
	private class ReadDatabufferTask implements ActionListener {
		public ReadDatabufferTask() {
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (dataBuffer.isExceptionFlag()) {
				// some exceptions come up, so suspend drawing
				// if alarm is on, turn off it and stop flashing
				alarmOn = false;
				if (alarmTimer != null) {
					alarmTimer.stop();
				}
				labelWarningMessage.setText("网络存在问题");
				labelWarningMessage.setEnabled(true);
				return;
			} else {
				// 如果网络没有问题，消除警报信息，策略需要改进
				if (labelWarningMessage.getText().compareTo("网络存在问题") == 0) {
					labelWarningMessage.setText("无警告");
					labelWarningMessage.setEnabled(false);
				}
			}
			if (freezeFlag)
				return;

			int updatePT = 0;
			updateCurrentT = System.currentTimeMillis();
			updatePT = (int) ((updateCurrentT - updateStartT) * ptPerSecond
					/ 1000 - currentPT);
			int updatePTBuffer = (int) ((float) dataBuffer
					.getAmountPoint(indexDataBuffer) / bufferRate);
			// DebugTool.printLogDebug("update "+updatePT + ":" +
			// updatePTBuffer);
			if (updatePT > updatePTBuffer)
				updatePT = updatePTBuffer;

			/*
			 * if(updatePT%1000==0)
			 * DebugTool.printLogDebug("Drawing : "+((currentPT+updatePT
			 * )*1000/(updateCurrentT - updateStartT))+" pt per sec");
			 */
			currentPT += updatePT;

			// System.out.println(timeWindow * ptPerSecond);
			int tempI = 0;
			float tempF = 0.0f;
			int shift = (int) bufferRate;
			if (shift <= 0)
				shift = 1;
			for (int i = 0; i < NChannel; ++i) {
				for (int j = 1; j <= updatePT; ++j) {
					tempI = dataBuffer.getLocalMeanChannel(
							(int) (indexDataBuffer + (j * bufferRate)), shift,
							i);
					// if only one channel, then no boundary.
					if (NChannel > 1)
						tempF = SystemConstant.ECGConvertor(tempI);
					else
						tempF = SystemConstant.ECGConvertorWOBoundary(tempI);
					if (i == 0 && tempF == SystemConstant.ECGPEAK_MV) {
						DebugTool.printLogDebug("peak!!");
						peakFlag[(indexDrawPT + j) % maxIndexDrawPT] = true;
					}
					if (i == 0 && tempF != SystemConstant.ECGPEAK_MV) {
						peakFlag[(indexDrawPT + j) % maxIndexDrawPT] = false;
					}
					// if(j%10==0)
					// DebugTool.printLogDebug("display:"+tempI+" -> "+tempF);

					displayBufferList.get(i)[(indexDrawPT + j) % maxIndexDrawPT] = tempF;
				}
			}
			boolean checkJump = false;
			indexDataBuffer += (bufferRate * updatePT);
			indexDrawPT += updatePT;

			if (indexDrawPT >= maxIndexDrawPT) {
				firstRound = false;
				// jump back for safety
				checkJump = true;
			}
			indexDataBuffer = indexDataBuffer
					% (dataBuffer.getFrequency() * dataBuffer.getSecondBuffer());
			indexDrawPT = indexDrawPT % maxIndexDrawPT;
			// DebugTool.printLogDebug("indexDrawPT "+indexDrawPT+"indexBuffer:"+indexBuffer+"currentPT"+dataBuffer.getCurrentPT());
			// in the end of each screen, cheating will be conducted to adjust
			// the reading index
			if (checkJump) {
				int maxPTDB = dataBuffer.getSecondBuffer()
						* dataBuffer.getFrequency();
				int diffIndex = dataBuffer.getCurrentPT() - indexDataBuffer
						+ maxPTDB;
				// output difference between writing and reading index
				// DebugTool.printLogDebug("difference of index: "+(diffIndex%maxPTDB));
				// in case of reading index goes to close or even goes over the
				// writing index
				if ((diffIndex % maxPTDB) > (maxPTDB * 2 / 3)
						|| (diffIndex % maxPTDB) < (WARNINGTH * dataBuffer
								.getFrequency())) {
					DebugTool.printLogDebug("~~~I am jumping backward ~~~"
							+ "indexBuffer" + indexDataBuffer + "currentPT"
							+ dataBuffer.getCurrentPT());
					// indexBuffer+=(dataBuffer.getFrequency()*delay);
					// indexBuffer =
					// indexBuffer%(dataBuffer.getFrequency()*dataBuffer.getSecondBuffer());
					indexDataBuffer = ((dataBuffer.getCurrentSecond() + delay) % dataBuffer
							.getSecondBuffer()) * dataBuffer.getFrequency();
					DebugTool.printLogDebug("indexBuffer" + indexDataBuffer
							+ "currentPT" + dataBuffer.getCurrentPT());
				}
				// in case of reading index lag behind the writing index
				if ((diffIndex % maxPTDB) < (maxPTDB * 2 / 3)
						&& (diffIndex % maxPTDB) > (-(WARNINGTHLAG + delay) * dataBuffer
								.getFrequency())) {
					DebugTool.printLogDebug("~~~I am jumping forward ~~~"
							+ "indexBuffer" + indexDataBuffer + "currentPT"
							+ dataBuffer.getCurrentPT());
					// indexBuffer+=(dataBuffer.getFrequency()*delay);
					indexDataBuffer = ((dataBuffer.getCurrentSecond() + delay) % dataBuffer
							.getSecondBuffer()) * dataBuffer.getFrequency();
					DebugTool.printLogDebug("indexBuffer" + indexDataBuffer
							+ "currentPT" + dataBuffer.getCurrentPT());
				}
			}
		}
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
			widthInformationPanel = 250;
			// informationPanel.setBounds((getWidth()-165),0,165,getHeight());
			informationPanel.setBounds(
					(getWidth() - widthInformationPanel - borderSize),
					borderSize, widthInformationPanel, getHeight() - 2
							* borderSize);

			int startX = 5;
			int startY = 20;
			startY += 50;
			startY += intervalI;
			startY += 15;
			startY += intervalI;
			startY += 15;
			startY += intervalI;
			startY += 20;
			startY += intervalI;
			startX = 5;
			startY += 15;
			startY += intervalI;
			startY += 23;
			startY += intervalI;
			startY += 23;
			startY += intervalI;
			startY += 23;
			startY += intervalI;
			prepareCanvas(DrawingPanel.this.getWidth(),
					DrawingPanel.this.getHeight());
			DrawingPanel.this.repaint();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
		}

	}

	/**
	 * This actionPerformed function is to responds to max/minimize this
	 * component in main frame window
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == settingButton) {
			return;
		} else if (e.getSource() == buttonPause) {
			DebugTool.printLogDebug("pause button clicked!!");
			if (freezeFlag) {
				DebugTool.printLogDebug("to resume~");
				this.resumeDraw();
				buttonPause.setToolTipText("暂停");
				buttonPause.setIcon(SystemResources.pauseIcon);
			} else {
				DebugTool.printLogDebug("to pause~");
				this.freezeDraw();
				buttonPause.setToolTipText("继续");
				buttonPause.setIcon(SystemResources.playIcon);
			}
			repaint();
			return;
		} else if (e.getSource() == buttonGrid) {
			if (isDrawBackground)
				isDrawBackground = false;
			else
				isDrawBackground = true;

			repaint();
			return;
		} else if (e.getSource() == buttonHistory) {
			openHistoryFrame();
		} else if (e.getSource() == buttonPrint) {
			File directory = new File("");// 设定为当前文件夹
			File file = new File("temp");
			if (!file.exists()) {
				file.mkdir();
			}
			JFileChooser saveFile = new SaveFileChooser(".");
			String tempSavePath = directory.getAbsolutePath() + "/temp";
			PrintPatientInfor printPatientInfor1 = new PrintPatientInfor(
					displayBufferList, verticalRangePanel, v2hRatioPanel,
					dataBuffer, parameters, tempSavePath + "/temp1.pdf",
					indexDrawPT, timeWindow, firstRound);
			PrintPatientInfor printPatientInfor2 = new PrintPatientInfor(
					displayBufferList, verticalRangePanel, v2hRatioPanel,
					dataBuffer, parameters, tempSavePath + "/temp2.pdf",
					indexDrawPT, timeWindow, firstRound);
			printPatientInfor1.SavePdf(true);
			printPatientInfor2.SavePdf(false);
			try {
				PrintPreviewDialog printPrevieDialog = new PrintPreviewDialog(
						saveFile, tempSavePath, displayBufferList,
						verticalRangePanel, v2hRatioPanel, dataBuffer,
						parameters, indexDrawPT, timeWindow, firstRound);
				printPrevieDialog.setVisible(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == buttonSound) {
			if (soundOn) {
				soundOn = false;
				buttonSound.setIcon(SystemResources.muteIcon);
				buttonSound.setToolTipText("音量关");
			} else {
				soundOn = true;
				buttonSound.setIcon(SystemResources.soundIcon);
				buttonSound.setToolTipText("音量开");
			}
			repaint();
			return;
		} else if (e.getSource() == tempMuteButton) {
			tempMuteFlag = (tempMuteFlag + 1) % 3;
			alertManager.tempMute(tempMuteFlag * 60000);
			if (tempMuteFlag == 0) {
				// 静音失效
				if (!soundOn) {
					soundOn = true;
					buttonSound.setIcon(SystemResources.soundIcon);
					buttonSound.setToolTipText("音量开");
				}
				tempMuteButton.setIcon(SystemResources.tempMuteIcon[0]);
				tempMuteButton.setToolTipText("暂时静音");
			} else {
				tempMuteButton
						.setIcon(SystemResources.tempMuteIcon[tempMuteFlag]);
				tempMuteButton.setToolTipText("暂时静音" + tempMuteFlag + "分钟");
			}
		} else if (e.getSource() == cancelAllAlarmButton) {
			// 禁止所有生理报警，以及关闭技术报警的声音
			if (cancelFlag) {
				cancelFlag = false;
				cancelAllAlarmButton.setIcon(SystemResources.notCancelIcon);
				cancelAllAlarmButton.setToolTipText("禁止全部生理报警");
			} else {
				cancelFlag = true;
				cancelAllAlarmButton.setIcon(SystemResources.cancelIcon);
				highAlarmLabel.clear();
				mediumAlarmLabel.clear();
				lowAlarmLabel.clear();
				cancelAllAlarmButton.setToolTipText("恢复全部生理报警");
			}
		} else if (e.getSource() == tempCancelButton) {
			tempCancelFlag = (tempCancelFlag + 1) % 3;
			alertManager.tempBanAlert(alert, tempCancelFlag * 60000);
			if (tempCancelFlag == 0) {
				// turnOffTempCancel();
				tempCancelButton.setIcon(SystemResources.tempCancelIcon[0]);
				if (cancelFlag) {
					cancelFlag = false;
					cancelAllAlarmButton.setIcon(SystemResources.notCancelIcon);
					cancelAllAlarmButton.setToolTipText("禁止全部生理报警");
				}
			} else {
				// turnOnTempCancel();
				tempCancelButton
						.setIcon(SystemResources.tempCancelIcon[tempCancelFlag]);
				tempCancelButton.setToolTipText("暂时禁止报警" + tempCancelFlag
						+ "分钟");
			}

		} else if (e.getSource() == leadSelectButton) {
			ChoseLeadDialog choseLeadDialog = new ChoseLeadDialog(mainFrame,
					DrawingPanel.this, patient);
			boolean selectChannelFlag2[] = {true, true, true, true, true, true,
					true, true, true, true, true, true};
			choseLeadDialog.setSelectChannelFlag(selectChannelFlag2);
		}
	}
	/*
	 * JFileChooser class and renew approveSelection funtion
	 */
	class SaveFileChooser extends JFileChooser {
		public SaveFileChooser() {
			this.addChoosableFileFilter(new PdfFileFilter("pdf"));
		}

		public SaveFileChooser(String path) {
			super(path);
			this.addChoosableFileFilter(new PdfFileFilter("pdf"));
		}

		public void approveSelection() {
			File file = this.getSelectedFile();
			if (file.exists()) {
				int copy = JOptionPane.showConfirmDialog(null, "是否要覆盖当前文件？",
						"保存", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (copy == JOptionPane.YES_OPTION)
					super.approveSelection();
				else
					return;
			}
			super.approveSelection();
		}

		/*
		 * FileFilter
		 */
		private class PdfFileFilter extends FileFilter {

			String ext;

			public PdfFileFilter(String ext) {
				this.ext = ext;
			}

			public boolean accept(File file) {
				if (file.isDirectory())
					return true;

				String fileName = file.getName();
				int atPointPos = fileName.indexOf('.');

				if (atPointPos > 0 && atPointPos < fileName.length() - 1) {
					String getExt = fileName.substring(atPointPos + 1);
					if (getExt.equals(ext))
						return true;
				}
				return false;
			}

			public String getDescription() {
				return "PDF 文件(*.pdf)";
			}
		}
	}

	private void openHistoryFrame() {
		HistoryFrame histFrame = new HistoryFrame("历史数据查询");
		if (this.dataBuffer.getPatient() != null)
			histFrame.setPatientSelected(this.dataBuffer.getPatient());
		histFrame.setConnectStr(parameters.getIpString());
	}

	public void openHeartFrame() {
		/*
		 * DebugTool.printLogDebug("before");
		 * JOptionPane.showMessageDialog(null, "alert", "alert",
		 * JOptionPane.ERROR_MESSAGE); DebugTool.printLogDebug("after");
		 */
		HeartRateFrame heartRateFrame = new HeartRateFrame("心跳历史数据查询",
				parameters.getIpString());
		if (this.dataBuffer.getPatient() != null)
			heartRateFrame.setPatientSelected(this.dataBuffer.getPatient());
	}

	/**
	 * This private class derived from MouseListener defines click on drawing
	 * component will start drawing
	 * 
	 * @author jfeng
	 * 
	 */
	private class ECGMouseListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// DoctorCommentDialog doctorCommentDialog = new
			// DoctorCommentDialog(mainFrame);
			// doctorCommentDialog.setVisible(true);
		}
	}

	// 鼠标移动显示波形电压
	private boolean mouseInECGArea;
	private int mouseXIndex;
	private int mouseInChannel;
	private int mouseX, mouseY;

	/**
	 * 鼠标移动处理
	 */
	private void mouseMovedProcess() {
		if (mouseX < (DrawingPanel.this.getWidth() - widthInformationPanel)) {
			if (startFlag || freezeFlag) {
				// 具体哪个点
				boolean mouseIsFind = false;
				for (int i = 0; i < maxIndexDrawPT && i < xIndex.length; ++i) {
					if (mouseX == xIndex[i]) {
						mouseXIndex = i;
						mouseIsFind = true;
					}
				}
				if (mouseIsFind) {
					mouseInECGArea = true;
					mouseIsFind = false;
				} else
					return;
				// 具体在哪个通道
				for (int i = 0; i < yChannel.length - 1; i++) {
					if (mouseY > yChannel[i] && mouseY < yChannel[i + 1]) {
						mouseInChannel = i;
						mouseIsFind = true;
						break;
					}
				}
				if (!mouseIsFind) {
					mouseInChannel = yChannel.length - 1;
				}
			}
		} else {
			mouseInECGArea = false;
		}
	}

	private class ECGMouseMotionListener implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			mouseMovedProcess();
		}
	}

	private class ComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(boxVerticalRange)) {
				int indexTemp = boxVerticalRange.getSelectedIndex();
				if (indexTemp >= 0 && indexTemp < verticalScaleOptions.length) {
					mmPermV = verticalScaleOptions[indexTemp];
				} else {
					mmPermV = verticalScaleOptions[0];
				}
				if (boxV2hRatio != null)
					v2hRatioPanel = mmPermV
							/ v2hRatioOptions[boxV2hRatio.getSelectedIndex()];
			}

			if (arg0.getSource().equals(boxV2hRatio)) {
				v2hRatioPanel = mmPermV
						/ v2hRatioOptions[boxV2hRatio.getSelectedIndex()];
			}
			if (arg0.getSource().equals(boxMode)) {
				if (dataBuffer.getPatient().getPatientID() == -1
						|| initializeFlag) {
					return;
				}
				final int selectedIndex = boxMode.getSelectedIndex();
				if (!initializeFlag) {
					eraseData();
					DrawingPanel.this.freezeDraw();
					buttonPause.setEnabled(false);

					waitingTimeDialog = new WaitingTimeDialog(mainFrame);
					waitingTimeDialog.getTipLabel().setText("模式设置中,请等待");
					recordNumb = -1;
					waitTimer = new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (recordNumb >= 0) {
								recordNumb--;
								if (recordNumb == 0) {
									waitTimer.stop();
									waitingTimeDialog.close();
									waitingTimeDialog = null;
									buttonPause.setEnabled(true);

									resumeDraw();
									buttonPause.setToolTipText("暂停");
									buttonPause
											.setIcon(SystemResources.pauseIcon);
									DrawingPanel.this.repaint();
								} else {
									String tip = "模式设置完成，缓存数据中 " + recordNumb
											+ " s";
									waitingTimeDialog.getTipLabel()
											.setText(tip);
								}
							}
						}
					});
					waitTimer.start();

					new Thread(new Runnable() {

						@Override
						public void run() {
							dataBuffer
									.setDeviceMode(modeOptions[selectedIndex]);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									ptPerSecond = dataBuffer.getFrequency();
									prepareCanvas(DrawingPanel.this.getWidth(),
											DrawingPanel.this.getHeight());
									DrawingPanel.this.repaint();
								}
							});
							recordNumb = 10;
						}
					}).start();
				}
			}
			prepareCanvas(DrawingPanel.this.getWidth(),
					DrawingPanel.this.getHeight());
			DrawingPanel.this.repaint();
		}
	}

	private int recordNumb;
	private Timer waitTimer;
	private WaitingTimeDialog waitingTimeDialog;

	// / ************* setters and getters **************
	/**
	 * getter of frequency of this drawing panel
	 * 
	 * @return the frequency of this drawing panel: how many points per second
	 *         when drawing curves
	 */
	public int getPTPersecond() {
		return ptPerSecond;
	}

	/**
	 * setter of frequency
	 * 
	 * @param frequency
	 */
	public void setPTPerSecond(int frequency) {
		this.ptPerSecond = frequency;
	}

	/**
	 * setter of parameters
	 * 
	 * @param frequency
	 */
	public SettingParameters getParameters() {
		return parameters;
	}

	/**
	 * getter of parameters
	 * 
	 * @param frequency
	 */
	public void setParameters(SettingParameters parameters) {
		this.parameters = parameters;
		prepareCanvas(this.getWidth(), this.getHeight());
	}

	/**
	 * getter of data buffer
	 * 
	 * @return data buffer
	 */
	public DataBufferInterface getDataBufferT() {
		return dataBuffer;
	}
}
