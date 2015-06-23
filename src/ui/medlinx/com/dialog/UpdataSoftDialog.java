package ui.medlinx.com.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.debug.LogType;

import com.medlinx.com.onlineSoftmanager.FileInfo;
import com.medlinx.com.onlineSoftmanager.OnlineSoftManager;
import com.medlinx.core.constant.SystemConstant;

public class UpdataSoftDialog extends JDialog {

	private static Dimension dialogSize = new Dimension(500, 300);
	private static String softAbsolutePath = new File("").getAbsolutePath()
			+ File.separator;
	private static String tempDirString = softAbsolutePath + "temp"
			+ File.separator;
	private static String tempUIString = "UI";

	private static String installString = softAbsolutePath + "install"
			+ File.separator + "install.jar";
	private static String startJavaw = softAbsolutePath + File.separator + "jre7"
			+ File.separator + "bin" + File.separator + "javaw ";
	private static String runInstallCMD = startJavaw + " -jar " + installString + " "
			+ tempDirString + tempUIString + " " + softAbsolutePath
			+ "美灵思心电监护系统.exe";

	private int UIMversion = 0;
	private int UISversion = 0;
	private long UISoftLength = 0;

	private final JPanel contentPanel = new JPanel();
	private JLabel newSoftVersionLabel;
	private JProgressBar downloadProgressBar;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UpdataSoftDialog dialog = new UpdataSoftDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public UpdataSoftDialog() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight() * 2));
		setBounds((int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2),
				(int) dialogSize.getWidth(), (int) dialogSize.getHeight());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("");
			lblNewLabel.setIcon(new ImageIcon(UpdataSoftDialog.class
					.getResource("/bg/mlnx_logo_min.png")));
			lblNewLabel.setBounds(10, 10, 252, 123);
			contentPanel.add(lblNewLabel);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("(C)2011-2015 美灵思", JLabel.CENTER);
			lblNewLabel_1.setForeground(new Color(0, 153, 255));
			lblNewLabel_1.setFont(new Font("楷体", Font.BOLD, 20));
			lblNewLabel_1.setBounds(20, 126, 252, 39);
			contentPanel.add(lblNewLabel_1);
		}
		{
			JLabel label = new JLabel("美灵思心电监护系统", JLabel.CENTER);
			label.setForeground(new Color(0, 153, 255));
			label.setFont(new Font("楷体", Font.BOLD, 20));
			label.setBounds(10, 156, 252, 39);
			contentPanel.add(label);
		}
		{
			JLabel label = new JLabel("当前版本:" + SystemConstant.mVersion + "."
					+ SystemConstant.sVersion, SwingConstants.CENTER);
			label.setForeground(new Color(47, 79, 79));
			label.setFont(new Font("楷体", Font.BOLD, 18));
			label.setBounds(272, 48, 194, 39);
			contentPanel.add(label);
		}
		{
			newSoftVersionLabel = new JLabel("正在获取最新版本号...",
					SwingConstants.CENTER);
			newSoftVersionLabel.setForeground(new Color(0, 128, 0));
			newSoftVersionLabel.setFont(new Font("楷体", Font.BOLD, 18));
			newSoftVersionLabel.setBounds(272, 79, 202, 39);
			contentPanel.add(newSoftVersionLabel);
		}

		btnNewButton = new JButton("更新软件");
		btnNewButton.setEnabled(false);
		btnNewButton.setFont(new Font("楷体", Font.BOLD, 20));
		btnNewButton.setForeground(new Color(0, 191, 255));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downLoadSoft();
			}
		});
		btnNewButton.setBounds(286, 156, 184, 39);
		contentPanel.add(btnNewButton);

		downloadProgressBar = new JProgressBar();
		downloadProgressBar.setForeground(new Color(50, 205, 50));
		downloadProgressBar.setVisible(false);
		downloadProgressBar.setStringPainted(true);
		downloadProgressBar.setValue(50);
		downloadProgressBar.setBounds(34, 219, 424, 21);
		contentPanel.add(downloadProgressBar);

		getSoftInfo();
	}

	private void downLoadSoft() {

		btnNewButton.setEnabled(false);
		downloadProgressBar.setVisible(true);
		downloadProgressBar.setMaximum((int) UISoftLength);
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(tempDirString);
				if (!file.exists())
					file.mkdir();

				Timer timer = new Timer(100, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						downloadProgressBar
								.setValue((int) OnlineSoftManager.downloadSum);
					}
				});
				timer.start();
				try {
					OnlineSoftManager.downLoadFile(
							OnlineSoftManager.DownLoadUIUrl, tempDirString
									+ tempUIString);
				} catch (IOException e1) {
					timer.stop();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									UpdataSoftDialog.this, "下载失败");
						}
					});
					return;
				}
				timer.stop();

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(UpdataSoftDialog.this,
								"下载成功，开始安装");
						UpdataSoftDialog.this.setVisible(false);
						UpdataSoftDialog.this.dispose();

						try {
							Runtime runtime = Runtime.getRuntime();
							runtime.exec(runInstallCMD);
							DebugTool.printLog(runInstallCMD, LogType.EMPUTENT);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					}
				});
			}
		}).start();
	}

	private void getSoftInfo() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				FileInfo fileInfo = OnlineSoftManager
						.getSoftInfo(OnlineSoftManager.DownLoadUIInfoUrl);
				UIMversion = Integer.valueOf(fileInfo.getmVersion());
				UISversion = Integer.valueOf(fileInfo.getsVersion());
				UISoftLength = fileInfo.getContentLength();

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						newSoftVersionLabel.setText("最新版本:" + UIMversion + "."
								+ UISversion);
						if (UIMversion > SystemConstant.mVersion
								|| UISversion > SystemConstant.sVersion)
							btnNewButton.setEnabled(true);
					}
				});
			}
		}).start();
	}
}
