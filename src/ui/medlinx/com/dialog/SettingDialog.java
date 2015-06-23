package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.extra.Style;
import ui.medlinx.com.frame.Main.MLnxClient;

public class SettingDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	JPanel jpRadioButtons, jpLineColor, jpScale, jpGid; // each panel for a
														// certain group of
														// setting
	JRadioButton jrbOrange, jrbGreen, jrbGray; // radio buttons for select
	private JCheckBox showOMVLineCheckBox, showChannelDivLineCheckBox;
	JRadioButton jrbLineRed, jrbLineGreen, jrbLineBlue; // select line color of
														// the ECG curve
	JRadioButton jrbExistGid, jrbNotExistGid;
	JComboBox jcbPaperSize;
	JLabel backgroundLabel, timeWindowLabel, lineColorLabel, lblLineWidth,
			lblFontSize, scaleLabel; // some text labels
	JLabel ipLabel, portLabel, hrMaxLabel, hrMinLabel, boMaxLabel, boMinLabel,
			wirelessMinLabel, electricMinLabel;
	JTextField ipTextField, portTextField;// to set the IP and port
	JPanel panelDisplay, panelConnection, panelWarning, panelPrint;// each panel
																	// for a
																	// certain
																	// group of
																	// setting
	private MLnxClient mainFrame; // the main frame window
	JButton confirmButton, cancelButton; // two buttons "OK" and "cancel"
	Container container; // to make a tab UI
	JSlider sliderLine, sliderFont, sliderHRMin, sliderHRMax, sliderBOMin,
			sliderBOMax, sliderWirelessMin, sliderElectricMin; // sliders for
																// setting
																// continues
																// fields
	JCheckBox heartAlarmBox;
	private SettingParameters parameters; // display parameters data structure
	private Color bgColor, fgColor; // background and foreground color for this
									// dialog window.

	private String[] strPaperSize = { "A0纸", "A1纸", "A2纸", "A3纸", "A4纸",
			"ECG标准打印纸" };

	public SettingDialog(SettingParameters _parameters, MLnxClient _mainFrame) {

		setTitle("显示设置");
		setAlwaysOnTop(true);
		parameters = _parameters;
		mainFrame = _mainFrame;
		setBounds(50, 50, 480, 450);
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 450, 268);
		panel.setLayout(null);
		getContentPane().add(panel);
		bgColor = Color.white;
		fgColor = Color.black;
		this.setBackground(bgColor);
		this.setForeground(fgColor);
		panel.setBackground(bgColor);
		panel.setForeground(fgColor);
		// UIManager.put("Slider.paintValue", true);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(bgColor);
		tabbedPane.setForeground(fgColor);
		panelDisplay = (JPanel) makeTextPanel("Panel #1");
		panelDisplay.setLayout(null);
		panelDisplay.setBackground(bgColor);
		panelDisplay.setForeground(fgColor);
		tabbedPane.addTab("显示设置", panelDisplay);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		panelConnection = (JPanel) makeTextPanel("Panel #2");
		panelConnection.setBackground(bgColor);
		panelConnection.setForeground(fgColor);
		tabbedPane.addTab("连接设置", panelConnection);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		panelWarning = (JPanel) makeTextPanel("Panel #3");
		panelWarning.setBackground(bgColor);
		panelWarning.setForeground(fgColor);
		tabbedPane.addTab("报警设置", panelWarning);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		panelPrint = (JPanel) makeTextPanel("Panel #4");
		panelPrint.setBackground(bgColor);
		panelPrint.setForeground(fgColor);
		tabbedPane.addTab("打印设置", panelPrint);
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

		// Add the tabbed pane to this panel.
		// The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		int tabHeight = 270, tabWidth = 450;
		tabbedPane.setBounds(0, 0, tabWidth, tabHeight);
		panel.add(tabbedPane);
		panel.setBackground(bgColor);
		panel.setForeground(fgColor);
		panelDisplay.setLayout(null);
		int startX = 10, startY = 10, groupWidth = 120;
		int intervalW = (tabWidth - 3 * groupWidth) / 4;

		startX = intervalW;
		jpRadioButtons = new JPanel();
		jpRadioButtons.setBackground(bgColor);
		jpRadioButtons.setForeground(fgColor);
		jpRadioButtons.setLayout(new GridLayout(4, 1));
		jpRadioButtons.add(backgroundLabel = new JLabel("背景网格线颜色"));
		jpRadioButtons.add(jrbOrange = new JRadioButton("Orange"));
		jpRadioButtons.add(jrbGreen = new JRadioButton("Green"));
		jpRadioButtons.add(jrbGray = new JRadioButton("Gray"));
		jrbOrange.setBackground(bgColor);
		jrbOrange.setForeground(Color.orange);
		jrbGreen.setBackground(bgColor);
		jrbGreen.setForeground(Color.green);
		jrbGray.setBackground(bgColor);
		jrbGray.setForeground(Color.gray);
		jpRadioButtons.setBounds(startX, startY, 100, 110);
		panelDisplay.add(jpRadioButtons);

		jpLineColor = new JPanel();
		jpLineColor.setBackground(bgColor);
		jpLineColor.setForeground(fgColor);
		jpLineColor.setLayout(new GridLayout(4, 1));
		jpLineColor.add(backgroundLabel = new JLabel("绘制颜色"));
		jpLineColor.add(jrbLineRed = new JRadioButton("Red"));
		jpLineColor.add(jrbLineGreen = new JRadioButton("Green"));
		jpLineColor.add(jrbLineBlue = new JRadioButton("Blue"));
		jrbLineRed.setBackground(bgColor);
		jrbLineRed.setForeground(Color.red);
		jrbLineGreen.setBackground(bgColor);
		jrbLineGreen.setForeground(Color.green);
		jrbLineBlue.setBackground(bgColor);
		jrbLineBlue.setForeground(Color.blue);
		jpLineColor.setBounds(140, startY, 100, 110);
		panelDisplay.add(jpLineColor);

		// ecg标记
		jpScale = new JPanel();
		jpScale.setBackground(bgColor);
		jpScale.setForeground(fgColor);
		jpScale.setLayout(new GridLayout(4, 1));
		jpScale.add(scaleLabel = new JLabel("ECG标记信息选择"));
		jpScale.add(showOMVLineCheckBox = new JCheckBox("显示0MV线"));
		jpScale.add(showChannelDivLineCheckBox = new JCheckBox("显示通道分割线"));
		showOMVLineCheckBox.setBackground(bgColor);
		showOMVLineCheckBox.setForeground(SettingParameters.getInstance()
				.getoMVLineColor());
		showChannelDivLineCheckBox.setBackground(bgColor);
		showChannelDivLineCheckBox.setForeground(SettingParameters
				.getInstance().getChannelDivLineColor());
		jpScale.setBounds(240, startY, 150, 110);
		panelDisplay.add(jpScale);

		sliderLine = new JSlider();
		sliderLine.setBackground(bgColor);
		sliderLine.setForeground(fgColor);
		sliderLine.setMinorTickSpacing(1);
		sliderLine.setMinimum(1);
		sliderLine.setMaximum(5);
		sliderLine.setMajorTickSpacing(2);
		sliderLine.setBounds(100, 135, 250, 45);
		sliderLine.setPaintTicks(true);
		sliderLine.setPaintLabels(true);
		sliderLine.setValue(parameters.getLineWidth());
		panelDisplay.add(sliderLine);

		lblLineWidth = new JLabel("线宽");
		lblLineWidth.setBounds(startX, 135, 70, 14);
		panelDisplay.add(lblLineWidth);

		lblFontSize = new JLabel("字体大小");
		lblFontSize.setBounds(startX, 190, 70, 14);
		panelDisplay.add(lblFontSize);

		sliderFont = new JSlider();
		sliderFont.setBackground(bgColor);
		sliderFont.setForeground(fgColor);
		sliderFont.setMinorTickSpacing(1);
		sliderFont.setMinimum(10);
		sliderFont.setMaximum(20);
		sliderFont.setMajorTickSpacing(2);
		sliderFont.setBounds(100, 190, 250, 45);
		sliderFont.setPaintTicks(true);
		sliderFont.setPaintLabels(true);
		sliderFont.setValue(parameters.getFontSize());
		panelDisplay.add(sliderFont);

		ButtonGroup group = new ButtonGroup();
		group.add(jrbOrange);
		group.add(jrbGreen);
		group.add(jrbGray);
		ButtonGroup groupLineColor = new ButtonGroup();
		groupLineColor.add(jrbLineRed);
		groupLineColor.add(jrbLineGreen);
		groupLineColor.add(jrbLineBlue);
		ButtonGroup groupTimeWindow = new ButtonGroup();
		ButtonGroup groupScale = new ButtonGroup();
		confirmButton = new JButton("确定");
		cancelButton = new JButton("取消");
		confirmButton.setBackground(bgColor);
		confirmButton.setForeground(fgColor);
		cancelButton.setBackground(bgColor);
		cancelButton.setForeground(fgColor);
		confirmButton.setBounds(208, 275, 89, 23);
		cancelButton.setBounds(345, 275, 89, 23);
		getContentPane().setBackground(bgColor);
		getContentPane().setForeground(fgColor);
		getContentPane().add(confirmButton);
		getContentPane().add(cancelButton);
		confirmButton.addActionListener(new ButtonActionListener());
		cancelButton.addActionListener(new ButtonActionListener());

		// panelConnection
		panelConnection.setLayout(null);
		startX = 30;
		startY = 30;
		ipLabel = new JLabel("IP地址");
		ipLabel.setBounds(startX, startY, 90, 14);
		panelConnection.add(ipLabel);
		ipTextField = new JTextField();
		ipTextField.setText(parameters.getIpString());
		ipTextField.setBounds(startX, startY + 5 + 14, 120, 20);
		panelConnection.add(ipTextField);
		portLabel = new JLabel("端口");
		portLabel.setBounds(startX + 120 + 20, startY, 70, 14);
		panelConnection.add(portLabel);
		portTextField = new JTextField();
		portTextField.setText(parameters.getPortString());
		portTextField.setBounds(startX + 120 + 20, startY + 5 + 14, 60, 20);
		panelConnection.add(portTextField);

		// panelWarning
		startX = 20;
		startY = 10;
		panelWarning.setLayout(null);
		panelWarning.setBackground(bgColor);
		panelWarning.setForeground(fgColor);
		hrMaxLabel = new JLabel("心率报警上限: " + parameters.getHrMax());
		hrMaxLabel.setBounds(startX, startY, 120, 14);
		panelWarning.add(hrMaxLabel);
		sliderHRMax = new JSlider();
		sliderHRMax.setForeground(fgColor);
		sliderHRMax.setBackground(bgColor);
		sliderHRMax.setMinorTickSpacing(1);
		sliderHRMax.setMinimum(100);
		sliderHRMax.setMaximum(200);
		sliderHRMax.setMajorTickSpacing(20);
		sliderHRMax.setBounds(startX, startY + 15, 180, 45);
		sliderHRMax.setPaintTicks(true);
		sliderHRMax.setPaintLabels(true);
		sliderHRMax.setValue(parameters.getHrMax());
		sliderHRMax.addChangeListener(new SliderChangeListener());
		panelWarning.add(sliderHRMax);
		hrMinLabel = new JLabel("心率报警下限: " + parameters.getHrMin());
		hrMinLabel.setBounds(startX, startY + 65, 100, 14);
		panelWarning.add(hrMinLabel);
		sliderHRMin = new JSlider();
		sliderHRMin.setBackground(bgColor);
		sliderHRMin.setForeground(fgColor);
		sliderHRMin.setMinorTickSpacing(1);
		sliderHRMin.setMinimum(30);
		sliderHRMin.setMaximum(100);
		sliderHRMin.setMajorTickSpacing(10);
		sliderHRMin.setBounds(startX, startY + 80, 180, 45);
		sliderHRMin.setPaintTicks(true);
		sliderHRMin.setPaintLabels(true);
		sliderHRMin.setValue(parameters.getHrMin());
		sliderHRMin.addChangeListener(new SliderChangeListener());
		panelWarning.add(sliderHRMin);

		boMaxLabel = new JLabel("血氧上限");
		boMaxLabel.setBounds(startX + 220, startY, 100, 14);
		panelWarning.add(boMaxLabel);
		sliderBOMax = new JSlider();
		sliderBOMax.setBackground(bgColor);
		sliderBOMax.setForeground(fgColor);
		sliderBOMax.setMinorTickSpacing(1);
		sliderBOMax.setMinimum(90);
		sliderBOMax.setMaximum(125);
		sliderBOMax.setMajorTickSpacing(5);
		sliderBOMax.setBounds(startX + 220, startY + 15, 180, 45);
		sliderBOMax.setPaintTicks(true);
		sliderBOMax.setPaintLabels(true);
		sliderBOMax.setValue(parameters.getBoMax());
		panelWarning.add(sliderBOMax);
		boMinLabel = new JLabel("血氧下限");
		boMinLabel.setBounds(startX + 220, startY + 65, 100, 14);
		panelWarning.add(boMinLabel);
		sliderBOMin = new JSlider();
		sliderBOMin.setBackground(bgColor);
		sliderBOMin.setForeground(fgColor);
		sliderBOMin.setMinorTickSpacing(1);
		sliderBOMin.setMinimum(20);
		sliderBOMin.setMaximum(55);
		sliderBOMin.setMajorTickSpacing(5);
		sliderBOMin.setBounds(startX + 220, startY + 80, 180, 45);
		sliderBOMin.setPaintTicks(true);
		sliderBOMin.setPaintLabels(true);
		sliderBOMin.setValue(parameters.getBoMin());
		panelWarning.add(sliderBOMin);

		wirelessMinLabel = new JLabel("无线信号强度下限");
		wirelessMinLabel.setBounds(startX, startY + 135, 150, 14);
		panelWarning.add(wirelessMinLabel);
		sliderWirelessMin = new JSlider();
		sliderWirelessMin.setForeground(fgColor);
		sliderWirelessMin.setBackground(bgColor);
		sliderWirelessMin.setMinorTickSpacing(1);
		sliderWirelessMin.setMinimum(0);
		sliderWirelessMin.setMaximum(1);
		sliderWirelessMin.setMajorTickSpacing(1);
		sliderWirelessMin.setBounds(startX, startY + 150, 180, 45);
		sliderWirelessMin.setPaintTicks(true);
		sliderWirelessMin.setPaintLabels(true);
		sliderWirelessMin.setValue(parameters.getWifiAlarmBoundary());
		panelWarning.add(sliderWirelessMin);

		electricMinLabel = new JLabel("电池余量下限");
		electricMinLabel.setBounds(startX + 220, startY + 135, 100, 14);
		panelWarning.add(electricMinLabel);
		sliderElectricMin = new JSlider();
		sliderElectricMin.setForeground(fgColor);
		sliderElectricMin.setBackground(bgColor);
		sliderElectricMin.setMinorTickSpacing(1);
		sliderElectricMin.setMinimum(10);
		sliderElectricMin.setMaximum(50);
		sliderElectricMin.setMajorTickSpacing(5);
		sliderElectricMin.setBounds(startX + 220, startY + 150, 180, 45);
		sliderElectricMin.setPaintTicks(true);
		sliderElectricMin.setPaintLabels(true);
		sliderElectricMin.setValue(parameters.getBatteryAlarmBoundary());
		panelWarning.add(sliderElectricMin);

		heartAlarmBox = new JCheckBox("是否心率报警");
		heartAlarmBox.setForeground(fgColor);
		heartAlarmBox.setBackground(bgColor);
		heartAlarmBox.setBounds(startX, startY + 190, 150, 45);
		heartAlarmBox.setSelected(parameters.isBpmAlarmOn());
		panelWarning.add(heartAlarmBox);

		jpGid = new JPanel();
		jpGid.setBackground(bgColor);
		jpGid.setForeground(fgColor);
		jpGid.setLayout(new GridLayout(0, 3));
		jpGid.add(jrbExistGid = new JRadioButton("保留网格"));
		jpGid.add(jrbNotExistGid = new JRadioButton("去除网格"));
		jrbExistGid.setBackground(bgColor);
		jrbExistGid.setForeground(Color.RED);
		jrbNotExistGid.setBackground(bgColor);
		jrbNotExistGid.setForeground(Color.GRAY);
		JLabel lbGrid = new JLabel("是否保留网格:");
		lbGrid.setBounds(startX, startY + 20, 120, 30);
		jpGid.setBounds(startX + 120, startY + 20, 400, 30);
		ButtonGroup groupGid = new ButtonGroup();
		groupGid.add(jrbExistGid);
		groupGid.add(jrbNotExistGid);

		jcbPaperSize = new JComboBox(strPaperSize);
		jcbPaperSize.setMaximumRowCount(5);
		JLabel lbPaper = new JLabel("打印纸尺寸选择:");
		lbPaper.setBounds(startX, startY + 100, 120, 30);
		jcbPaperSize.setBounds(startX + 120, startY + 100, 200, 30);
		panelPrint.add(lbGrid);
		panelPrint.add(lbPaper);
		panelPrint.add(jpGid);
		panelPrint.add(jcbPaperSize);

		this.setBackground(Color.LIGHT_GRAY);
		setSelectedItem();
		this.setVisible(true);
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panelTemp = new JPanel(false);
		panelTemp.setLayout(null);
		JLabel filler = new JLabel(text);
		filler.setBounds(0, 0, 0, 0);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panelTemp.add(filler);
		return panelTemp;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */

	private static void createAndShowGUI() {
		// Create and set up the window.
		SettingParameters parametersInstance = new SettingParameters();
		SettingDialog frame = new SettingDialog(parametersInstance,
				new MLnxClient());

		// Add content to the window.
		// frame.getContentPane().add(new TestSettingDialog(),
		// BorderLayout.CENTER);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth() - 460;
		double height = screenSize.getHeight() - 320;
		frame.setBounds((int) width / 2, (int) height / 2, 480, 350);
	}

	// main() for testing
	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}

	// Initialize status of all components
	private void setSelectedItem() {
		// parentPanel.setBackgroundColor(Color.orange);
		if (parameters != null) {
			if (Color.ORANGE.equals(parameters.getBackgroundColor())) {
				System.out.print("orange");
				jrbOrange.setSelected(true);
			}
			if (parameters.getBackgroundColor().equals(Color.green)) {
				jrbGreen.setSelected(true);
			}
			if (parameters.getBackgroundColor().equals(Color.gray)) {
				jrbGray.setSelected(true);
			}

			// Line color
			if (Color.red == parameters.getLineColor()) {
				jrbLineRed.setSelected(true);
			}
			if (Color.black == parameters.getLineColor()) {
				jrbLineGreen.setSelected(true);
			}
			if (Color.blue == parameters.getLineColor()) {
				jrbLineBlue.setSelected(true);
			}

			// ecgTIP
			showOMVLineCheckBox.setSelected(parameters.isShowOMVLine());
			showChannelDivLineCheckBox.setSelected(parameters
					.isShowChannelDivLine());

			if (parameters.isExistGid())
				jrbExistGid.setSelected(true);
			else
				jrbNotExistGid.setSelected(true);

			int i = 0;
			for (i = 0; i <= Style.paperSizeArr.length - 2; ++i) {
				if (parameters.getPaperSize().equals(Style.paperSizeArr[i])) {
					jcbPaperSize.setSelectedIndex(i);
					DebugTool.printLogDebug("A" + i);
					break;
				}
			}
			if (Style.paperSizeArr.length - 1 == i)
				jcbPaperSize.setSelectedIndex(i);
		}
	}

	// ******************** private class of action listener
	// **********************
	/**
	 * This private class define the behavior when user click "OK". That is
	 * collect all selected items and pass them to main frame window, then close
	 * this setting dialog
	 * 
	 * @author jfeng
	 * 
	 */
	private class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (arg0.getSource().equals(confirmButton)) // collect all setting
														// parameters then send
														// back to main frame
														// window
			{
				if (mainFrame != null) {
					if (jrbOrange.isSelected())
						parameters.setBackgroundColor(Color.orange);
					if (jrbGreen.isSelected())
						parameters.setBackgroundColor(Color.green);
					if (jrbGray.isSelected())
						parameters.setBackgroundColor(Color.gray);
					// Line color
					if (jrbLineRed.isSelected())
						parameters.setLineColor(Color.red);
					if (jrbLineGreen.isSelected())
						parameters.setLineColor(Color.green);
					if (jrbLineBlue.isSelected())
						parameters.setLineColor(Color.blue);

					// ecg tip
					parameters.setShowOMVLine(showOMVLineCheckBox.isSelected());
					parameters.setShowChannelDivLine(showChannelDivLineCheckBox
							.isSelected());

					parameters.setIpString(ipTextField.getText());
					parameters.setPortString(portTextField.getText());
					// frontSize
					parameters.setFontSize(sliderFont.getValue());
					// line width
					parameters.setLineWidth(sliderLine.getValue());
					// hr min;max
					parameters.setHrMax(sliderHRMax.getValue());
					parameters.setHrMin(sliderHRMin.getValue());
					// bo min;max
					parameters.setBoMax(sliderBOMax.getValue());
					parameters.setBoMin(sliderBOMin.getValue());
					// wireless min
					parameters.setWifiAlarmBoundary(sliderWirelessMin
							.getValue());
					// battery min
					parameters.setBatteryAlarmBoundary(sliderElectricMin
							.getValue());
					// bpm select
					parameters.setBpmAlarmOn(heartAlarmBox.isSelected());

					if (jrbExistGid.isSelected())
						parameters.setExistGid(true);
					if (jrbNotExistGid.isSelected())
						parameters.setExistGid(false);
					int i = 0;
					for (; i < strPaperSize.length - 1; ++i) {
						if (jcbPaperSize.getSelectedItem().equals(
								strPaperSize[i])) {
							parameters.setPaperSize(Style.paperSizeArr[i]);
							break;
						}
					}
					if (strPaperSize.length - 1 == i)
						parameters.setPaperSize(Style.paperSizeArr[i]);
					
					mainFrame.repaint();
				}
			} else if (arg0.getSource().equals(cancelButton)) {
				// nothing to do here actually, in case of main frame window is
				// null
			}
			onExit();
		}

	}

	private void onExit() {
		this.dispose();
	}

	private class SliderChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			Object source = e.getSource();
			JSlider theJSlider = (JSlider) source;
			if (theJSlider == sliderHRMin) {
				if (!theJSlider.getValueIsAdjusting()) {
					hrMinLabel.setText("心率报警下限: " + theJSlider.getValue());
				}
			}
			if (theJSlider == sliderHRMax) {
				if (!theJSlider.getValueIsAdjusting()) {
					hrMaxLabel.setText("心率报警上限: " + theJSlider.getValue());
				}
			}
		}
	}
}
