package ui.medlinx.com.frame.Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import ui.medlinx.com.dialog.InitScreemPhysicsSizeDialog;
import ui.medlinx.com.dialog.LoginDialog;
import ui.medlinx.com.dialog.SettingDialog;
import ui.medlinx.com.dialog.UpdataSoftDialog;
import ui.medlinx.com.dialog.WaitingTipDialog;
import ui.medlinx.com.extra.ParamenterStore;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.frame.ECGhistory.HistoryFrame;

import com.medlinx.com.onlineSoftmanager.FileInfo;
import com.medlinx.com.onlineSoftmanager.OnlineSoftManager;
import com.medlinx.core.alarm.Alarm;
import com.medlinx.core.alarm.Alert;
import com.medlinx.core.alarm.AlertManager;
import com.medlinx.core.alarm.MetaInformation;
import com.medlinx.core.alarm.thread.AlarmTimerSound;
import com.medlinx.core.client.DataClientFactory;
import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.datafactory.DataFactory;
import com.medlinx.core.patient.Patient;

/**
 * Main frame window, container of drawing components, controller of whole
 * system.
 */
public class MLnxClient extends JFrame {

	public static boolean showRefrshStateDialog = false;
	private static final long serialVersionUID = 1L;
	private JSplitPane contentPane;
	private JPanel ceternPanel;// the userPanel component
	// contains all drawing
	// panels
	private static String connectStr;
	private static final int ALARM_INTERVAL[] = { 0, 8000, 20000, 35000 };
	private static int repaintFrequency, displayBufferFrequency,
			dataBufferFrequency; // #repaint per second for drawing panel
	// #PT per second in display buffer
	// #PT per second in data buffer
	private int delayT, alarmLevel; // delay time between reading pointer and
	// writing,pointer of data buffer.
	private ArrayList<DrawingPanel> panelList;

	// UI
	private boolean addComponent = false;
	private JLabel lblStatus, lblTime;
	private AlarmTimerSound alarmTimerSound;
	private JButton btnPatientManage, btnRealEcg; // 显示功能panel
	private JButton btnLogin, refreshAllData; // 操作功能
	private JButton btnSetting, btnSetScreemPHYSize, onlineUpdata; // 显示系统设置对话框

	// dialog
	private WaitingTipDialog loginOutDialog;

	// 功能panel
	private PatientManagePanel patientManagePanel;
	private RealECGPanel realECGPanel;

	private AlertManager alertManager = new AlertManager();
	private MetaInformation information = new MetaInformation();
	private Alert alert = new Alert();

