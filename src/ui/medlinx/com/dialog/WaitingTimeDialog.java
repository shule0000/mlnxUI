package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.medlinx.com.frame.Main.BackgroundPanel;
import ui.medlinx.com.frame.Main.MLnxClient;

import com.medlinx.core.constant.SystemConstant;

public class WaitingTimeDialog extends JDialog {
	private MLnxClient client;

	private JLabel tipLabel;
	private JPanel loginOutJpanel;

	public WaitingTimeDialog(MLnxClient _client) {
		super(_client);
		this.client = _client;

		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		loginOutJpanel = new BackgroundPanel(image);
		this.setContentPane(loginOutJpanel);
		loginOutJpanel.setLayout(new GridLayout(1, 1));
		tipLabel = new JLabel("请等待", JLabel.CENTER);
		tipLabel.setFont(new Font("楷体", Font.BOLD, 25));
		tipLabel.setForeground(Color.GREEN);
		loginOutJpanel.add(tipLabel);

		this.setResizable(false);
		this.setAlwaysOnTop(true);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = new Dimension(700, 190);
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight() * 2));
		System.out
				.println(dialogSize.getWidth() + " " + dialogSize.getHeight());
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		this.setVisible(true);
	}

	public void close() {
		this.setVisible(false);
		this.dispose();
	}

	public JLabel getTipLabel() {
		return tipLabel;
	}

	public void setTipLabel(JLabel tipLabel) {
		this.tipLabel = tipLabel;
	}
	
	public static void main(String[] args) {
		new WaitingTimeDialog(null);
	}
}
