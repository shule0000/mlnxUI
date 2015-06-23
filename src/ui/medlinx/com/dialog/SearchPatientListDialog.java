package ui.medlinx.com.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ui.medlinx.com.frame.Main.PatientManagePanel;

public class SearchPatientListDialog extends JDialog {

	ScrollPane scrollPane = new ScrollPane();
	private JPanel searchPatientListPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public SearchPatientListDialog(
			List<com.mlnx.pms.core.Patient> searchPatients,
			final PatientManagePanel patientManagePanel) {
		setTitle("搜索病人结果");
		setBounds(
				(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - 650) / 2,
				100, 650, 400);
		getContentPane().setLayout(new BorderLayout());
		searchPatientListPanel.setLayout(new FlowLayout());
		searchPatientListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.add(searchPatientListPanel);

		if (searchPatientListPanel != null)
			searchPatientListPanel.removeAll();
		else
			searchPatientListPanel = new JPanel();
		if (searchPatients == null || searchPatients.size() == 0) {
			searchPatientListPanel.setLayout(new GridLayout(1, 1));
			JPanel panel = new JPanel(new GridLayout(1, 1));
			JLabel label = new JLabel("无", JLabel.CENTER);
			panel.add(label);
			searchPatientListPanel.add(panel);
			return;
		}

		searchPatientListPanel.setLayout(new GridLayout(0, 1));

		{

			JPanel panel = new JPanel(new GridLayout(1, 5));
			searchPatientListPanel.add(panel);
			JPanel line = new JPanel();
			line.setSize((int) searchPatientListPanel.getSize().getWidth(), 1);
			line.setBackground(Color.BLUE);
			searchPatientListPanel.add(line);

			JLabel label = new JLabel("姓名", JLabel.CENTER);
			panel.add(label);
			label = new JLabel("年龄", JLabel.CENTER);
			panel.add(label);
			label = new JLabel("性别", JLabel.CENTER);
			panel.add(label);
			label = new JLabel("联系方式", JLabel.CENTER);
			panel.add(label);
			label = new JLabel("身份证号码", JLabel.CENTER);
			panel.add(label);

		}

		for (com.mlnx.pms.core.Patient patient : searchPatients) {

			JPanel panel = new JPanel(new GridLayout(1, 5));
			panel.addMouseListener(patientManagePanel.new SearchMouseListener(
					patient.getId(), SearchPatientListDialog.this));
			searchPatientListPanel.add(panel);
			JPanel line = new JPanel();
			line.setSize((int) searchPatientListPanel.getSize().getWidth(), 1);
			line.setBackground(Color.CYAN);
			searchPatientListPanel.add(line);

			JLabel label = new JLabel(patient.getName(), JLabel.CENTER);
			panel.add(label);
			label = new JLabel(patient.getAge() + "", JLabel.CENTER);
			panel.add(label);
			label = new JLabel(patient.getGender() + "", JLabel.CENTER);
			panel.add(label);
			label = new JLabel(patient.getContact(), JLabel.CENTER);
			panel.add(label);
			label = new JLabel(patient.getLastFourNumber(), JLabel.CENTER);
			panel.add(label);

		}
		searchPatientListPanel.updateUI();
	}
}
