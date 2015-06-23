package ui.medlinx.com.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui.medlinx.com.frame.Main.BackgroundPanel;
import ui.medlinx.com.frame.Main.DrawingPanel;
import ui.medlinx.com.frame.Main.PatientManagePanel;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.patient.Patient;

public class ChoseLeadDialog extends JDialog {

	private JRadioButton[] leadRadioButtons;
	private ButtonGroup leadRadioButtonsGroup;
	private Patient patient;
	private JComponent ownerPanel;

	public ChoseLeadDialog(Frame owner, JComponent ownerPanel, Patient patient) {
		super(owner);
		this.patient = patient;
		this.ownerPanel = ownerPanel;

		setIconImage(SystemResources.MlnxImageIcon.getImage());
		java.awt.Image image = new ImageIcon(SystemConstant.MAIN_BG_FILE_PATH)
				.getImage();
		JPanel panel = new BackgroundPanel(image);
		this.setContentPane(panel);
		this.setResizable(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		TitledBorder titledBorder = BorderFactory.createTitledBorder(patient
				.getPatientName() + " 导联选择");
		titledBorder.setTitleColor(Color.CYAN);
		titledBorder.setTitleFont(new Font("楷体", Font.PLAIN, 25));
		panel.setBorder(titledBorder);

		// 导联
		{
			leadRadioButtonsGroup = new ButtonGroup();
			leadRadioButtons = new JRadioButton[SystemConstant.ECGLEADNAMES
					.size()];
			titledBorder = BorderFactory.createTitledBorder("导联列表");
			titledBorder.setTitleColor(Color.CYAN);
			titledBorder.setTitleFont(new Font("楷体", Font.PLAIN, 20));
			JPanel choseLeadPanel = new JPanel(new GridLayout(4,
					leadRadioButtons.length / 4, 5, 5));
			choseLeadPanel.setBorder(titledBorder);
			choseLeadPanel.setOpaque(false);
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtons[i] = new JRadioButton(
						SystemConstant.ECGLEADNAMES.get(i));
				leadRadioButtons[i].setFont(new Font("楷体", Font.PLAIN, 20));
				leadRadioButtons[i].setSelected(true);
				leadRadioButtons[i].setOpaque(false);
				leadRadioButtons[i].setForeground(Color.WHITE);
				leadRadioButtons[i].addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						for (int i = 0; i < leadRadioButtons.length; i++) {
							if (leadRadioButtons[i].isSelected())
								leadRadioButtons[i].setForeground(Color.CYAN);
							else
								leadRadioButtons[i].setForeground(Color.WHITE);
						}
					}
				});
				choseLeadPanel.add(leadRadioButtons[i]);
			}
			panel.add(choseLeadPanel);
		}

		// 导联控制
		{
			JPanel panel2 = new JPanel(
					new FlowLayout(FlowLayout.CENTER, 10, 10));
			panel2.setOpaque(false);

			JCheckBox checkBox = new JCheckBox("单导联选择");
			checkBox.setOpaque(false);
			checkBox.setFont(new Font("楷体", Font.PLAIN, 20));
			checkBox.setForeground(Color.WHITE);
			checkBox.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					JCheckBox checkBox = (JCheckBox) e.getSource();
					setGroup(checkBox.isSelected());
				}
			});

			JCheckBox checkBox2 = new JCheckBox("多导联选择");
			checkBox2.setOpaque(false);
			checkBox2.setFont(new Font("楷体", Font.PLAIN, 20));
			checkBox2.setSelected(true);
			checkBox2.setForeground(Color.WHITE);

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(checkBox);
			buttonGroup.add(checkBox2);

			panel2.add(checkBox);
			panel2.add(checkBox2);
			panel.add(panel2);
		}
		// button
		{
			JPanel panel2 = new JPanel(new GridLayout(1, 1));
			JButton button = new JButton("确认");
			button.setFont(new Font("楷体", Font.PLAIN, 20));
			button.setBackground(Color.CYAN);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean[] selectChannelFlag = new boolean[leadRadioButtons.length];
					for (int i = 0; i < leadRadioButtons.length; i++) {
						selectChannelFlag[i] = leadRadioButtons[i].isSelected();
					}
					if (ChoseLeadDialog.this.ownerPanel instanceof PatientManagePanel) {
					} else if (ChoseLeadDialog.this.ownerPanel instanceof DrawingPanel) {
						DrawingPanel drawingPanel = (DrawingPanel) ChoseLeadDialog.this.ownerPanel;
						drawingPanel.selectChannel(selectChannelFlag);
					}
				}
			});
			panel2.add(button);
			panel.add(panel2);
		}

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = new Dimension(300, 200);
		this.setSize((int) (dialogSize.getWidth()),
				(int) (dialogSize.getHeight() * 2));
		System.out
				.println(dialogSize.getWidth() + " " + dialogSize.getHeight());
		this.setLocation(
				(int) (dimension.getWidth() / 2 - this.getWidth() / 2),
				(int) (dimension.getHeight() / 2 - this.getHeight() / 2));
		this.setVisible(true);
	}
	
	public void setSelectChannelFlag(boolean[] selectChannelFlag){
		for (int i = 0; i < leadRadioButtons.length; i++) {
			leadRadioButtons[i].setSelected(selectChannelFlag[i]);
		}
		this.repaint();
	}

	private void setGroup(boolean isGroup) {
		if (isGroup) {
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtonsGroup.remove(leadRadioButtons[i]);
			}
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtonsGroup.add(leadRadioButtons[i]);
			}
		} else {
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtonsGroup.remove(leadRadioButtons[i]);
			}
		}
	}

	public void close() {
		this.setVisible(false);
		this.dispose();
	}
}
