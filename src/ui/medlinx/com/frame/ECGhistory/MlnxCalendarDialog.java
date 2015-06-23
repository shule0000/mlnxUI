package ui.medlinx.com.frame.ECGhistory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.patient.PatientEventList;
import com.mlnx.pms.core.PatientEvent;

public class MlnxCalendarDialog extends JDialog {

	private static final String WEEKS_STRING[] = { "星期日", "星期一", "星期二", "星期三",
			"星期四", "星期五", "星期六" };
	private static final String MONTH_STRING[] = { "一月", "二月", "三月", "四月",
			"五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };
	private static final int DialogW = 450;
	private static final int DialogH = 380;

	private PatientEventList patientEventList = null;
	private Set<Integer> days = null;

	private HistoryFrame historyFrame;

	public Calendar choseCalendar = Calendar.getInstance();
	private int daysOfMonth[][] = new int[6][7];

	private JLabel prevYearLabel, preMonthLabel, showCalendarLabel,
			nextMonthLabel, nextYearLabel;
	private JPanel daysPanel;
	private JLabel daysOfMonthLabel[][] = new JLabel[6][7];
	private JPanel daysOfMonthPanels[][] = new JPanel[6][7];

	private JPanel mlnxCalendarPanel;
	private JLabel waitLabel;

	public static void main(String[] args) {

		Calendar calendar = Calendar.getInstance();
		DebugTool.printLogDebug(calendar.get(Calendar.HOUR_OF_DAY));
	}

	public MlnxCalendarDialog(HistoryFrame historyFrame) {

		this.historyFrame = historyFrame;

		this.setResizable(false);
		this.setAlwaysOnTop(true);

		mlnxCalendarPanel = new JPanel();
		this.setContentPane(mlnxCalendarPanel);
		this.revalidate();
		this.repaint();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);

				MlnxCalendarDialog.this.close();
			}
		});

		initUI();

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = new Dimension(DialogW + 20, DialogH);
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight()));
		System.out
				.println(dialogSize.getWidth() + " " + dialogSize.getHeight());
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		this.setVisible(true);

		initPatientEventList();
	}

	public PatientEventList getPatientEventList() {
		return patientEventList;
	}

	/**
	 * 设置登入对话框的是否可点击
	 * 
	 * @param container
	 * @param enable
	 */
	public void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

	/**
	 * 初始化 patientEventList
	 */
	private void initPatientEventList() {
		if (patientEventList == null) {
			waitLabel.setText("正在获取数据, 请等待");
			enableComponents(mlnxCalendarPanel, false);
			new Thread(new Runnable() {

				@Override
				public void run() {
					int pid = historyFrame.getPatientSelected().getPatientID();
					Calendar firstDayInTheMonth = Calendar.getInstance();

					firstDayInTheMonth.set(choseCalendar.get(Calendar.YEAR),
							choseCalendar.get(Calendar.MONTH), 1, 0, 0, 0);
					firstDayInTheMonth.set(Calendar.MILLISECOND, 0);
					Date start = firstDayInTheMonth.getTime();
					Calendar firstDayInNextMonth = firstDayInTheMonth;
					firstDayInNextMonth.add(Calendar.MONTH, 1);
					Date end = new Date(
							firstDayInNextMonth.getTime().getTime() - 1);

					patientEventList = MlnxDoctorClient.getPatientEvents(pid,
							start, end);
					if (patientEventList != null) {
						merge(patientEventList);
					}

					days = getECGRecordingDays();

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							enableComponents(mlnxCalendarPanel, true);
							initDaysPanel();

							waitLabel.setText("绿色字体代表该天有数据，黑色代表没有数据");

							try {
								historyFrame.getOverviewPanel()
										.choseRecordDataList(choseCalendar,
												patientEventList);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}).start();
		}
	}

	/**
	 * 
	 * @param list
	 *            PatientEvent list function: 当连续出现两个ecgstop
	 *            时间的时候，取第二个时间覆盖到第一个时间
	 */
	private void merge(PatientEventList list) {
		List<PatientEvent> tempPatientEvents = new ArrayList<PatientEvent>();
		List<PatientEvent> patientEvents = list.getList();
		for (Iterator iterator = patientEvents.iterator(); iterator.hasNext();) {
			PatientEvent patientEvent = (PatientEvent) iterator.next();
			if (patientEvent.getType().equals(PatientEvent.Type.ECG_STOPPED)
					&& tempPatientEvents.size() >= 1
					&& tempPatientEvents.get(tempPatientEvents.size() - 1)
							.getType().equals(PatientEvent.Type.ECG_STOPPED)) {
				tempPatientEvents.get(tempPatientEvents.size() - 1)
						.setDateTime(patientEvent.getDateTime());
			} else
				tempPatientEvents.add(patientEvent);
		}
		list.setList(tempPatientEvents);
	}

	/**
	 * 获取一个月存在心电数据的天数
	 * 
	 * @return
	 */
	public Set<Integer> getECGRecordingDays() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Set<Integer> days = new HashSet<Integer>();
		if (patientEventList == null)
			return days;
		List<PatientEvent> list = patientEventList.getList();
		Date ecgStarted = patientEventList.getStart();
		if (list != null) {
			for (PatientEvent event : list) {

				if (event.getType().equals(PatientEvent.Type.ECG_STARTED)) {
					ecgStarted = event.getDateTime();
				} else if (event.getType()
						.equals(PatientEvent.Type.ECG_STOPPED)) {
					if (ecgStarted != null) {
						Date ecgStopped = event.getDateTime();
						DebugTool.printLogDebug("ecg start "
								+ sdf.format(ecgStarted));
						System.out
								.println("ecg stop " + sdf.format(ecgStopped));
						Calendar cal = Calendar.getInstance();
						cal.setTime(ecgStarted);
						int startDay = cal.get(Calendar.DAY_OF_MONTH);
						cal.setTime(ecgStopped);
						int endDay = cal.get(Calendar.DAY_OF_MONTH);
						DebugTool.printLogDebug(startDay + "," + endDay);
						for (int i = startDay; i <= endDay; i++) {
							days.add(i);
						}
						ecgStarted = null;
					}
				}
			}
			if (ecgStarted != null) {
				Calendar cal = Calendar.getInstance();
				Date start = ecgStarted;
				Date end = patientEventList.getEnd();
				cal.setTime(start);
				int startDay = cal.get(Calendar.DAY_OF_MONTH);
				cal.setTime(end);
				int endDay = cal.get(Calendar.DAY_OF_MONTH);
				Calendar today = Calendar.getInstance();
				if (today.get(Calendar.MONTH) == cal.get(Calendar.MONTH))
					endDay = today.get(Calendar.DAY_OF_MONTH);
				for (int i = startDay; i <= endDay; i++) {
					days.add(i);
				}
			}
		}
		return days;
	}

	public void close() {
		this.setVisible(false);
	}

	public void showw() {
		this.setVisible(true);
	}

	private void initUI() {
		mlnxCalendarPanel.setLayout(null);
		// 控制按钮
		{
			JPanel panel = new JPanel(new GridLayout(1, 5));
			panel.setBounds(0, 0, DialogW, 50);
			mlnxCalendarPanel.add(panel);

			prevYearLabel = new JLabel(SystemResources.prevYearIcon);
			prevYearLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					choseCalendar.set(Calendar.YEAR,
							choseCalendar.get(Calendar.YEAR) - 1);
					processClick();
				}
			});
			panel.add(prevYearLabel);

			preMonthLabel = new JLabel(SystemResources.prevMonthIcon);
			preMonthLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					int m = choseCalendar.get(Calendar.MONTH);
					if (m > 0)
						choseCalendar.set(Calendar.MONTH, m - 1);
					else if (m == 0) {
						choseCalendar.set(Calendar.YEAR,
								choseCalendar.get(Calendar.YEAR) - 1);
						choseCalendar.set(Calendar.MONTH, 11);
					}
					processClick();
				}
			});
			panel.add(preMonthLabel);

			String string = MONTH_STRING[choseCalendar.get(Calendar.MONTH)]
					+ " " + choseCalendar.get(Calendar.YEAR);
			showCalendarLabel = new JLabel(string, JLabel.CENTER);
			panel.add(showCalendarLabel);

			nextMonthLabel = new JLabel(SystemResources.nextMonthIcon);
			nextMonthLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					int m = choseCalendar.get(Calendar.MONTH);
					if (m < 11)
						choseCalendar.set(Calendar.MONTH, m + 1);
					else if (m == 11) {
						choseCalendar.set(Calendar.YEAR,
								choseCalendar.get(Calendar.YEAR) + 1);
						choseCalendar.set(Calendar.MONTH, 0);
					}
					processClick();
				}
			});
			panel.add(nextMonthLabel);

			nextYearLabel = new JLabel(SystemResources.nextYearIcon);
			nextYearLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					choseCalendar.set(Calendar.YEAR,
							choseCalendar.get(Calendar.YEAR) + 1);
					processClick();
				}
			});
			panel.add(nextYearLabel);
		}

		// 星期
		{
			JPanel panel = new JPanel(new GridLayout(1, 7));
			panel.setBounds(0, 50, DialogW, 30);
			mlnxCalendarPanel.add(panel);
			for (int i = 0; i < 7; i++) {
				JLabel label = new JLabel(WEEKS_STRING[i], JLabel.CENTER);
				panel.add(label);
			}

			JPanel linePanel = new JPanel();
			linePanel.setBackground(Color.BLACK);
			linePanel.setBounds(5, 80, DialogW - 10, 1);
			mlnxCalendarPanel.add(linePanel);
		}

		// 日期
		{
			daysPanel = new JPanel(new GridLayout(6, 7));
			daysPanel.setBounds(0, 80, DialogW, 180);
			mlnxCalendarPanel.add(daysPanel);

			initDaysPanel();

			JPanel linePanel = new JPanel();
			linePanel.setBackground(Color.GRAY);
			linePanel.setBounds(5, 260, DialogW - 10, 1);
			mlnxCalendarPanel.add(linePanel);
		}

		// 今天
		{
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.setBounds(0, 270, DialogW, 30);
			mlnxCalendarPanel.add(panel);

			JLabel label = new JLabel();
			label.setFont(new Font("楷体", Font.BOLD, 20));
			Calendar todayCalendar = Calendar.getInstance();
			String string = "Today is ";
			string += todayCalendar.get(Calendar.DAY_OF_MONTH) + " ";
			string += MONTH_STRING[todayCalendar.get(Calendar.MONTH)] + " ";
			string += todayCalendar.get(Calendar.YEAR);
			label.setText(string);

			panel.add(label);
		}

		// 等待
		{
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.setBounds(0, 300, DialogW, 30);
			mlnxCalendarPanel.add(panel);

			waitLabel = new JLabel("绿色字体代表该天有数据，黑色代表没有数据");
			waitLabel.setForeground(Color.RED);
			waitLabel.setFont(new Font("楷体", Font.BOLD, 15));
			panel.add(waitLabel);
		}
	}

	private void initDaysPanel() {

		for (int i = 0; i < daysOfMonth.length; i++) {
			for (int j = 0; j < daysOfMonth[i].length; j++) {
				daysOfMonth[i][j] = -1;
			}
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, choseCalendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, choseCalendar.get(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int daySum = choseCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		int m = dayOfWeek;
		int n = 0;
		for (int i = 1; i <= daySum; i++) {
			daysOfMonth[n][m] = i;
			m++;
			if (m >= 7) {
				m = 0;
				n++;
			}
		}

		daysPanel.removeAll();
		for (int i = 0; i < daysOfMonth.length; i++) {
			for (int j = 0; j < daysOfMonth[i].length; j++) {
				JLabel label = new JLabel("", JLabel.CENTER);
				label.setFont(new Font("宋体", Font.BOLD, 15));
				label.addMouseListener(new DayMouseListener());

				daysOfMonthLabel[i][j] = label;
				daysOfMonthPanels[i][j] = new JPanel();
				daysOfMonthPanels[i][j].add(label);
				daysOfMonthPanels[i][j].setBackground(Color.WHITE);

				if (daysOfMonth[i][j] != -1) {
					label.setText(daysOfMonth[i][j] + "");
					if (days != null && days.contains(daysOfMonth[i][j])) {
						label.setForeground(Color.GREEN);
					}
					if (choseCalendar.get(Calendar.DAY_OF_MONTH) == daysOfMonth[i][j]) {
						daysOfMonthPanels[i][j].setBackground(Color.yellow);
					}
				}
				daysPanel.add(daysOfMonthPanels[i][j]);
			}
		}
		daysPanel.updateUI();
		daysPanel.repaint();
		MlnxCalendarDialog.this.repaint();

	}

	class MouseAdapter implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void processClick() {
			String string = MONTH_STRING[choseCalendar.get(Calendar.MONTH)]
					+ " " + choseCalendar.get(Calendar.YEAR);
			showCalendarLabel.setText(string);

			initDaysPanel();
			patientEventList = null;
			initPatientEventList();

			DebugTool.printLogDebug("processClick");
		}
	}

	class DayMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			String text = label.getText();
			if (text.length() > 0) {
				choseCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(text));
				for (int i = 0; i < daysOfMonthLabel.length; i++) {
					for (int j = 0; j < daysOfMonthLabel[i].length; j++) {
						if (daysOfMonthLabel[i][j].getText().equals(text)) {
							daysOfMonthPanels[i][j].setBackground(Color.yellow);
						} else {
							daysOfMonthPanels[i][j].setBackground(Color.WHITE);
						}
					}
				}
				historyFrame.setCurrentTime(choseCalendar.getTime());
				try {
					historyFrame.getOverviewPanel().choseRecordDataList(
							choseCalendar, patientEventList);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			// daysPanel.updateUI();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
