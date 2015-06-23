package ui.medlinx.com.frame.ECGhistory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.dialog.WaitLayerUI;
import ui.medlinx.com.extra.Style;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.databuff.HistoryDataBuffer;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;
//import junit.framework.Assert;
//import com.standbysoft.component.date.DateSelectionModel;
//import com.standbysoft.component.date.DateSelectionModel.SelectionMode;
//import com.standbysoft.component.date.swing.JDateComponent;
//import com.standbysoft.component.date.swing.JDatePicker;
//import com.standbysoft.component.date.swing.JMonthView;
//import com.standbysoft.component.date.swing.JDateComponent.DateAction;
//import com.standbysoft.component.date.swing.plaf.basic.SpinnerTimePickerUI;

/**
 * Frame to show history data
 * 
 * @author Andong
 * 
 */
public class HistoryFrame extends JFrame implements ActionListener,
		ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7844200664714875388L;
	private static final DateFormat choseDateFormat = new SimpleDateFormat(
			"EEE yyyy年MM月dd日HH时 mm分ss秒");
	private JButton buttonPrev, buttonNext, buttonOverview;
	private HistoryDrawingPanel drawingPanel;
	private JPanel controlPanel;
	private HistoryOverviewDrawingPanel overviewPanel;
	private HistoryDataBuffer DataBufferInterface;
	private JSlider slider;
	private JLabel labelStartDate, labelEndDate, labelCurrentTime;
	private JPanel north, south, center, contentPanel;
	private JButton riliButton;
	private boolean overviewFlag;

	private JButton buttonSearch;
	private JTextField textStateDate, textPatient;
	private JComboBox comboBoxStateTime;
	private String[] TIMES, patientStr;
	private String connectStr;
	private Timer stopper;
	private WaitLayerUI layerUI;
	public static final String DATE_FORMAT_TODAY = "yyyy-MM-dd";
	private DataClient dataClient;
	private com.medlinx.core.patient.Patient patientSelected;
	private Date startDate;
	private JLayer<JPanel> jlayer;
	private boolean readyForSouth;

	private Calendar choseCalendar = Calendar.getInstance(); // 选中的时刻
	private MlnxCalendarDialog mlnxCalendarDialog;

	public static void main() {
		HistoryFrame historyFrame = new HistoryFrame("");
		historyFrame.setVisible(true);
	}

	private void initHoursStr() {
		TIMES = new String[48];
		for (int i = 0; i < 10; i++) {
			TIMES[2 * i] = "0" + i + ":00";
			TIMES[2 * i + 1] = "0" + i + ":30";
		}
		for (int i = 10; i < 24; i++) {
			// int hour = i==0?12:i;
			TIMES[2 * i] = i + ":00";
			TIMES[2 * i + 1] = i + ":30";
		}
	}

	public HistoryFrame(String header, com.medlinx.core.patient.Patient p) {
		super(header);
		patientSelected = p;
		this.setSize(900, 600);
		overviewFlag = false;
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		connectStr = "localhost";
		// Setup the data from DB
		startDate = null;
		// Determine the new location of the window
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		// Move the window
		this.setLocation(x, y);
		contentPanel = (JPanel) this.getContentPane();// new JPanel();
		// this.getContentPane().add(contentPanel,BorderLayout.CENTER);
		setNorth();
		setCenter();
		setSouth();
		this.setBackground(Style.InfoAreaBackgroundColor);
		this.setVisible(true);
		setupLoadingUI();
		readyForSouth = false;
		enableComponents(south, false);
	}

	public HistoryFrame(String header) {
		this(header, null);
	}

	private void setupLoadingUI() {
		layerUI = new WaitLayerUI();
		jlayer = new JLayer<JPanel>(center, layerUI);
		this.add(jlayer);
		stopper = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// DebugTool.printLogDebug("timer is on!!"+drawingPanel.getDataBuffer().isLoading());
				if (drawingPanel != null
						&& !drawingPanel.getDataBuffer().isLoading()) {
					// DebugTool.printLogDebug("stop!!");

					enableComponents(north, true);
					if (readyForSouth)
						enableComponents(south, true);
					layerUI.stop();
				}
			}
		});
		stopper.start();
		center.remove(drawingPanel);
		Patient patientTemp = new Patient(); // fake patient
		for (int indexC = 0; indexC < 3; ++indexC)
			patientTemp.setChannelFlag(indexC);
		DataBufferInterface = new HistoryDataBuffer(patientTemp,
				"localhost:8787");
		// DataBufferInterface.start();
		drawingPanel = new HistoryDrawingPanel(this, DataBufferInterface);
		center.add(drawingPanel, BorderLayout.CENTER);
		center.revalidate();
		center.repaint();
	}

	private String today() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_TODAY);
		return sdf.format(cal.getTime());
	}

	private void setNorth() {
		north = new JPanel();
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		controlPanel = new JPanel();
		controlPanel
				.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));

		labelStartDate = new JLabel(choseDateFormat.format(choseCalendar
				.getTime()));
		labelStartDate.setFont(new Font("楷体", Font.PLAIN, 18));
		labelStartDate.setForeground(Color.BLUE);
		labelEndDate = new JLabel("病人信息:");

		initHoursStr();
		riliButton = new JButton("日历");
		riliButton.setFont(new Font("楷体", Font.PLAIN, 18));
		riliButton.setForeground(Color.BLUE);
		
		textStateDate = new JTextField(today());
		textStateDate.addActionListener(this);
		textStateDate.setFont(new Font("楷体", Font.PLAIN, 18));
		textStateDate.setForeground(Color.BLUE);
		
		textPatient = new JTextField("请选择病人");
		if (patientSelected != null && patientSelected.getPatientID() >= 0) {
			textPatient.setText("     编号:" + patientSelected.getPatientID()
					+ " 姓名：" + patientSelected.getPatientName() + " 性别："
					+ patientSelected.getGender());
		}
		textPatient.setFont(new Font("楷体", Font.PLAIN, 18));
		textPatient.setForeground(Color.BLUE);
		
		buttonSearch = new JButton("获取数据");
		buttonSearch.setFont(new Font("楷体", Font.PLAIN, 18));
		buttonSearch.setForeground(Color.BLUE);
		
		buttonOverview = new JButton("预览");
		buttonOverview.setFont(new Font("楷体", Font.PLAIN, 18));
		buttonOverview.setForeground(Color.BLUE);
		
		controlPanel.add(labelStartDate);
		controlPanel.add(riliButton);
		controlPanel.add(buttonOverview);
		controlPanel.add(labelEndDate);
		controlPanel.add(textPatient);
		controlPanel.add(buttonSearch);
		north.add(controlPanel);
		riliButton.addActionListener(this);
		buttonSearch.addActionListener(this);
		buttonOverview.addActionListener(this);

		overviewPanel = new HistoryOverviewDrawingPanel(this);
		overviewPanel.setPreferredSize(new Dimension(100, 190));
		north.add(overviewPanel);
		overviewFlag = true;

		contentPanel.add(north, BorderLayout.NORTH);
	}

	private void setCenter() {
		// default layout 1x1
		center = new JPanel();
		center.setLayout(new BorderLayout());
		Patient patientTemp = new Patient(); // fake patient
		for (int indexC = 0; indexC < 6; ++indexC)
			patientTemp.setChannelFlag(indexC);
		DataBufferInterface = new HistoryDataBuffer(patientTemp,
				"localhost:8787");
		DataBufferInterface.start();
		drawingPanel = new HistoryDrawingPanel(this, DataBufferInterface);

		center.add(drawingPanel, BorderLayout.CENTER);
		contentPanel.add(center, BorderLayout.CENTER);
	}

	private void setSouth() {
		south = new JPanel();
		labelCurrentTime = new JLabel("起始：");
		buttonPrev = addButton(SystemResources.prevIcon, "前一页");
		buttonNext = addButton(SystemResources.nextIcon, "后一页");
		slider = new JSlider();

		slider.setBackground(Color.black);
		// slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(50);
		// slider.setPaintTicks(true);
		slider.setPreferredSize(new Dimension(getWidth() - 100, 40));
		slider.setPaintLabels(true);
		slider.setMinimum(0);
		slider.setMaximum(150);
		slider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		for (int i = 0; i <= 150; i += 30)
			labels.put(i, new JLabel(i + "秒"));
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		slider.setValue(drawingPanel.getDisplayStartPoint());
		slider.addChangeListener(this);

		JPanel firstPart = new JPanel();
		south.add(buttonPrev, BorderLayout.WEST);

		firstPart.add(labelCurrentTime);
		// firstPart.add(buttonPrev);
		south.add(slider, BorderLayout.CENTER);
		south.add(buttonNext, BorderLayout.EAST);

		south.setBackground(Color.black);
		contentPanel.add(south, BorderLayout.SOUTH);
	}

	public String getConnectStr() {
		return connectStr;
	}

	public void setConnectStr(String connectStr) {
		this.connectStr = connectStr;
		DebugTool.printLogDebug("connectStr: " + connectStr);

	}

	private JButton addButton(ImageIcon icon, String tip) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setSize(Style.HistoryButtonDimension);
		button.setBackground(Style.InfoAreaBackgroundColor);
		button.setForeground(Style.InfoAreaForegroundColor);
		button.addActionListener(this);
		button.setToolTipText(tip);

		removeBorder(button);
		return button;
	}

	private void removeBorder(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		button.setContentAreaFilled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.riliButton) {
			if (mlnxCalendarDialog == null) {
				mlnxCalendarDialog = new MlnxCalendarDialog(HistoryFrame.this);
			}
			mlnxCalendarDialog.showw();
		} else if (e.getSource() == this.buttonNext) {
			boolean needLoading = drawingPanel.next();
			if (needLoading) {
				startLoadingMask();
				if (drawingPanel.getCurrentTime() != null)
					this.setCurrentTime(drawingPanel.getCurrentTime());
			}
			slider.setValue(drawingPanel.getDisplayStartPoint());
			this.repaint();
		} else if (e.getSource() == this.buttonPrev) {
			boolean needLoading = drawingPanel.prev();
			if (needLoading) {
				startLoadingMask();
				if (drawingPanel.getCurrentTime() != null)
					this.setCurrentTime(drawingPanel.getCurrentTime());
			}
			slider.setValue(drawingPanel.getDisplayStartPoint());
			this.repaint();
		} else if (e.getSource() == this.buttonSearch) {
			// to retrieve data from server
			startDate = choseCalendar.getTime();

			if (patientSelected == null || patientSelected.getPatientID() < 0) {
				JOptionPane.showMessageDialog(this, "请先点击按钮选择监控病人！");
				return;
			}
			center.remove(drawingPanel);
			DataBufferInterface = new HistoryDataBuffer(patientSelected,
					connectStr);

			drawingPanel = new HistoryDrawingPanel(this, DataBufferInterface);
			center.add(drawingPanel, BorderLayout.CENTER);
			// contentPanel.add(drawingPanel, BorderLayout.CENTER);

			slider.setValue(drawingPanel.getDisplayStartPoint());
			center.revalidate();
			center.repaint();
			startLoadingMask();
			DataBufferInterface.loadData(startDate);

		} else if (e.getSource() == this.buttonOverview) {
			if (overviewFlag) {
				north.remove(overviewPanel);
				revalidate();
				overviewFlag = false;
			} else {

				north.add(overviewPanel);
				revalidate();
				repaint();
				overviewFlag = true;
			}
		}
	}

	private void startLoadingMask() {
		readyForSouth = true;
		DataBufferInterface.setLoading(true);
		contentPanel.setEnabled(false);
		layerUI.start();
		enableComponents(north, false);
		enableComponents(south, false);
	}

	public void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

	public HistoryOverviewDrawingPanel getOverviewPanel() {
		return overviewPanel;
	}

	/**
	 * Test this frame.
	 */
	public static void main(String[] args) {
		new HistoryFrame("test");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slider) {
			drawingPanel.setDisplayStartPoint(slider.getValue());
			this.repaint();
		}
	}

	public com.medlinx.core.patient.Patient getPatientSelected() {
		return patientSelected;
	}

	public void setPatientSelected(
			com.medlinx.core.patient.Patient patientSelected) {
		this.patientSelected = patientSelected;
		textPatient.setText("     编号:" + patientSelected.getPatientID()
				+ " 姓名：" + patientSelected.getPatientName() + " 性别："
				+ patientSelected.getGender());
	}


	public void setCurrentTime(Date date) {
		choseCalendar.setTime(date);
		labelStartDate.setText(choseDateFormat.format(date));
		mlnxCalendarDialog.choseCalendar.setTime(date);
		repaint();
	}

	private ArrayList<Date> createStartTimes() {
		ArrayList<Date> tempList = new ArrayList<Date>();
		Date temp = new Date();
		temp.setHours(10);
		temp.setMinutes(10);
		tempList.add(temp);

		Date temp1 = new Date();
		temp1.setHours(20);
		temp1.setMinutes(10);
		tempList.add(temp1);
		return tempList;
	}

	private ArrayList<Date> createPhysicalTimes() {
		ArrayList<Date> tempList = new ArrayList<Date>();
		Date temp = new Date();
		temp.setHours(10);
		temp.setMinutes(20);
		tempList.add(temp);

		Date temp1 = new Date();
		temp1.setHours(10);
		temp1.setMinutes(55);
		tempList.add(temp1);

		Date temp2 = new Date();
		temp2.setHours(12);
		temp2.setMinutes(35);
		tempList.add(temp1);
		return tempList;
	}

	private ArrayList<Date> createHardwareTimes() {
		ArrayList<Date> tempList = new ArrayList<Date>();
		Date temp = new Date();
		temp.setHours(20);
		temp.setMinutes(20);
		tempList.add(temp);

		Date temp1 = new Date();
		temp1.setHours(20);
		temp1.setMinutes(55);
		tempList.add(temp1);

		Date temp2 = new Date();
		temp2.setHours(21);
		temp2.setMinutes(35);
		tempList.add(temp1);
		return tempList;
	}

	private ArrayList<Date> createEndTimes() {
		ArrayList<Date> tempList = new ArrayList<Date>();
		Date temp = new Date();
		temp.setHours(13);
		temp.setMinutes(10);
		tempList.add(temp);

		Date temp1 = new Date();
		temp1.setHours(22);
		temp1.setMinutes(10);
		tempList.add(temp1);
		return tempList;
	}
}
