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
import com.sun.xml.bind.v2.model.core.Adapter;

public class ChoseLeadDialog extends JDialog {

	private JRadioButton[] leadRadioButtons;
	private ButtonGroup leadRadioButtonsGroup;
	private Patient patient;
	private JComponent ownerPanel;

	private int model;

	public ChoseLeadDialog(Frame owner, JComponent ownerPanel, Patient patient) {
		super(owner);
		this.patient = patient;
		this.ownerPanel = ownerPanel;
		model = 1;

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

		{
			titledBorder = BorderFactory.createTitledBorder("导联模式");
			titledBorder.setTitleColor(Color.CYAN);
			titledBorder.setTitleFont(new Font("楷体", Font.PLAIN, 20));
			JPanel chanelMode = new JPanel(new GridLayout(1, 2));

			chanelMode.setBorder(titledBorder);
			chanelMode.setOpaque(false);

			JCheckBox checkBox3 = new JCheckBox("五导联");
			checkBox3.setOpaque(false);
			checkBox3.setFont(new Font("楷体", Font.PLAIN, 20));
			checkBox3.setSelected(true);
			checkBox3.setForeground(Color.WHITE);
			checkBox3.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox checkBox3 = (JCheckBox) e.getSource();
					fiveChanel(checkBox3.isSelected());
				}

			});

			JCheckBox checkBox4 = new JCheckBox("十导联");
			checkBox4.setOpaque(false);
			checkBox4.setFont(new Font("楷体", Font.PLAIN, 20));
			checkBox4.setSelected(true);
			checkBox4.setForeground(Color.WHITE);
			checkBox4.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox checkBox4 = (JCheckBox) e.getSource();
					tenChanel(checkBox4.isSelected());
				}

			});
			chanelMode.add(checkBox4);
			chanelMode.add(checkBox3);

			ButtonGroup bg = new ButtonGroup();
			bg.add(checkBox4);
			bg.add(checkBox3);

			panel.add(chanelMode);

		}

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
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox checkBox = (JCheckBox) e.getSource();
					setGroup(checkBox.isSelected());
				}
			});
			// checkBox.addChangeListener(new ChangeListener() {
			//
			// @Override
			// public void stateChanged(ChangeEvent e) {
			//
			// }
			// });

			JCheckBox checkBox2 = new JCheckBox("多导联选择");
			checkBox2.setOpaque(false);
			checkBox2.setFont(new Font("楷体", Font.PLAIN, 20));
			checkBox2.setSelected(true);
			checkBox2.setForeground(Color.WHITE);
			checkBox2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JCheckBox checkBox2 = (JCheckBox) e.getSource();
					cancelGroup(checkBox2.isSelected());
				}
			});

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
						drawingPanel.selectChannel2(selectChannelFlag, model);

					}
					ChoseLeadDialog.this.close();
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

	public void setSelectChannelFlag(boolean[] selectChannelFlag) {
		for (int i = 0; i < leadRadioButtons.length; i++) {
			leadRadioButtons[i].setSelected(selectChannelFlag[i]);
		}
		this.repaint();
	}

	private void setGroup(boolean b) {
		if (b) {
			if (model == 1) {
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
				for (int i = 0; i < leadRadioButtons.length; i++) {
					leadRadioButtonsGroup.add(leadRadioButtons[i]);
				}
			}

		}

	}

	public void cancelGroup(boolean b) {
		if (b) {
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtonsGroup.remove(leadRadioButtons[i]);
			}

			if (model == 1) {
				for (int i = 0; i < leadRadioButtons.length; i++) {
					leadRadioButtons[i].setSelected(true);
				}
			} else {
				leadRadioButtons[10].setSelected(true);
				leadRadioButtons[5].setSelected(true);
				leadRadioButtons[4].setSelected(true);
				leadRadioButtons[3].setSelected(true);
				leadRadioButtons[2].setSelected(true);
				leadRadioButtons[1].setSelected(true);
				leadRadioButtons[0].setSelected(true);
			}

		}

	}

	private void fiveChanel(boolean b) {
		if (b) {
			model = 2;
			leadRadioButtons[6].setVisible(false);
			leadRadioButtons[7].setVisible(false);
			leadRadioButtons[8].setVisible(false);
			leadRadioButtons[9].setVisible(false);
			leadRadioButtons[11].setVisible(false);

			leadRadioButtons[6].setSelected(false);
			leadRadioButtons[7].setSelected(false);
			leadRadioButtons[8].setSelected(false);
			leadRadioButtons[9].setSelected(false);
			leadRadioButtons[11].setSelected(false);

			leadRadioButtons[10].setSelected(true);
			leadRadioButtons[5].setSelected(true);
			leadRadioButtons[4].setSelected(true);
			leadRadioButtons[3].setSelected(true);
			leadRadioButtons[2].setSelected(true);
			leadRadioButtons[1].setSelected(true);
			leadRadioButtons[0].setSelected(true);
		}

	}

	private void tenChanel(boolean b) {
		if (b) {
			model = 1;
			for (int i = 0; i < leadRadioButtons.length; i++) {
				leadRadioButtons[leadRadioButtons.length - 1 - i]
						.setVisible(true);
				leadRadioButtons[leadRadioButtons.length - 1 - i]
						.setSelected(true);
			}
		}

	}

	public void close() {
		this.setVisible(false);
		this.dispose();
	}
}
