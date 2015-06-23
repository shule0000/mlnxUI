package ui.medlinx.com.frame.ECGhistory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;

import com.medlinx.core.patient.PatientEventList;
import com.mlnx.pms.core.PatientEvent;

public class HistoryOverviewDrawingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HistoryFrame mainFrame;
	private SettingParameters parameters;
	private MyComponentListener resizeListener;
	private int mouseDragX;
	int widthIconHalf;

	private List<_24HourData> hourDatas; // 一天24小时心电数据存在时间段详情
	private Calendar choseCalendar;

	public HistoryOverviewDrawingPanel(HistoryFrame hf) {
		parameters = SettingParameters.getInstance();
		mainFrame = hf;
		widthIconHalf = 7;
		hourDatas = new ArrayList<_24HourData>();
		for (int i = 0; i < 24; ++i) {
			_24HourData hourData = new _24HourData();
			hourData.hour = i;
			hourData.sumExistSec = 0;
			hourDatas.add(hourData);
		}

		initializeComponents();
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
			HistoryOverviewDrawingPanel.this.repaint();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
		}

	}

	private JPanel itemDataPanel;
	private JScrollPane scrollitemDataPanel;
	private List<JPanel> itemDataPanels;
	private int selectItemDataPanel = 0;

	private JPanel itemEventPanel;
	private JLabel choseDtaLabel;

	private ECGTipPanel ecgTipPanel;

	private void initializeComponents() {

		this.setBorder(BorderFactory.createTitledBorder(""));
		this.setLayout(new GridLayout(1, 3));
		// 时间段
		{
			JPanel panel = new JPanel();
			TitledBorder titledBorder = BorderFactory
					.createTitledBorder("数据总览");
			titledBorder.setTitleColor(Color.BLUE);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 17));
			panel.setBorder(titledBorder);
			this.add(panel);

			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			// item
			{
				JPanel itemPanel = new JPanel();
				panel.add(itemPanel);
				itemPanel.setLayout(new GridLayout(1, 3));
				JLabel label = new JLabel("时间段", JLabel.CENTER);
				label.setFont(new Font("楷体", Font.BOLD, 20));
				itemPanel.add(label);

				label = new JLabel("心电数据长度", JLabel.CENTER);
				label.setFont(new Font("楷体", Font.BOLD, 20));
				itemPanel.add(label);

				label = new JLabel("事件数量", JLabel.CENTER);
				label.setFont(new Font("楷体", Font.BOLD, 20));
				itemPanel.add(label);
			}
			// 数据
			{
				itemDataPanel = new JPanel();
				scrollitemDataPanel = new JScrollPane(itemDataPanel);
				panel.add(scrollitemDataPanel);
				itemDataPanel.setLayout(new BoxLayout(itemDataPanel,
						BoxLayout.Y_AXIS));

				initItemDataPanel();
			}
		}
		// 心电数据
		{
			JPanel panel = new JPanel();
			TitledBorder titledBorder = BorderFactory
					.createTitledBorder("详细心电存在时间");
			titledBorder.setTitleColor(Color.BLUE);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 17));
			panel.setBorder(titledBorder);

			this.add(panel);
			panel.setLayout(new GridLayout(3, 1));
			// tip
			{
				JPanel tipPanel = new JPanel();
				tipPanel.setLayout(new BoxLayout(tipPanel, BoxLayout.Y_AXIS));
				panel.add(tipPanel);
				{
					JPanel tip1Panel = new JPanel(new FlowLayout(
							FlowLayout.LEFT));
					tipPanel.add(tip1Panel);

					JPanel panel2 = new JPanel();
					panel2.setBackground(Color.GREEN);
					tip1Panel.add(panel2);

					JLabel label = new JLabel(" 有心电数据");
					label.setForeground(Color.GREEN);
					label.setFont(new Font("楷体", Font.BOLD, 15));
					tip1Panel.add(label);
				}

				{
					JPanel tip2Panel = new JPanel(new FlowLayout(
							FlowLayout.LEFT));
					tipPanel.add(tip2Panel);

					JPanel panel2 = new JPanel();
					panel2.setBackground(Color.GRAY);
					tip2Panel.add(panel2);

					JLabel label = new JLabel(" 无心电数据");
					label.setForeground(Color.GRAY);
					label.setFont(new Font("楷体", Font.BOLD, 15));
					tip2Panel.add(label);
				}
			}
			// ecg data
			{
				ecgTipPanel = new ECGTipPanel();
				ecgTipPanel.addMouseListener(new pickupTimeMouseListener());
				ecgTipPanel.addMouseMotionListener(new MMotionListener());
				panel.add(ecgTipPanel);
			}
			// date
			{
				choseDtaLabel = new JLabel("00:00:00", JLabel.CENTER);
				choseDtaLabel.setFont(new Font("楷体", Font.BOLD, 20));
				panel.add(choseDtaLabel);
			}
		}
		// 事件
		{
			JPanel panel = new JPanel();
			TitledBorder titledBorder = BorderFactory
					.createTitledBorder("详细病人时间列表");
			titledBorder.setTitleColor(Color.BLUE);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 17));
			panel.setBorder(titledBorder);
			this.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			// item
			{
				JPanel itemPanel = new JPanel();
				panel.add(itemPanel);
				itemPanel.setLayout(new GridLayout(1, 2));
				JLabel label = new JLabel("时刻", JLabel.CENTER);
				label.setFont(new Font("楷体", Font.BOLD, 20));
				itemPanel.add(label);

				label = new JLabel("事件类型", JLabel.CENTER);
				label.setFont(new Font("楷体", Font.BOLD, 20));
				itemPanel.add(label);
			}
			// 数据
			{
				itemEventPanel = new JPanel();
				JScrollPane scrollPane = new JScrollPane(itemEventPanel);
				panel.add(scrollPane);
				itemEventPanel.setLayout(new GridLayout(1, 1));

				JLabel label = new JLabel("无病人事件", JLabel.CENTER);
				label.setForeground(Color.RED);
				label.setFont(new Font("楷体", Font.BOLD, 25));

				itemEventPanel.add(label);
			}
		}

		resizeListener = new MyComponentListener();
		this.addComponentListener(resizeListener);
		this.setBackground(Color.BLACK);
	}

	/**
	 * 初始化时间列表区域的信息
	 */
	private void initItemDataPanel() {
		itemDataPanel.removeAll();
		itemDataPanels = new ArrayList<JPanel>();
		for (int i = 0; i < hourDatas.size(); i++) {
			JPanel panel = new JPanel(new GridLayout(1, 3));
			itemDataPanel.add(panel);
			itemDataPanels.add(panel);
			panel.addMouseListener(new ItemDataPanelMouseAdapter(i));

			JLabel label = new JLabel(String.format("%02d", i) + ":00 ~ "
					+ String.format("%02d", i) + ":59", JLabel.CENTER);
			label.setFont(new Font("楷体", Font.BOLD, 17));
			panel.add(label);

			String string = "";
			if (hourDatas.get(i).sumExistSec > 60) {
				string = hourDatas.get(i).sumExistSec / 60 + "分"
						+ hourDatas.get(i).sumExistSec % 60 + "秒";
			} else {
				string = hourDatas.get(i).sumExistSec % 60 + "秒";
			}

			label = new JLabel(string, JLabel.CENTER);
			label.setFont(new Font("楷体", Font.BOLD, 17));
			panel.add(label);

			label = new JLabel("0", JLabel.CENTER);
			label.setFont(new Font("楷体", Font.BOLD, 17));
			panel.add(label);

			if (selectItemDataPanel == i)
				panel.setBackground(Color.yellow);
		}
		itemDataPanel.repaint();
		itemDataPanel.updateUI();
		scrollitemDataPanel.repaint();
		HistoryOverviewDrawingPanel.this.repaint();
	}

	// 时间列表每个pannel的监听器
	class ItemDataPanelMouseAdapter extends MouseAdapter {
		private int hour;

		public ItemDataPanelMouseAdapter(int hour) {
			super();
			this.hour = hour;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			for (int i = 0; i < itemDataPanels.size(); i++) {
				if (i == hour)
					itemDataPanels.get(i).setBackground(Color.yellow);
				else
					itemDataPanels.get(i).setBackground(Color.WHITE);
			}
			selectItemDataPanel = hour;
			ecgTipPanel.repaint();
			HistoryOverviewDrawingPanel.this.repaint();
		}

	}

	/**
	 * 显示ecg存在时间
	 * 
	 * @author Administrator
	 * 
	 */
	class ECGTipPanel extends JPanel {

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2D = (Graphics2D) g;
			if (g == null || hourDatas.size() == 0)
				return;

			_24HourData hourData = hourDatas.get(selectItemDataPanel);

			g2D.setColor(Color.GRAY);
			g2D.fill3DRect(0, 0, getWidth(), getHeight(), true);
			g2D.setColor(Color.GREEN);

			if (hourData != null) {
				for (DataExistInfor dataExistInfor : hourData.dataExistInfors) {
					int startx = dataExistInfor.getStartSec() * getWidth()
							/ 3600;
					int endx = dataExistInfor.getEndSec() * getWidth() / 3600;

					g2D.fill3DRect(startx, 0, endx - startx, getHeight(), true);
				}
			}

			g2D.setColor(Color.RED);
			g2D.setStroke(new BasicStroke(2));
			g2D.drawLine(mouseDragX, 0, mouseDragX, getHeight());
		}

		private void setChoseData() {
			String string = String.format("%02d", selectItemDataPanel);
			int m = mouseDragX * 3600 / getWidth() / 60;
			int s = mouseDragX * 3600 / getWidth() % 60;
			string += ":" + String.format("%02d", m);
			string += ":" + String.format("%02d", s);
			choseDtaLabel.setText(string);

			if (choseCalendar != null) {
				choseCalendar.set(Calendar.HOUR_OF_DAY, selectItemDataPanel);
				choseCalendar.set(Calendar.MINUTE, m);
				choseCalendar.set(Calendar.SECOND, s);
				mainFrame.setCurrentTime(choseCalendar.getTime());
			}
		}
	}

	/**
	 * This private class derived from MouseListener defines click on drawing
	 * component will start drawing
	 * 
	 * @author jfeng
	 * 
	 */
	private class pickupTimeMouseListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseDragX = e.getX();
			ecgTipPanel.setChoseData();
			ecgTipPanel.repaint();
			HistoryOverviewDrawingPanel.this.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	}

	private class MMotionListener implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseDragX = e.getX();
			ecgTipPanel.repaint();
			HistoryOverviewDrawingPanel.this.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

	/**
	 * 
	 * @param patientEventList
	 * @throws ParseException
	 */
	public void choseRecordDataList(Calendar choseCalendar,
			PatientEventList patientEventList) throws ParseException {
		this.choseCalendar = choseCalendar;
		hourDatas = new ArrayList<_24HourData>();

		if (patientEventList == null) {
			for (int i = 0; i < 24; ++i) {
				_24HourData hourData = new _24HourData();
				hourData.hour = i;
				hourData.sumExistSec = 0;
				hourDatas.add(hourData);
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					initItemDataPanel();
					ecgTipPanel.repaint();
				}
			});
			return;
		}

		List<PatientEvent> list = patientEventList.getList();
		List<PatientEvent> thisDayList = new ArrayList<PatientEvent>();

		DebugTool.printLogDebug("list.size() = " + list.size());

		// 筛选出当天的事件
		for (PatientEvent patientEvent : list) {
			Date start = patientEvent.getDateTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			if (choseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
					&& choseCalendar.get(Calendar.MONTH) == calendar
							.get(Calendar.MONTH)
					&& choseCalendar.get(Calendar.DAY_OF_MONTH) == calendar
							.get(Calendar.DAY_OF_MONTH))
				thisDayList.add(patientEvent);
		}

		DebugTool.printLogDebug("thisDayList.size() = " + thisDayList.size());

		// 筛选出被选中天数据的具体信息
		if (thisDayList.size() > 0) {
			Date ecgStarted = null;
			List<DataExistInfor> dataExistInfors = new ArrayList<DataExistInfor>();

			// 保存 start 和 end 对
			for (PatientEvent patientEvent : thisDayList) {

				if (patientEvent.getType()
						.equals(PatientEvent.Type.ECG_STARTED))
					ecgStarted = patientEvent.getDateTime();
				else if (patientEvent.getType().equals(
						PatientEvent.Type.ECG_STOPPED)) {
					if (ecgStarted == null) {
						Calendar start = Calendar.getInstance();
						start.set(choseCalendar.get(Calendar.YEAR),
								choseCalendar.get(Calendar.MONTH),
								choseCalendar.get(Calendar.DAY_OF_MONTH), 0, 0,
								0);
						Date end = patientEvent.getDateTime();
						dataExistInfors.add(new DataExistInfor(start.getTime(),
								end));
					} else {
						Date start = ecgStarted;
						Date end = patientEvent.getDateTime();
						dataExistInfors.add(new DataExistInfor(start, end));

						ecgStarted = null;
					}
				}
			}
			// 最后只有一个start 没有end的情况
			if (ecgStarted != null) {
				Calendar end = Calendar.getInstance();
				dataExistInfors.add(new DataExistInfor(ecgStarted, end
						.getTime()));
			}

			for (int i = 0; i < 24; ++i) {
				_24HourData hourData = new _24HourData();
				hourData.hour = i;
				hourData.sumExistSec = 0;
				for (DataExistInfor dataExistInfor : dataExistInfors) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataExistInfor.start);
					if (cal.get(Calendar.HOUR_OF_DAY) == hourData.hour) {
						cal.setTime(dataExistInfor.end);
						if (cal.get(Calendar.HOUR_OF_DAY) == hourData.hour) {
							hourData.dataExistInfors.add(new DataExistInfor(
									dataExistInfor.start, dataExistInfor.end));
							hourData.AddSumExistSec(dataExistInfor.start,
									dataExistInfor.end);
						} else {
							Calendar temp = Calendar.getInstance();
							temp.set(choseCalendar.get(Calendar.YEAR),
									choseCalendar.get(Calendar.MONTH),
									choseCalendar.get(Calendar.DAY_OF_MONTH),
									i, 59, 59);
							hourData.dataExistInfors.add(new DataExistInfor(
									dataExistInfor.start, temp.getTime()));
							hourData.AddSumExistSec(dataExistInfor.start,
									temp.getTime());
						}
					} else if (cal.get(Calendar.HOUR_OF_DAY) < hourData.hour) {
						cal.setTime(dataExistInfor.end);
						if (cal.get(Calendar.HOUR_OF_DAY) == hourData.hour) {
							Calendar temp = Calendar.getInstance();
							temp.set(choseCalendar.get(Calendar.YEAR),
									choseCalendar.get(Calendar.MONTH),
									choseCalendar.get(Calendar.DAY_OF_MONTH),
									i, 0, 0);

							hourData.dataExistInfors.add(new DataExistInfor(
									temp.getTime(), dataExistInfor.end));
							hourData.AddSumExistSec(temp.getTime(),
									dataExistInfor.end);
						}
					}
				}
				hourDatas.add(hourData);
			}
		} else {
			for (int i = 0; i < 24; ++i) {
				_24HourData hourData = new _24HourData();
				hourData.hour = i;
				hourData.sumExistSec = 0;
				hourDatas.add(hourData);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				initItemDataPanel();
				ecgTipPanel.repaint();
			}
		});
		DebugTool.printLogDebug("hourDatas.size()" + hourDatas.size());
		DebugTool.printLogDebug(hourDatas.toString());
	}

	// 数据存在的信息
	public class DataExistInfor {
		public Date start;
		public Date end;

		public DataExistInfor(Date start, Date end) {
			super();
			this.start = start;
			this.end = end;
		}

		public int getStartSec() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			return calendar.get(Calendar.MINUTE) * 60
					+ calendar.get(Calendar.SECOND);
		}

		public int getEndSec() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(end);
			return calendar.get(Calendar.MINUTE) * 60
					+ calendar.get(Calendar.SECOND);
		}

		@Override
		public String toString() {

			String format = "yyyy-MM-dd HH:mm:ss.SSS";
			return ((new SimpleDateFormat(format)).format(start) + "-->" + (new SimpleDateFormat(
					format)).format(end));
		}
	}

	public class _24HourData {
		public int hour;
		public int sumExistSec = 0;
		public List<DataExistInfor> dataExistInfors = new ArrayList<DataExistInfor>();

		public void AddSumExistSec(Date start, Date end) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			int startSec = calendar.get(Calendar.MINUTE) * 60
					+ calendar.get(Calendar.SECOND);
			calendar.setTime(end);
			int endSec = calendar.get(Calendar.MINUTE) * 60
					+ calendar.get(Calendar.SECOND);
			sumExistSec += (endSec - startSec);
		}

		@Override
		public String toString() {

			StringBuffer stringBuffer = new StringBuffer();
			for (DataExistInfor dataExistInfor : dataExistInfors) {
				stringBuffer.append(dataExistInfor.toString()).append("\n");
			}
			return stringBuffer.toString();
		}
	}

}
