package com.medlinx.com.onlineSoftmanager;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class upLoadFileFrame extends JFrame {

	private JPanel contentPane;
	private JProgressBar progressBar;
	private JTextField mVtextField;
	private JTextField sVField;
	private JTextField pathtextField;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;

	private int mVersion;
	private int sVersion;

	private boolean sucess;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					upLoadFileFrame frame = new upLoadFileFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public upLoadFileFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("主版本号");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		lblNewLabel.setForeground(new Color(0, 191, 255));
		lblNewLabel.setBounds(10, 39, 88, 15);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("子版本号");
		lblNewLabel_1.setForeground(new Color(0, 191, 255));
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(205, 40, 72, 15);
		contentPane.add(lblNewLabel_1);

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setForeground(new Color(0, 128, 0));
		progressBar.setValue(50);
		progressBar.setBounds(10, 220, 424, 30);
		contentPane.add(progressBar);

		mVtextField = new JTextField();
		mVtextField.setBounds(91, 38, 88, 21);
		contentPane.add(mVtextField);
		mVtextField.setColumns(10);

		sVField = new JTextField();
		sVField.setColumns(10);
		sVField.setBounds(286, 39, 88, 21);
		contentPane.add(sVField);

		pathtextField = new JTextField();
		pathtextField.setBounds(131, 86, 303, 21);
		contentPane.add(pathtextField);
		pathtextField.setColumns(10);

		JLabel label = new JLabel("上传文件路径");
		label.setForeground(new Color(0, 191, 255));
		label.setFont(new Font("宋体", Font.PLAIN, 18));
		label.setBounds(10, 87, 113, 15);
		contentPane.add(label);

		JButton btnNewButton = new JButton("选择文件");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser file = new UPLoadFileChooser(".");
				int result = file.showSaveDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					String uploadFilePath = file.getSelectedFile()
							.getAbsolutePath();
					pathtextField.setText(uploadFilePath);
					progressBar.setVisible(false);
				}
			}
		});
		btnNewButton.setBounds(10, 168, 194, 23);
		contentPane.add(btnNewButton);

		JButton button = new JButton("上传文件");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File(pathtextField.getText());
				if (!file.exists()) {
					JOptionPane.showMessageDialog(upLoadFileFrame.this,
							"选择的文件不存在");
					return;
				}

				try {
					mVersion = Integer.valueOf(mVtextField.getText());
					sVersion = Integer.valueOf(sVField.getText());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(upLoadFileFrame.this,
							"输入的版本号格式不对");
					return;
				}

				progressBar.setVisible(true);
				progressBar.setValue(0);
				try {
					InputStream inputStream = new FileInputStream(file);
					progressBar.setMaximum(inputStream.available());
					inputStream.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						sucess = false;

						Timer timer = new Timer(100, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								progressBar
										.setValue((int) OnlineSoftManager.uploadSum);
							}
						});
						timer.start();

						if (rdbtnNewRadioButton.isSelected()) {
							sucess = OnlineSoftManager.upLoadFile(
									OnlineSoftManager.UpLoadUIUrl
											+ mVersion + "." + sVersion + "/",
									pathtextField.getText());
							
							System.out.println(OnlineSoftManager.UpLoadUIUrl
											+ mVersion + "." + sVersion + "/");

						} else if (rdbtnNewRadioButton_1.isSelected())
							sucess = OnlineSoftManager.upLoadFile(
									OnlineSoftManager.UpLoadDoctorUrl
											+ mVersion + "." + sVersion + "/",
									pathtextField.getText());
						timer.stop();
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								if (sucess)
									JOptionPane.showMessageDialog(
											upLoadFileFrame.this, "上传成功");
								else
									JOptionPane.showMessageDialog(
											upLoadFileFrame.this, "上传失败");
							}
						});
					}
				}).start();
			}
		});
		button.setBounds(221, 168, 194, 23);
		contentPane.add(button);

		Panel panel = new Panel();
		panel.setBounds(10, 120, 424, 30);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(0, 3, 0, 3));

		ButtonGroup buttonGroup = new ButtonGroup();

		rdbtnNewRadioButton = new JRadioButton("电脑UI");
		rdbtnNewRadioButton.setSelected(true);
		panel.add(rdbtnNewRadioButton);
		buttonGroup.add(rdbtnNewRadioButton);

		rdbtnNewRadioButton_1 = new JRadioButton("doctorApp");
		panel.add(rdbtnNewRadioButton_1);
		buttonGroup.add(rdbtnNewRadioButton_1);

		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("其他");
		rdbtnNewRadioButton_2.setEnabled(false);
		panel.add(rdbtnNewRadioButton_2);
		buttonGroup.add(rdbtnNewRadioButton_2);
	}

	/*
	 * JFileChooser class and renew approveSelection funtion
	 */
	class UPLoadFileChooser extends JFileChooser {
		public UPLoadFileChooser() {
			this.addChoosableFileFilter(new EXEFileFilter("exe"));
		}

		public UPLoadFileChooser(String path) {
			super(path);
			this.addChoosableFileFilter(new EXEFileFilter("exe"));
		}

		public void approveSelection() {
			super.approveSelection();
		}

		/*
		 * FileFilter
		 */
		private class EXEFileFilter extends FileFilter {

			String ext;

			public EXEFileFilter(String ext) {
				this.ext = ext;
			}

			public boolean accept(File file) {
				if (file.isDirectory())
					return true;

				String fileName = file.getName();
				int atPointPos = fileName.indexOf('.');

				if (atPointPos > 0 && atPointPos < fileName.length() - 1) {
					String getExt = fileName.substring(atPointPos + 1);
					if (getExt.equals(ext))
						return true;
				}
				return false;
			}

			public String getDescription() {
				return "EXE 文件(*.exe)";
			}
		}
	}
}
