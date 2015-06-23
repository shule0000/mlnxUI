package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.frame.Main.BackgroundPanel;
import ui.medlinx.com.frame.Main.MLnxClient;

import com.medlinx.core.constant.SystemConstant;

public class WaitingTipDialog extends JDialog {

	private MLnxClient client;

	private JLabel tipLabel;
	private JPanel loginOutJpanel;

	private String tipString;
	private int recordNumb = 0;
	private Timer waitTimer;

	private static WaitingTipDialog waitingTipDialog;

	public static void showDialog(MLnxClient client, String tipString) {
		waitingTipDialog = new WaitingTipDialog(client, tipString);

		DebugTool.printLogDebug("start waitingTipDialog");
	}

	public static void closeDialog() {
		if (waitingTipDialog != null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					waitingTipDialog.getClient().getPatientManagePanel()
							.finishSelectP();
					waitingTipDialog.close();
					waitingTipDialog = null;
				}
			});
			DebugTool.printLogDebug("stop waitingTipDialog");
		}

		DebugTool.printLogDebug("waitingTipDialog = " + waitingTipDialog);
	}

	public WaitingTipDialog(MLnxClient client) {
		this(client, "正在登出");
	}

	public MLnxClient getClient() {
		return client;
	}

	public WaitingTipDialog(MLnxClient client, String tipString) {

		this.client = client;
		this.tipString = tipString;

		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		loginOutJpanel = new BackgroundPanel(image);
		this.setContentPane(loginOutJpanel);
		loginOutJpanel.setLayout(new GridLayout(1, 1));
		tipLabel = new JLabel(tipString+" . . . . .", JLabel.CENTER);
		tipLabel.setFont(new Font("楷体", Font.BOLD, 25));
		tipLabel.setForeground(Color.GREEN);
		loginOutJpanel.add(tipLabel);

		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				
				WaitingTipDialog.this.close();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = new Dimension(700, 350);
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight()));
		System.out
				.println(dialogSize.getWidth() + " " + dialogSize.getHeight());
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		this.setVisible(true);

		waitTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				StringBuffer tipBuffer = new StringBuffer(
						WaitingTipDialog.this.tipString);

				WaitingTipDialog.this.recordNumb++;
				if (WaitingTipDialog.this.recordNumb > 4)
					WaitingTipDialog.this.recordNumb = 1;
				for (int i = 0; i < WaitingTipDialog.this.recordNumb; i++) {
					tipBuffer.append(" .");
				}
				 tipLabel.setText(tipBuffer.toString());
			}
		});
		waitTimer.start();
	}

	public void close() {
		
		if (this.isShowing()){
			this.dispose();
			this.setVisible(false);
			waitTimer.stop();
		}
	}

	public static void main(String[] args) {
		new WaitingTipDialog(null);
	}
}
