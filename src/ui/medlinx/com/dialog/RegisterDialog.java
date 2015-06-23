package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

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

import ui.medlinx.com.debug.DebugTool;

import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.constant.SystemConstant;
import com.mlnx.pms.client.ServerProcessingException;
import com.mlnx.pms.core.User;
import com.mlnx.pms.pojo.ResponseCodeType;
import com.mlnx.pms.pojo.ResponseRegisterUser;

public class RegisterDialog extends JDialog {

	private static int WIDTH = 500;
	private static int HEIGH = 350;

	private JPanel registerPanel;
	private LoginDialog loginDialog;
	private JTextField usrField, passwordField, rpasswordField;
	private JPasswordField passwordJPasswordField, rpasswordJPasswordField;
	private JRadioButton showPasswordRadioButton;
	private WaitLayerUI layerUI;
	private JLayer<JPanel> jlayer;

	public RegisterDialog(LoginDialog loginDialog) {
		super(loginDialog);
		this.loginDialog = loginDialog;

		// dialog style
		this.setModal(true);
		this.setResizable(false);
		ImageIcon imageIcon = new ImageIcon("res/icon.png");
		setIconImage(imageIcon.getImage());

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(WIDTH, HEIGH);
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));

		registerPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				ImageIcon img = new ImageIcon(SystemConstant.USR_BG_FILE_PATH);
				g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
				DebugTool.printLogDebug(getWidth() + " " + getHeight());
			}
		};
		registerPanel.setLayout(null);
		// title 0033FF
		JLabel title = new JLabel("MLNX用户注册");
		title.setFont(new Font("宋体", Font.BOLD, 25));
		title.setForeground(new Color(0x0033FF));
		title.setBounds(WIDTH / 2 - 80, 20, 160, 30);
		registerPanel.add(title);

		// line
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0x0033FF));
		panel.setBounds(0, 60, WIDTH, 2);
		registerPanel.add(panel);

		// usr
		JLabel usrLabel = new JLabel("输入用户名");
		usrLabel.setForeground(Color.WHITE);
		usrLabel.setFont(new Font("宋体", Font.BOLD, 15));
		usrLabel.setBounds(30, 70, 100, 30);
		registerPanel.add(usrLabel);
		usrField = new JTextField(30);
		usrField.setFont(new Font("宋体", Font.BOLD, 15));
		usrField.setBounds(130, 70, WIDTH - 140, 30);
		registerPanel.add(usrField);
		JLabel usrTipLabel = new JLabel("只能输入数字和英文字母");
		usrTipLabel.setFont(new Font("宋体", Font.BOLD, 15));
		usrTipLabel.setBounds(WIDTH - 200, 100, 200, 30);
		usrTipLabel.setForeground(new Color(0xFF6633));
		registerPanel.add(usrTipLabel);
		panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(0, 130, WIDTH, 1);
		registerPanel.add(panel);

		// password
		JLabel passwordLabel = new JLabel("输入密码");
		passwordLabel.setForeground(Color.WHITE);
		passwordLabel.setFont(new Font("宋体", Font.BOLD, 15));
		passwordLabel.setBounds(30, 140, 100, 30);
		registerPanel.add(passwordLabel);
		passwordJPasswordField = new JPasswordField(30);
		passwordJPasswordField.setFont(new Font("宋体", Font.BOLD, 15));
		passwordJPasswordField.setBounds(130, 140, WIDTH - 140, 30);
		registerPanel.add(passwordJPasswordField);
		passwordField = new JTextField(30);
		passwordField.setFont(new Font("宋体", Font.BOLD, 15));
		passwordField.setBounds(130, 140, WIDTH - 140, 30);
		passwordField.setVisible(false);
		registerPanel.add(passwordField);

		// rpassword
		JLabel rpasswordLabel = new JLabel("再次输入");
		rpasswordLabel.setForeground(Color.WHITE);
		rpasswordLabel.setFont(new Font("宋体", Font.BOLD, 15));
		rpasswordLabel.setBounds(30, 170, 100, 30);
		registerPanel.add(rpasswordLabel);
		rpasswordJPasswordField = new JPasswordField(30);
		rpasswordJPasswordField.setFont(new Font("宋体", Font.BOLD, 15));
		rpasswordJPasswordField.setBounds(130, 170, WIDTH - 140, 30);
		registerPanel.add(rpasswordJPasswordField);
		rpasswordField = new JTextField(30);
		rpasswordField.setFont(new Font("宋体", Font.BOLD, 15));
		rpasswordField.setBounds(130, 170, WIDTH - 140, 30);
		rpasswordField.setVisible(false);
		registerPanel.add(rpasswordField);

		// show密码
		showPasswordRadioButton = new JRadioButton("显示密码");
		showPasswordRadioButton.setForeground(Color.WHITE);
		showPasswordRadioButton.setFont(new Font("宋体", Font.BOLD, 15));
		showPasswordRadioButton.setBounds(30, 200, 100, 30);
		showPasswordRadioButton.setOpaque(false);
		showPasswordRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				showPassword(showPasswordRadioButton.isSelected());
			}
		});
		registerPanel.add(showPasswordRadioButton);
		JLabel passwordTipLabel = new JLabel("6-16位，可为大小写字母、数字");
		passwordTipLabel.setFont(new Font("宋体", Font.BOLD, 15));
		passwordTipLabel.setBounds(WIDTH - 300, 200, 300, 30);
		passwordTipLabel.setForeground(new Color(0xFF6633));
		registerPanel.add(passwordTipLabel);
		panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(0, 230, WIDTH, 1);
		registerPanel.add(panel);

		// button
		JButton button = new JButton("登录");
		button.setBackground(new Color(0x0099CC));
		button.setForeground(Color.WHITE);
		button.setFont(new Font("宋体", Font.PLAIN, 15));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// check usr
				String usr = usrField.getText();
				if (usr.isEmpty()) {
					JOptionPane
							.showMessageDialog(RegisterDialog.this, "请输入用户名");
					return;
				}
				if (!usr.matches("^[A-Za-z0-9]+$")) {
					JOptionPane.showMessageDialog(RegisterDialog.this,
							"输入的用户名存在非法字符");
					return;
				}
				// check password
				String password;
				String rpassword;
				if (showPasswordRadioButton.isSelected()) {
					password = passwordField.getText();
					rpassword = rpasswordField.getText();
				} else {
					password = new String(passwordJPasswordField.getPassword());
					rpassword = new String(rpasswordJPasswordField
							.getPassword());
				}
				if (password.length() < 6) {
					JOptionPane.showMessageDialog(RegisterDialog.this,
							"密码长度必须大于6");
					return;
				} else if (password.length() > 16) {
					JOptionPane.showMessageDialog(RegisterDialog.this,
							"密码长度必须小于16");
					return;
				} else if (!password.matches("^[A-Za-z0-9]+$")) {
					JOptionPane.showMessageDialog(RegisterDialog.this,
							"输入的密码存在非法字符");
					return;
				} else if (!password.equals(rpassword)) {
					JOptionPane.showMessageDialog(RegisterDialog.this,
							"两次输入密码不相同");
					return;
				} else {
					enableComponents(registerPanel, false);
					layerUI.start();
					SwingWorkerRegister swingWorkerRegister = new SwingWorkerRegister(
							usr, password);
					swingWorkerRegister.execute();
				}
			}
		});
		button.setBounds(10, 250, WIDTH - 20, 30);
		registerPanel.add(button);

		// setupLoadingUI
		layerUI = new WaitLayerUI();
		jlayer = new JLayer<JPanel>(registerPanel, layerUI);
		this.add(jlayer);
		this.revalidate();
		this.repaint();
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
	 * 显示密码
	 */
	private void showPassword(boolean show) {
		if (show) {
			passwordJPasswordField.setVisible(false);
			rpasswordJPasswordField.setVisible(false);
			passwordField.setVisible(true);
			rpasswordField.setVisible(true);

			passwordField.setText(new String(passwordJPasswordField
					.getPassword()));
			rpasswordField.setText(new String(rpasswordJPasswordField
					.getPassword()));
		} else {
			passwordJPasswordField.setVisible(true);
			rpasswordJPasswordField.setVisible(true);
			passwordField.setVisible(false);
			rpasswordField.setVisible(false);

			passwordJPasswordField.setText(passwordField.getText());
			rpasswordJPasswordField.setText(rpasswordField.getText());
		}
	}

	// register
	class SwingWorkerRegister extends SwingWorker<Void, Void> {

		private String usrID;
		private String password;

		public SwingWorkerRegister(String usrID, String password) {
			super();
			this.usrID = usrID;
			this.password = password;
		}

		/**
		 * 登入
		 * 
		 * @throws IOException
		 * @throws ServerProcessingException
		 */
		private void register() {
			User user = new User();
			user.setId(usrID);
			user.setPassword(password);
			ResponseRegisterUser registerUser = null;
			try {
				registerUser = MlnxDoctorClient.userRegister(user);
			} catch (ServerProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			layerUI.stop();
			enableComponents(registerPanel, true);
			if (registerUser == null) {
				JOptionPane.showMessageDialog(RegisterDialog.this, "注册失败!");
				return;
			} else {
				JOptionPane.showMessageDialog(
						RegisterDialog.this,
						ResponseCodeType.getResponseCodeType(
								registerUser.getResponseCode()).toString());
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			register();
			return null;
		}
	}

	public static void main(String[] args) {
		RegisterDialog registerDialog = new RegisterDialog(null);
		registerDialog.setVisible(true);
	}
}
