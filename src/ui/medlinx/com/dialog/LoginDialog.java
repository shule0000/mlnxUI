package ui.medlinx.com.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.frame.Main.MLnxClient;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.client.DataClientFactory;
import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.constant.SystemConstant;
import com.mlnx.pms.core.User;

/**
 * Login dialog of toy version...
 * 
 */
public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private MLnxClient client;
	private JTextField usrField;
	private JPasswordField passwordField;
	private JButton button;
	private JRadioButton remenberRadioButton;
	private JPanel loginJpanel, loginPanel;
	private WaitLayerUI layerUI;
	private JLayer<JPanel> jlayer;
	private Timer stopper;
	private boolean loginReady, loginSuccess;
	private ImagePanel mlnxLogo;
	private Dimension dialogSize;
	private JLabel registerLabel, findPassowrdLabel;

	// wait UI
	private void setupLoadingUI() {
		layerUI = new WaitLayerUI();
		jlayer = new JLayer<JPanel>(loginJpanel, layerUI);
		this.add(jlayer);
		this.revalidate();
		this.repaint();

		stopper = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				layerUI.start();
				if (loginReady) {
					enableComponents(loginJpanel, true);
					layerUI.stop();
					onExit();
				}

			}
		});
		// stopper.start();
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

	public LoginDialog(MLnxClient _client) throws IOException {
		this.client = _client;
		BufferedImage bufferedImage = ImageIO.read(new File(
				SystemConstant.MLNX_LOGO_FILE_PATH));
		dialogSize = new Dimension(500, 250);

		// dialog style
		this.setModal(true);
		this.setResizable(false);
		setIconImage(SystemResources.MlnxImageIcon.getImage());

		loginReady = false;
		loginSuccess = false;

		// 布局
		getContentPane().setLayout(new BorderLayout());
		loginJpanel = new JPanel();
		loginJpanel.setLayout(new GridLayout(2, 0));
		getContentPane().add(loginJpanel, BorderLayout.CENTER);
		SettingParameters parameter = SettingParameters.getInstance();

		// mlnx logo
		{
			mlnxLogo = new ImagePanel(SystemConstant.MLNX_LOGO_WHITE_FILE_PATH);
			loginJpanel.add(mlnxLogo);
		}

		// login ui
		{
			// loginPanel = new JPanel() {
			// public void paintComponent(Graphics g) {
			// super.paintComponent(g);
			// ImageIcon img = new ImageIcon(SystemConstant.USR_BG_FILE_PATH);
			// g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
			// }
			// };
			loginPanel = new JPanel();
			loginPanel.setBackground(SystemConstant.BG_COLOR);
			loginJpanel.add(loginPanel);
			loginPanel.setLayout(null);
			int yy = 20;
			// doctor head
			{
				JLabel doctorhead = new JLabel(SystemResources.doctorHeadIcon);
				doctorhead.setBounds(30, 30+yy, 100, 100);
				loginPanel.add(doctorhead);
			}
			// usr
			{
				JLabel label = new JLabel("用户名");
				label.setForeground(Color.WHITE);
				label.setBounds(150, 30+yy, 40, 30);
				loginPanel.add(label);

				usrField = new JTextField("", 20);
				usrField.setFont(new Font("宋体", Font.PLAIN, 15));
				usrField.setToolTipText("请输入用户名");
				usrField.setBounds(200, 30+yy, 200, 30);
				usrField.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							passwordField.requestFocus();
					}
				});
				if (parameter.isRememberUsrID())
					usrField.setText(parameter.getUserID());
				loginPanel.add(usrField);
			}
			// password
			{
				JLabel label = new JLabel("密码");
				label.setForeground(Color.WHITE);
				label.setBounds(150, 59+yy, 40, 30);
				loginPanel.add(label);

				passwordField = new JPasswordField("", 20);
				passwordField.setFont(new Font("宋体", Font.PLAIN, 15));
				passwordField.setToolTipText("请输入密码");
				passwordField.setBounds(200, 59+yy, 200, 30);
				passwordField.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							enableComponents(loginPanel, false);
							stopper.start();
							SwingWorkerLogin swsLogin = new SwingWorkerLogin(
									usrField.getText(), new String(
											passwordField.getPassword()));
							swsLogin.execute();
						}
					}
				});
				loginPanel.add(passwordField);
			}

			// 记住密码
			{
				remenberRadioButton = new JRadioButton("记住用户");
				remenberRadioButton.setForeground(Color.WHITE);
				remenberRadioButton.setOpaque(false);
				remenberRadioButton.setBounds(150, 90+yy, 100, 30);
				remenberRadioButton.setSelected(parameter.isRememberUsrID());
				loginPanel.add(remenberRadioButton);
			}
			// 登入按钮
			{
				button = new JButton("登录");
				button.setBackground(new Color(0x0099CC));
				button.setForeground(Color.WHITE);
				button.setFont(new Font("宋体", Font.PLAIN, 15));
				button.addActionListener(new ButtonActionListener());
				button.setBounds(150, 120+yy, 250, 25);
				loginPanel.add(button);
			}

			// 注册和忘记密码
			{
				registerLabel = new JLabel("注册用户");
				findPassowrdLabel = new JLabel("找回密码");
				registerLabel.setBounds(408, 30+yy, 100, 30);
				loginPanel.add(registerLabel);
				findPassowrdLabel.setBounds(408, 59+yy, 100, 30);
				loginPanel.add(findPassowrdLabel);

				// set color
				registerLabel.setForeground(new Color(0x0066CC));
				findPassowrdLabel.setForeground(new Color(0x0066CC));

				// set font
				registerLabel.setFont(new Font("宋体", Font.PLAIN, 15));
				findPassowrdLabel.setFont(new Font("宋体", Font.PLAIN, 15));
				// lister
				registerLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						super.mouseClicked(e);
						RegisterDialog registerDialog = new RegisterDialog(
								LoginDialog.this);
						registerDialog.setVisible(true);
					}
				});
				findPassowrdLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						super.mouseClicked(e);

						JOptionPane.showMessageDialog(LoginDialog.this,
								"找回密码功能未添加!");

					}
				});
			}
		}

		// 监听窗口大小的变化
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {
				mlnxLogo.repaint();
				mlnxLogo.updateUI();
				LoginDialog.this.revalidate();
				LoginDialog.this.repaint();
				setSize();
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				mlnxLogo.repaint();
				mlnxLogo.updateUI();
				LoginDialog.this.revalidate();
				LoginDialog.this.repaint();
				setSize();
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {

			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				onExit();
			}
		});

		setupLoadingUI();
	}

	/**
	 * 设置dialog的尺寸 和显示的位置
	 */
	private void setSize() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight() *7/4));
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));

	}

	// image panel
	class ImagePanel extends JPanel {
		private BufferedImage bufferedImage;
		private int scal = 100;
		private boolean bigbig = false;

		public ImagePanel(String picFileName) throws IOException {
			bufferedImage = ImageIO.read(new File(picFileName));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			this.setSize((int) dialogSize.getWidth(),
					(int) dialogSize.getHeight());
			float scalF = (float) (scal / 100.0);
			g.drawImage(bufferedImage, 0, 0,
					(int) (dialogSize.getWidth() * scalF),
					(int) (dialogSize.getHeight() * scalF),
					SystemConstant.BG_COLOR, null);
		}
	}

	/*
	 * 退出对话框
	 */
	private void onExit() {
		this.dispose();
	}

	// 事件监听器
	private class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(button)) {
				String usr = usrField.getText();
				String password = new String(passwordField.getPassword());
				if (usr.isEmpty()) {
					JOptionPane.showMessageDialog(LoginDialog.this,
							"用户名不能呢个为空!");
				} else if (password.isEmpty()) {
					JOptionPane
							.showMessageDialog(LoginDialog.this, "密码不能呢个为空!");
				} else {
					SettingParameters parameter = SettingParameters
							.getInstance();
					if (remenberRadioButton.isSelected()) {
						parameter.setRememberUsrID(true);
						parameter.setUserID(usr);
					} else
						parameter.setRememberUsrID(false);
					enableComponents(loginPanel, false);
					stopper.start();
					SwingWorkerLogin swsLogin = new SwingWorkerLogin(usr,
							password);
					swsLogin.execute();
				}
				button.setForeground(Color.WHITE);
			}
		}
	}

	// login
	class SwingWorkerLogin extends SwingWorker<Void, Void> {

		private String usrID;
		private String password;

		public SwingWorkerLogin(String usrID, String password) {
			super();
			this.usrID = usrID;
			this.password = password;
		}

		/**
		 * 登入
		 */
		private void login() {
			User loginUser = null;
			SettingParameters parameter = SettingParameters.getInstance();
			parameter.setUserID(usrID);
			parameter.setPwd(new String(password));
			loginUser = MlnxDoctorClient.logIn();

			layerUI.stop();
			if (loginUser != null) {
				DebugTool.printLogDebug("Logged in as "
						+ loginUser.getFullName());
				JOptionPane.showMessageDialog(LoginDialog.this, "登录成功！");
				SettingParameters parameters = SettingParameters.getInstance();
				parameters.setLoginUser(loginUser);
				DataClientFactory.produceDataClient();
			} else {
				SettingParameters parameters = SettingParameters.getInstance();
				parameters.setLoginUser(null);
				JOptionPane.showMessageDialog(LoginDialog.this, "登录失败！");
			}
			onExit();
			client.setLoginFlag();
		}

		@Override
		protected Void doInBackground() throws Exception {
			login();
			return null;
		}
	}

}
