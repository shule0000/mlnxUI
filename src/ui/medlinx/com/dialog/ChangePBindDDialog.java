package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.debug.LogType;
import ui.medlinx.com.frame.Main.BackgroundPanel;
import ui.medlinx.com.frame.Main.MLnxClient;
import ui.medlinx.com.frame.Main.PatientManagePanel;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.constant.SystemConstant;
import com.mlnx.pms.core.Device;

public class ChangePBindDDialog extends JDialog {

	private Dimension dialogSize = new Dimension(700, 400);

	private int patientID;
	private Device[] devices;
	private Device[] filtrateDevices;
	private PatientManagePanel managePanel;

	private JPanel[] listDevicePanels;
	private JTextField deviceIDField;
	private JPanel deviceListPanel;

	public ChangePBindDDialog(PatientManagePanel managePanel, int patientID,
			Device[] devices) {

		this.managePanel = managePanel;
		this.patientID = patientID;
		this.devices = devices;

		setIconImage(SystemResources.MlnxImageIcon.getImage());
		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		JPanel contentPane = new BackgroundPanel(image);
		contentPane.setLayout(null);
		this.setContentPane(contentPane);
		this.setResizable(false);

		// 搜索设备
		{
			JPanel searchDPanel = new JPanel();
			searchDPanel
					.setBounds(10, 10, (int) dialogSize.getWidth() - 10, 50);
			searchDPanel.setOpaque(false);
			this.add(searchDPanel);

			JLabel label = new JLabel("请筛选设备:");
			label.setForeground(Color.CYAN);
			label.setFont(new Font("楷体", Font.PLAIN, 25));

			deviceIDField = new JTextField(20);
			deviceIDField.setForeground(Color.CYAN);
			deviceIDField.setFont(new Font("楷体", Font.PLAIN, 25));
			deviceIDField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void removeUpdate(DocumentEvent e) {
							filtrateDevices();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							filtrateDevices();
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							filtrateDevices();
						}
					});

			searchDPanel.add(label);
			searchDPanel.add(deviceIDField);
		}
		// 设备列表
		{
			deviceListPanel = new JPanel();
			deviceListPanel.setLayout(new GridLayout(0, 1));
			JScrollPane scrollPane = new JScrollPane(deviceListPanel);
			scrollPane.setBounds(10, 70, (int) dialogSize.getWidth() - 20,
					(int) dialogSize.getHeight() - 100);
			deviceListPanel.setOpaque(false);
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false);
			this.add(scrollPane);

			filtrateDevices = new Device[devices.length];
			for (int i = 0; i < devices.length; i++) {
				filtrateDevices[i] = devices[i];
			}
			sortDeviceAsID(filtrateDevices);
			initDeviceListPanel();
		}

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight()));
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		this.setVisible(true);
	}

	private void filtrateDevices() {
		String filtrateString = deviceIDField.getText().trim();
		if (filtrateString.length() == 0) {
			filtrateDevices = new Device[devices.length];
			for (int i = 0; i < devices.length; i++) {
				filtrateDevices[i] = devices[i];
			}
		} else {
			List<Device> deviceList = new ArrayList<Device>();
			for (int i = 0; i < devices.length; i++) {
				if (devices[i].getId().contains(filtrateString))
					deviceList.add(devices[i]);
			}
			filtrateDevices = deviceList.toArray(new Device[0]);
		}
		sortDeviceAsID(filtrateDevices);
		initDeviceListPanel();
	}

	private void sortDeviceAsID(Device[] devices) {
		for (int i = 0; i < devices.length - 1; ++i) {
			for (int j = 0; j < devices.length - i - 1; ++j) {
				if (devices[j].getId().compareToIgnoreCase(
						devices[j + 1].getId()) > 0) {
					Device device = devices[j];
					devices[j] = devices[j + 1];
					devices[j + 1] = device;
				}
			}
		}
	}

	private void initDeviceListPanel() {

		deviceListPanel.removeAll();
		Color color = Color.white;
		Font font = new Font("楷体", Font.BOLD, 18);
		{
			JPanel panel = new JPanel(new GridLayout(1, 5));
			panel.setOpaque(false);
			deviceListPanel.add(panel);
			JPanel line = new JPanel();
			line.setSize((int) dialogSize.getWidth(), 1);
			line.setBackground(Color.GREEN);
			deviceListPanel.add(line);

			JLabel label = new JLabel("设备ID", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel("绑定的病人ID", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel("数据类型", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel("模式", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel("导联位置", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
		}

		font = new Font("楷体", Font.PLAIN, 17);
		listDevicePanels = new JPanel[filtrateDevices.length];
		for (int i = 0; i < filtrateDevices.length; i++) {
			JPanel panel = new JPanel(new GridLayout(1, 5));
			panel.setOpaque(false);
			listDevicePanels[i] = panel;
			deviceListPanel.add(panel);
			// JPanel line = new JPanel();
			// line.setSize((int) dialogSize.getWidth(), 1);
			// line.setBackground(Color.yellow);
			// deviceListPanel.add(line);

			JLabel label = new JLabel(filtrateDevices[i].getId(), JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			if (filtrateDevices[i].getPatientId() == null
					|| filtrateDevices[i].getPatientId() == 0)
				label = new JLabel("未绑定病人", JLabel.CENTER);
			else
				label = new JLabel(filtrateDevices[i].getPatientId()+"", JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel(filtrateDevices[i].getDataType() + "",
					JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel(filtrateDevices[i].getMode().toString(),
					JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);
			label = new JLabel(filtrateDevices[i].getPosition().toString(),
					JLabel.CENTER);
			label.setForeground(color);
			label.setFont(font);
			panel.add(label);

			panel.addMouseListener(new DevicePanelMouseLister(i, patientID,
					filtrateDevices[i]));
		}

		repaint();
		revalidate();
	}

	class DevicePanelMouseLister extends MouseAdapter {

		private int patientID;
		private Device device;
		private int listDevicePanelIndex;

		public DevicePanelMouseLister(int listDevicePanelIndex, int patientID,
				Device device) {
			super();
			this.listDevicePanelIndex = listDevicePanelIndex;
			this.patientID = patientID;
			this.device = device;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			for (int i = 0; i < listDevicePanels.length; i++) {
				if (i == listDevicePanelIndex)
					listDevicePanels[i].setBackground(Color.BLUE);
				else
					listDevicePanels[i].setOpaque(false);
			}
			device.setPatientId(patientID);
			managePanel.changePBindD(ChangePBindDDialog.this, device);
			DebugTool.printLog("绑定设备：" + device.getId() + "和病人: " + patientID,
					LogType.EMPUTENT);
			ChangePBindDDialog.this.dispose();
			ChangePBindDDialog.this.setVisible(false);
		}
	}
}