	// timer task
	private Timer updateAnalysisTimer; // repaint all drawing panel, system
	// time, etc.
	// update analysis, information, i.e.
	// BPM,
	private final int refreshOnlineDelay = 10;
	private int recordSeconds = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (args.length >= 1) {
			connectStr = args[0];
		} else {
			connectStr = "https://192.168.1.116:8443"; // [NY] Use HTTPS
		}
		if (args.length >= 2) {
			repaintFrequency = Integer.parseInt(args[1]);
		} else {
			repaintFrequency = 30;
		}
		if (args.length >= 3) {
			displayBufferFrequency = Integer.parseInt(args[2]);
		} else {
			displayBufferFrequency = 100;
		}
		if (args.length >= 4) {
			dataBufferFrequency = Integer.parseInt(args[3]);
		} else {
			dataBufferFrequency = 300;
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MLnxClient frame = new MLnxClient();
					frame.setExtendedState(Frame.MAXIMIZED_BOTH);
					frame.setContentPane(frame.getWelcomePanel());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 欢迎界面
	 * 
	 * @return
	 */
	private JPanel getWelcomePanel() {
		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		JPanel welcomePanel = new BackgroundPanel(new ImageIcon(
				SystemConstant.MLNX_LOGO_WHITE_FILE_PATH).getImage());
		welcomePanel.setBackground(SystemConstant.BG_COLOR);
		welcomePanel.setLayout(new GridLayout(1, 1));
		JLabel welcomeLabel = new JLabel("欢迎使用美灵思心电监护系统(数据载入中请稍后 . . .)",
				JLabel.CENTER);
		welcomeLabel.setFont(new Font("楷体", Font.BOLD, 40));
		welcomeLabel.setForeground(Color.ORANGE);
		welcomePanel.add(welcomeLabel);

		return welcomePanel;
	}

	class BackgroundPanel extends JPanel {

		/** 
	     *  
	     */
		private static final long serialVersionUID = -6352788025440244338L;

		private Image image = null;

		public BackgroundPanel(Image image) {
			this.image = image;
		}

		// 固定背景图片，允许这个JPanel可以在图片上添加其他组件
		protected void paintComponent(Graphics g) {
			g.setColor(SystemConstant.BG_COLOR);
			g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
			g.drawImage(image, this.getWidth() / 4, this.getHeight() / 4,
					this.getWidth() / 2, this.getHeight() / 2,
					SystemConstant.BG_COLOR, this);
		}
	}

	/**
	 * Create the frame.
	 */
	public MLnxClient() {
		this.initData();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								if (!addComponent) {
									MLnxClient.this.initComponent();
									MLnxClient.this.addListener();
									StartTimer();
									MLnxClient.this.repaint();
									MLnxClient.this.revalidate();
								}
							}
						});
					}
				}).start();
			}
		});
	}

	/**
	 * 初始化信息
	 */
	private void initData() {

		ImageIcon imageIcon = new ImageIcon("res/icon.png");
		setIconImage(imageIcon.getImage());

		panelList = new ArrayList<DrawingPanel>();
		delayT = -2; // time delay between reading and writing pointer of data
		// buffer
		// 设置参数
		SettingParameters parameters = SettingParameters.getInstance();// set up
		// all
		// default
		ParamenterStore paramenterStore = DataFactory.readParameters();
		if (paramenterStore == null) {
			// of parameter
			parameters.setDisplayFrequency(displayBufferFrequency);
			parameters.setDataFrequency(dataBufferFrequency);
			String[] outputStr = connectStr.split(":");
			parameters.setPortString(outputStr[2]);
			parameters.setIpString(outputStr[1].substring(2,
					outputStr[1].length()));
		} else {
			parameters.initSettingParameters(paramenterStore);
			parameters.setPortString(paramenterStore.getPortString());
			parameters.setIpString(paramenterStore.getIpString());
		}
		parameters.setClient(this);

	}

	/*
	 * 设置定时器
	 */
	private void StartTimer() {
		// 设置报警器
		Alarm alarmTemp = new Alarm(3, "", null);
		alarmTimerSound = new AlarmTimerSound(alarmTemp);

		alertManager.setMetaInformation(information);
		alertManager.setSettingParameters(SettingParameters.getInstance());
		alarmLevel = 0;

		// two threads to repaint and update analysis information, repainting
		// thread will be started by some drawing panels
		updateAnalysisTimer = new Timer(1000, new UpdateAnalysis());
		updateAnalysisTimer.start();
	}

	/**
	 * 添加组件
	 */
	public void initComponent() {
		this.setTitle("美灵思心电监控系统");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		contentPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		contentPane.setOneTouchExpandable(true);
		setContentPane(contentPane);

		JPanel statePanel = new JPanel();
		statePanel.setBackground(SystemConstant.BG_COLOR);
		statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
		contentPane.setLeftComponent(statePanel);
		{
			JPanel panelbt = new JPanel();
			panelbt.setBackground(SystemConstant.BG_COLOR);
			panelbt.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
			statePanel.add(panelbt);

			// ====================功能====================//
			Font fb = new Font("楷体", Font.BOLD, 15);
			btnPatientManage = new JButton("病人管理");
			btnPatientManage.setFont(fb);

			btnRealEcg = new JButton("实时心电");
			btnRealEcg.setFont(fb);

			TitledBorder titledBorder = BorderFactory.createTitledBorder("功能");
			titledBorder.setTitleColor(Color.GREEN);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));

			JPanel panel = new JPanel();
			panel.setBackground(SystemConstant.BG_COLOR);
			panel.setBorder(titledBorder);
			panel.add(btnPatientManage);
			panel.add(btnRealEcg);
			panelbt.add(panel);
			// ====================功能====================//

			// ====================操作====================//
			titledBorder = BorderFactory.createTitledBorder("刷新数据");
			titledBorder.setTitleColor(Color.GREEN);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
			refreshAllData = new JButton("刷新群组和病人信息");
			refreshAllData.setFont(fb);

			btnLogin = new JButton("登录");
			btnLogin.setFont(fb);

			lblStatus = new JLabel(" 未登录 ");
			Font ft = new Font("楷体", Font.BOLD, 20);
			lblStatus.setForeground(Color.GREEN);
			lblStatus.setFont(ft);

			titledBorder = BorderFactory.createTitledBorder("操作");
			titledBorder.setTitleColor(Color.GREEN);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));

			panel = new JPanel();
			panel.setBackground(SystemConstant.BG_COLOR);
			panel.setBorder(titledBorder);
			panel.add(refreshAllData);
			panel.add(btnLogin);
			panel.add(lblStatus);
			panelbt.add(panel);
			// ====================操作====================//

			// ====================系统====================//
			btnSetting = new JButton("系统设置");
			btnSetting.setFont(fb);

			btnSetScreemPHYSize = new JButton("屏幕尺寸设置");
			btnSetScreemPHYSize.setFont(fb);

			onlineUpdata = new JButton("在线升级");
			onlineUpdata.setFont(fb);

			titledBorder = BorderFactory.createTitledBorder("系统");
			titledBorder.setTitleColor(Color.GREEN);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));

			panel = new JPanel();
			panel.setBackground(SystemConstant.BG_COLOR);
			panel.setBorder(titledBorder);
			panel.add(btnSetting);
			panel.add(btnSetScreemPHYSize);
			panel.add(onlineUpdata);
			panelbt.add(panel);
			// ====================系统====================//
		}

		{
			DateFormat df = new SimpleDateFormat("系统时间：yyyy/MM/dd HH:mm:ss");
			String sdt = df.format(new Date(System.currentTimeMillis()));
			lblTime = new JLabel(sdt);
			lblTime.setFont(new Font("楷体", Font.BOLD, 18));
			lblTime.setForeground(Color.GREEN);
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

			JLabel label = new JLabel("医院名称：宁波市第二人民医院");
			label.setFont(new Font("楷体", Font.BOLD, 18));
			label.setForeground(Color.GREEN);

			panel.add(label);
			panel.add(lblTime);
			statePanel.add(panel);
		}

		// 中间
		{
			// 功能界面
			patientManagePanel = new PatientManagePanel(MLnxClient.this);
			realECGPanel = new RealECGPanel(MLnxClient.this);

			ceternPanel = new JPanel();
			ceternPanel.setBorder(null);
			contentPane.setRightComponent(ceternPanel);

			ceternPanel.removeAll();
			ceternPanel.setLayout(new GridLayout(1, 1));
			ceternPanel.add(patientManagePanel);
			ceternPanel.revalidate();
			ceternPanel.repaint();
		}
	}

	/**
	 * 添加监听器
	 */
	private ActionListener loginActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			try {
				login();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	private ActionListener loginOutActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			logout();
		}
	};

	public void addListener() {

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				DataFactory.save();
			}
		});

		// 显示病人管理
		btnPatientManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (patientManagePanel == null) {
					patientManagePanel = new PatientManagePanel(MLnxClient.this);
				}
				ceternPanel.removeAll();
				ceternPanel.setLayout(new GridLayout(1, 1));
				ceternPanel.add(patientManagePanel);
				ceternPanel.revalidate();
				ceternPanel.repaint();
			}
		});
		// 显示实时心电
		btnRealEcg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ceternPanel.removeAll();
				ceternPanel.setLayout(new GridLayout(1, 1));
				ceternPanel.add(realECGPanel);
				ceternPanel.revalidate();
				ceternPanel.repaint();
			}
		});

		// 刷新病人和群组信息
		refreshAllData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (askLogin()) {
					showRefrshStateDialog = true;
					patientManagePanel.refreshPatientListTask();
				}
			}
		});

		// 登入
		btnLogin.addActionListener(loginActionListener);

		// 系统设置
		btnSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingDialog frame = new SettingDialog(SettingParameters
						.getInstance(), MLnxClient.this);
				frame.pack();
				frame.setVisible(true);

				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				double width = screenSize.getWidth() - 460;
				double height = screenSize.getHeight() - 320;
				frame.setBounds((int) width / 2, (int) height / 2, 480, 350);
			}
		});

		btnSetScreemPHYSize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				InitScreemPhysicsSizeDialog dialog = new InitScreemPhysicsSizeDialog(
						MLnxClient.this);
			}
		});

		onlineUpdata.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UpdataSoftDialog dialog = new UpdataSoftDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
	}

	private void getSoftInfo() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				FileInfo fileInfo = OnlineSoftManager
						.getSoftInfo(OnlineSoftManager.DownLoadUIInfoUrl);
				final int UIMversion = Integer.valueOf(fileInfo.getmVersion());
				final int UISversion = Integer.valueOf(fileInfo.getsVersion());
				final long UISoftLength = fileInfo.getContentLength();

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (UIMversion > SystemConstant.mVersion
								|| UISversion > SystemConstant.sVersion)
							;
					}
				});
			}
		}).start();
	}

	private void openHistoryFrame() {
		HistoryFrame histFrame = new HistoryFrame("历史数据查询");
	}

	public PatientManagePanel getPatientManagePanel() {
		return patientManagePanel;
	}

	public RealECGPanel getRealECGPanel() {
		return realECGPanel;
	}

	/*
	 * 数据更新后进行广播
	 */
	public void broadcastPGData() {
		realECGPanel.setGPData(patientManagePanel.getGroupList(),
				patientManagePanel.getPatientGroupMap());
	}

	/**
	 * 查询选择病人的实时心电
	 * 
	 * @param selectPatient
	 */
	public void watchRealECG(Patient selectPatient) {
		ceternPanel.removeAll();
		ceternPanel.setLayout(new GridLayout(1, 1));
		ceternPanel.add(realECGPanel);
		ceternPanel.revalidate();
		ceternPanel.repaint();
		realECGPanel.selectPatient(selectPatient);
	}

	/*
	 * 询问是否已经登入
	 */
	private boolean askLogin() {
		if (SettingParameters.getInstance().getLoginUser() == null) {
			JOptionPane.showMessageDialog(MLnxClient.this, "请先登入");
			return false;
		}
		return true;
	}

	/**
	 * Set login flag, if _loginFlag is true means login successfully
	 * 
	 * @param _loginFlag
	 */
	public void setLoginFlag() {
		SettingParameters parameter = SettingParameters.getInstance();
		if (parameter.getLoginUser() != null) {
			btnLogin.setText("登出");
			btnLogin.removeActionListener(loginActionListener);
			if (btnLogin.getActionListeners().length == 0) {
				btnLogin.addActionListener(loginOutActionListener);
			}
			lblStatus.setText("登录:" + parameter.getLoginUser().getId());

			showRefrshStateDialog = true;
			patientManagePanel.refreshPatientListTask();
		} else {
			btnLogin.setText("登录");
			btnLogin.removeActionListener(loginOutActionListener);
			if (btnLogin.getActionListeners().length == 0) {
				btnLogin.addActionListener(loginActionListener);
			}
			lblStatus.setText("未登录");
		}
		this.validate();
	}

	/**
	 * Login (open login dialog)
	 * 
	 * @throws IOException
	 */
	public void login() throws IOException {
		// login
		LoginDialog dialog = new LoginDialog(this);
		dialog.setVisible(true);
	}

	/**
	 * logout
	 */
	public void logout() {
		// logout
		SettingParameters parameter = SettingParameters.getInstance();
		parameter.setLoginUser(null);
		setLoginFlag();

		loginOutDialog = new WaitingTipDialog(this);
		new Thread(new Runnable() {

			@Override
			public void run() {

				patientManagePanel.unSelectP();
				MlnxDoctorClient.closeDataClient();
				DataClientFactory.stopProduce();
				DataClientFactory.clearDataClients();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						stopAndInitAll();
						loginOutDialog.close();
					}
				});
			}
		}).start();
	}

	/**
	 * 登出的时候结束和初始化所有
	 */
	private void stopAndInitAll() {
		MLnxClient.this.initComponent();
		MLnxClient.this.addListener();
		MLnxClient.this.revalidate();
		MLnxClient.this.repaint();
	}

	/**
	 * Maximize one panel to cover the whole frame window.
	 * 
	 * @param panelTemp
	 *            the panel is to maximized
	 */
	public void maximizePanel(DrawingPanel panelTemp) {
		ceternPanel.setLayout(new GridLayout(1, 1));
		ceternPanel.removeAll();
		ceternPanel.add(panelTemp);
		ceternPanel.revalidate();
		ceternPanel.repaint();
	}

	/**
	 * This private class derived from ActionListener is for updating analysis
	 * information every second informations to update: beat per minute, system
	 * time, etc.
	 * 
	 * @author jfeng
	 * 
	 */
	private class UpdateAnalysis implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// 更新当前时间
			DateFormat df = new SimpleDateFormat("系统时间：yyyy/MM/dd HH:mm:ss");
			String sdt = df.format(new Date(System.currentTimeMillis()));
			lblTime.setText(sdt);

			if (SettingParameters.getInstance().getLoginUser() != null) {
				// 更新生理信息并且获取报警等级进行报警
				int alarmSoundTemp = 4;
				alarmSoundTemp = patientManagePanel.updataAnalysisData();
				if (alarmSoundTemp == 4) {
					alarmSoundTemp = 0;
				}
				try {
					MLnxClient.this.alarmAudioPlay(alarmTimerSound,
							alarmSoundTemp);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 不是每秒钟都更新的数据
				recordSeconds++;// refreshOnlineDelay
				switch (recordSeconds % refreshOnlineDelay) {
				// 更新在线病人
				case 0:
					recordSeconds = 0;
					patientManagePanel.refreshOnlinePatientListTask();
					break;
				}
			}
		}
	}

	/*
	 * play sound to alarm
	 * 
	 * @param
	 * 
	 * @author Jianqiao Feng
	 */
	public void alarmAudioPlay(AlarmTimerSound alarmTimerSound, int level) {

		// not play
		if (0 == level) {
			alarmTimerSound.stop();
		} // play
		else if (!alarmTimerSound.isPlay()) {
			alert.setLevel(level);
			alarmTimerSound.setAlert(alert);
			alarmTimerSound.start(ALARM_INTERVAL[alert.getLevel()]);
		} // 已经在播放，但是传进来的等级比在播放的高
		else if (alarmTimerSound.getAlarm().getAlarmLevel() != level) {
			alarmTimerSound.stop();
			alert.setLevel(level);
			alarmTimerSound.setAlert(alert);
			alarmTimerSound.setAlert(alert);
			alarmTimerSound.start(ALARM_INTERVAL[alert.getLevel()]);
		}
	}
}
