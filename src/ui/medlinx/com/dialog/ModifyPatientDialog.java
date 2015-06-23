package ui.medlinx.com.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.mlnx.pms.core.Patient;
import com.mlnx.pms.core.Patient.Gender;

import ui.medlinx.com.frame.Main.PatientManagePanel;

import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

public class ModifyPatientDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField patientMobileTextField;
	private JTextField patientAgeTextField;
	private JTextField patientNameTextField;
	private JLabel lblNewLabel_3;
	private JRadioButton radioButton;
	private JLabel lblNewLabel_4;
	private JRadioButton rdbtnNewRadioButton;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField patientIDNumTextField;
	private JTextField other;
	private final JCheckBox checkBox;
	private final JCheckBox checkBox_1;
	private final JCheckBox chckbxNewCheckBox;
	private final JCheckBox chckbxNewCheckBox_1;
	private final JCheckBox chckbxNewCheckBox_2;
	private final JCheckBox chckbxNewCheckBox_3;
	final JTextArea textArea;

	/**
	 * 修改病人信息的dialog
	 */
	public ModifyPatientDialog(final PatientManagePanel patientManagePanel,
			final Patient patient) {
		setTitle("修改病人信息");
		System.out.println(patient.getPastMedicalHistory());

		setBounds(
				(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - 647) / 2,
				100, 647, 486);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(0, 153, 160));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		{
			lblNewLabel_1 = new JLabel("性别");
			lblNewLabel_1.setForeground(Color.WHITE);
			lblNewLabel_1.setFont(new Font("楷体", Font.BOLD, 16));
			lblNewLabel_1.setBounds(112, 30, 40, 25);
			contentPanel.add(lblNewLabel_1);
		}
		{
			lblNewLabel_2 = new JLabel("年龄");
			lblNewLabel_2.setFont(new Font("楷体", Font.BOLD, 16));
			lblNewLabel_2.setForeground(Color.WHITE);
			lblNewLabel_2.setBounds(200, 30, 40, 25);
			contentPanel.add(lblNewLabel_2);
		}
		{
			lblNewLabel_3 = new JLabel("手机号");
			lblNewLabel_3.setFont(new Font("楷体", Font.BOLD, 16));
			lblNewLabel_3.setForeground(Color.WHITE);
			lblNewLabel_3.setBounds(270, 30, 60, 25);
			contentPanel.add(lblNewLabel_3);
		}
		{
			lblNewLabel_4 = new JLabel("身份证号");
			lblNewLabel_4.setFont(new Font("楷体", Font.BOLD, 16));
			lblNewLabel_4.setForeground(Color.WHITE);
			lblNewLabel_4.setBounds(420, 30, 75, 25);
			contentPanel.add(lblNewLabel_4);
		}
		{
			patientNameTextField = new JTextField();
			patientNameTextField.setBounds(8, 65, 85, 27);
			patientNameTextField.setText(patient.getName());
			contentPanel.add(patientNameTextField);
			patientNameTextField.setColumns(10);
		}
		{
			if (patient.getGender().equals("MALE")) {
				rdbtnNewRadioButton = new JRadioButton("男");
				rdbtnNewRadioButton.setSelected(true);
				rdbtnNewRadioButton.setFont(new Font("宋体", Font.PLAIN, 13));
				rdbtnNewRadioButton.setForeground(new Color(255, 255, 255));
				rdbtnNewRadioButton.setBackground(new Color(0, 153, 160));
				rdbtnNewRadioButton.setBounds(108, 66, 40, 25);

				radioButton = new JRadioButton("女");
				radioButton.setFont(new Font("宋体", Font.PLAIN, 13));
				radioButton.setForeground(new Color(255, 255, 255));
				radioButton.setBackground(new Color(0, 153, 160));
				radioButton.setBounds(151, 66, 40, 25);
			} else {
				rdbtnNewRadioButton = new JRadioButton("男");
				rdbtnNewRadioButton.setFont(new Font("宋体", Font.PLAIN, 13));
				rdbtnNewRadioButton.setForeground(new Color(255, 255, 255));
				rdbtnNewRadioButton.setBackground(new Color(0, 153, 160));
				rdbtnNewRadioButton.setBounds(108, 66, 40, 25);

				radioButton = new JRadioButton("女");
				rdbtnNewRadioButton.setSelected(true);
				radioButton.setFont(new Font("宋体", Font.PLAIN, 13));
				radioButton.setForeground(new Color(255, 255, 255));
				radioButton.setBackground(new Color(0, 153, 160));
				radioButton.setBounds(151, 66, 40, 25);
			}

			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(rdbtnNewRadioButton);
			buttonGroup.add(radioButton);
			contentPanel.add(rdbtnNewRadioButton);
			contentPanel.add(radioButton);
		}
		{
			JLabel lblNewLabel_6 = new JLabel("病史");
			lblNewLabel_6.setFont(new Font("楷体", Font.BOLD, 16));
			lblNewLabel_6.setForeground(new Color(255, 255, 255));
			lblNewLabel_6.setBounds(12, 130, 40, 25);
			contentPanel.add(lblNewLabel_6);
		}
		{

		}
		{
			patientAgeTextField = new JTextField();
			patientAgeTextField.setText(patient.getAge() + "");
			patientAgeTextField.setBounds(200, 65, 50, 27);
			contentPanel.add(patientAgeTextField);
			patientAgeTextField.setColumns(10);
		}
		{
			patientMobileTextField = new JTextField();
			patientMobileTextField.setText(patient.getContact());
			patientMobileTextField.setBounds(270, 65, 130, 27);
			patientMobileTextField.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(patientMobileTextField);
			patientMobileTextField.setColumns(5);
		}
		{
			patientIDNumTextField = new JTextField();
			patientIDNumTextField.setText(patient.getLastFourNumber());
			patientIDNumTextField.setBounds(420, 65, 201, 27);
			contentPanel.add(patientIDNumTextField);
			patientIDNumTextField.setColumns(10);
		}

		if (patient.getPastMedicalHistory() != null) {
			if (patient.getPastMedicalHistory().indexOf("高血压患者") != -1) {
				checkBox = new JCheckBox("高血压患者");
				checkBox.setSelected(true);
				checkBox.setFont(new Font("宋体", Font.PLAIN, 13));
				checkBox.setForeground(new Color(255, 255, 255));
				checkBox.setBackground(new Color(0, 153, 160));
				checkBox.setBounds(60, 161, 123, 25);
				contentPanel.add(checkBox);
			} else {
				checkBox = new JCheckBox("高血压患者");
				checkBox.setFont(new Font("宋体", Font.PLAIN, 13));
				checkBox.setForeground(new Color(255, 255, 255));
				checkBox.setBackground(new Color(0, 153, 160));
				checkBox.setBounds(60, 161, 123, 25);
				contentPanel.add(checkBox);
			}

			if (patient.getPastMedicalHistory().indexOf("糖尿病患者") != -1) {
				chckbxNewCheckBox = new JCheckBox("糖尿病患者");
				chckbxNewCheckBox.setSelected(true);
				chckbxNewCheckBox.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox.setBounds(228, 161, 123, 25);
				contentPanel.add(chckbxNewCheckBox);
			} else {
				chckbxNewCheckBox = new JCheckBox("糖尿病患者");
				chckbxNewCheckBox.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox.setBounds(228, 161, 123, 25);
				contentPanel.add(chckbxNewCheckBox);
			}

			if (patient.getPastMedicalHistory().indexOf("肾脏病患者") != -1) {
				chckbxNewCheckBox_1 = new JCheckBox("肾脏病患者");
				chckbxNewCheckBox_1.setSelected(true);
				chckbxNewCheckBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_1.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_1.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_1.setBounds(396, 161, 123, 25);
				contentPanel.add(chckbxNewCheckBox_1);
			} else {
				chckbxNewCheckBox_1 = new JCheckBox("肾脏病患者");
				chckbxNewCheckBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_1.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_1.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_1.setBounds(396, 161, 123, 25);
				contentPanel.add(chckbxNewCheckBox_1);
			}

			if (patient.getPastMedicalHistory().indexOf("冠心病患者") != -1) {
				checkBox_1 = new JCheckBox("冠心病患者");
				checkBox_1.setSelected(true);
				checkBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
				checkBox_1.setForeground(new Color(255, 255, 255));
				checkBox_1.setBackground(new Color(0, 153, 160));
				checkBox_1.setBounds(60, 203, 123, 25);
				contentPanel.add(checkBox_1);
			} else {
				checkBox_1 = new JCheckBox("冠心病患者");
				checkBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
				checkBox_1.setForeground(new Color(255, 255, 255));
				checkBox_1.setBackground(new Color(0, 153, 160));
				checkBox_1.setBounds(60, 203, 123, 25);
				contentPanel.add(checkBox_1);
			}

			if (patient.getPastMedicalHistory().indexOf("心肌梗塞后患者") != -1) {
				chckbxNewCheckBox_2 = new JCheckBox("心肌梗塞后患者");
				chckbxNewCheckBox_2.setSelected(true);
				chckbxNewCheckBox_2.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_2.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_2.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_2.setBounds(228, 203, 123, 25);
				contentPanel.add(chckbxNewCheckBox_2);
			} else {
				chckbxNewCheckBox_2 = new JCheckBox("心肌梗塞后患者");
				chckbxNewCheckBox_2.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_2.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_2.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_2.setBounds(228, 203, 123, 25);
				contentPanel.add(chckbxNewCheckBox_2);
			}

			if (patient.getPastMedicalHistory().indexOf("脑血管障碍患者") != -1) {
				chckbxNewCheckBox_3 = new JCheckBox("脑血管障碍患者");
				chckbxNewCheckBox_3.setSelected(true);
				chckbxNewCheckBox_3.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_3.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_3.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_3.setBounds(396, 203, 123, 25);
				contentPanel.add(chckbxNewCheckBox_3);
			} else {
				chckbxNewCheckBox_3 = new JCheckBox("脑血管障碍患者");
				chckbxNewCheckBox_3.setFont(new Font("宋体", Font.PLAIN, 13));
				chckbxNewCheckBox_3.setForeground(new Color(255, 255, 255));
				chckbxNewCheckBox_3.setBackground(new Color(0, 153, 160));
				chckbxNewCheckBox_3.setBounds(396, 203, 123, 25);
				contentPanel.add(chckbxNewCheckBox_3);
			}

			String oth = patient.getPastMedicalHistory().replaceFirst("高血压患者",
					"");
			oth = oth.replaceFirst("糖尿病患者 ", "");
			oth = oth.replaceFirst("肾脏病患者 ", "");
			oth = oth.replaceFirst("冠心病患者 ", "");
			oth = oth.replaceFirst("心肌梗塞后患者 ", "");
			oth = oth.replaceFirst("脑血管障碍患者 ", "");

			other = new JTextField();
			other.setText(oth);
			other.setBounds(93, 246, 398, 21);
			contentPanel.add(other);
			other.setColumns(10);
		} else {
			checkBox = new JCheckBox("高血压患者");
			checkBox.setFont(new Font("宋体", Font.PLAIN, 13));
			checkBox.setForeground(new Color(255, 255, 255));
			checkBox.setBackground(new Color(0, 153, 160));
			checkBox.setBounds(60, 161, 123, 25);
			contentPanel.add(checkBox);

			chckbxNewCheckBox = new JCheckBox("糖尿病患者");
			chckbxNewCheckBox.setFont(new Font("宋体", Font.PLAIN, 13));
			chckbxNewCheckBox.setForeground(new Color(255, 255, 255));
			chckbxNewCheckBox.setBackground(new Color(0, 153, 160));
			chckbxNewCheckBox.setBounds(228, 161, 123, 25);
			contentPanel.add(chckbxNewCheckBox);

			chckbxNewCheckBox_1 = new JCheckBox("肾脏病患者");
			chckbxNewCheckBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
			chckbxNewCheckBox_1.setBackground(new Color(0, 153, 160));
			chckbxNewCheckBox_1.setForeground(new Color(255, 255, 255));
			chckbxNewCheckBox_1.setBounds(396, 161, 123, 25);
			contentPanel.add(chckbxNewCheckBox_1);

			checkBox_1 = new JCheckBox("冠心病患者");
			checkBox_1.setFont(new Font("宋体", Font.PLAIN, 13));
			checkBox_1.setForeground(new Color(255, 255, 255));
			checkBox_1.setBackground(new Color(0, 153, 160));
			checkBox_1.setBounds(60, 203, 123, 25);
			contentPanel.add(checkBox_1);

			chckbxNewCheckBox_2 = new JCheckBox("心肌梗塞后患者");
			chckbxNewCheckBox_2.setFont(new Font("宋体", Font.PLAIN, 13));
			chckbxNewCheckBox_2.setForeground(new Color(255, 255, 255));
			chckbxNewCheckBox_2.setBackground(new Color(0, 153, 160));
			chckbxNewCheckBox_2.setBounds(228, 203, 123, 25);
			contentPanel.add(chckbxNewCheckBox_2);

			chckbxNewCheckBox_3 = new JCheckBox("脑血管障碍患者");
			chckbxNewCheckBox_3.setFont(new Font("宋体", Font.PLAIN, 13));
			chckbxNewCheckBox_3.setForeground(new Color(255, 255, 255));
			chckbxNewCheckBox_3.setBackground(new Color(0, 153, 160));
			chckbxNewCheckBox_3.setBounds(396, 203, 123, 25);
			contentPanel.add(chckbxNewCheckBox_3);

			other = new JTextField();
			other.setBounds(93, 246, 398, 21);
			contentPanel.add(other);
			other.setColumns(10);
		}

		JLabel label = new JLabel("姓名");
		label.setFont(new Font("楷体", Font.BOLD, 16));
		label.setForeground(Color.WHITE);
		label.setBounds(12, 30, 40, 25);
		contentPanel.add(label);

		JLabel label_1 = new JLabel("其它");
		label_1.setFont(new Font("宋体", Font.PLAIN, 13));
		label_1.setForeground(Color.WHITE);
		label_1.setBounds(60, 244, 32, 25);
		contentPanel.add(label_1);

		JLabel label_2 = new JLabel("备注");
		label_2.setFont(new Font("楷体", Font.BOLD, 16));
		label_2.setForeground(new Color(255, 255, 255));
		label_2.setBackground(new Color(0, 153, 160));
		label_2.setBounds(12, 287, 40, 25);
		contentPanel.add(label_2);

		if (patient.getRemark() != null) {
			textArea = new JTextArea();
			textArea.setText(patient.getRemark());
			textArea.setBounds(79, 318, 411, 82);
			contentPanel.add(textArea);
		} else {
			textArea = new JTextArea();
			textArea.setBounds(79, 318, 411, 82);
			contentPanel.add(textArea);
		}

		JLabel label_3 = new JLabel("(用空格间隔)");
		label_3.setForeground(Color.WHITE);
		label_3.setBounds(93, 267, 75, 25);
		contentPanel.add(label_3);
		contentPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[]{lblNewLabel_3, lblNewLabel_4, lblNewLabel_1,
						lblNewLabel_2}));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(0, 153, 160));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			JButton btnNewButton = new JButton("修改");
			btnNewButton.setMnemonic(13);
			btnNewButton.setFont(new Font("楷体", Font.BOLD, 16));
			btnNewButton.setBackground(new Color(30, 144, 255));
			btnNewButton.setForeground(new Color(240, 248, 255));
			buttonPane.add(btnNewButton);
			btnNewButton.addActionListener(new ActionListener() {

				// 确认修改病人信息
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (checkInputAll()) {
						com.mlnx.pms.core.Patient modiPatient = new com.mlnx.pms.core.Patient();
						modiPatient.setId(patient.getId());
						modiPatient.setName(patientNameTextField.getText());
						modiPatient.setAge(Integer.valueOf(patientAgeTextField
								.getText()));
						modiPatient.setContact(patientMobileTextField.getText());
						modiPatient.setLastFourNumber(patientIDNumTextField
								.getText());
						modiPatient.setGender(rdbtnNewRadioButton.isSelected()
								? Gender.MALE
								: Gender.FEMALE);
						String history = "";
						if (checkBox.isSelected()) {
							history = history + checkBox.getText() + " ";
						}
						if (checkBox_1.isSelected()) {
							history = history + checkBox_1.getText() + " ";
						}
						if (chckbxNewCheckBox.isSelected()) {
							history = history + chckbxNewCheckBox.getText()
									+ " ";
						}
						if (chckbxNewCheckBox_1.isSelected()) {
							history = history + chckbxNewCheckBox_1.getText()
									+ " ";
						}
						if (chckbxNewCheckBox_2.isSelected()) {
							history = history + chckbxNewCheckBox_2.getText()
									+ " ";
						}
						if (chckbxNewCheckBox_3.isSelected()) {
							history = history + chckbxNewCheckBox_3.getText()
									+ " ";
						}
						history = history + other.getText();

						modiPatient.setPastMedicalHistory(history);
						modiPatient.setRemark(textArea.getText());

						PatientManagePanel.PatientManageSwingWorker patientManageSwingWorker = patientManagePanel.new PatientManageSwingWorker();
						patientManageSwingWorker.modifyPatient(modiPatient);

						ModifyPatientDialog.this.dispose();

					}
				}

			});

			JButton btnNewButton_1 = new JButton("还原");
			btnNewButton_1.setFont(new Font("楷体", Font.BOLD, 16));
			btnNewButton_1.setBackground(new Color(30, 144, 255));
			btnNewButton_1.setForeground(new Color(240, 248, 255));
			buttonPane.add(btnNewButton_1);
			btnNewButton_1.addActionListener(new ActionListener() {

				// 取消修改并还原到原本信息
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					ModifyPatientDialog.this.dispose();
					ModifyPatientDialog modifyPatientDialog = new ModifyPatientDialog(
							patientManagePanel, patient);
					modifyPatientDialog.setVisible(true);
				}

			});

		}
	}

	// 测试对病人操作时信息是否填写完整
	private boolean checkInputAll() {
		if (patientNameTextField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(ModifyPatientDialog.this, "请输入病人姓名");
			return false;
		} else if (!rdbtnNewRadioButton.isSelected()
				&& !radioButton.isSelected()) {
			JOptionPane.showMessageDialog(ModifyPatientDialog.this, "选择病人性别");
			return false;
		} else if (patientAgeTextField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(ModifyPatientDialog.this, "请输入病人年龄");
			return false;
		} else if (patientMobileTextField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(ModifyPatientDialog.this, "请输入病人手机号");
			return false;
		} else if (patientIDNumTextField.getText().isEmpty()) {
			JOptionPane
					.showMessageDialog(ModifyPatientDialog.this, "请输入病人身份证号");
			return false;
		}
		return true;
	}
}
