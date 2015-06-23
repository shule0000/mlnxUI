package ui.medlinx.com.doctor_tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.extra.Style;

public class DoctorCommentDialog extends JDialog implements ActionListener {

	JLabel recordTip, commentTitle, timeTip;
	JTextArea commentField;
	JPanel soundLevelLabelArray[];
	int levelSum = 13;
	JButton recordSound, stopRecordSound, saveRecordSound, existRecordSound;
	JButton playBack, stopPlayBack, openFiled;
	JRadioButton recordSoundType, playSoundType;
	JPanel switchType;

	boolean startRecordFlag, pauseRecordFlag, stopRecordFlag, saveFlagFlag;
	boolean startPlayBackFlag, pausePlayBackFlag, stopPlayBackFlag;
	boolean recordType;

	RecordPlayer recordPlayer;

	Timer showDateTimer;
	int recordShowDate = 0;

	public DoctorCommentDialog(Frame owner, boolean modal) {
		super(owner, modal);
		// TODO Auto-generated constructor stub
		this.setTitle("医生录音备注");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		startRecordFlag = false;
		pauseRecordFlag = false;
		stopRecordFlag = true;
		saveFlagFlag = true;

		startPlayBackFlag = false;
		pausePlayBackFlag = false;
		stopPlayBackFlag = true;
		recordPlayer = new RecordPlayer(this);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 460, 350);
		panel.setBackground(Color.WHITE);

		commentTitle = new JLabel("备注主题:");
		commentTitle.setBounds(5, 5, 90, 20);
		commentTitle.setFont(new Font("宋体", Font.PLAIN, 18));

		commentField = new JTextArea();
		commentField.setBackground(new Color(238, 238, 238));
		commentField.setFont(new Font("宋体", Font.PLAIN, 15));
		JScrollPane scrollPane = new JScrollPane(commentField);
		scrollPane.setBounds(90, 5, 350, 100);

		recordTip = new JLabel("录音停止...");
		recordTip.setBounds(10, 170, 90, 20);
		recordTip.setForeground(Color.BLACK);
		recordTip.setFont(new Font("宋体", Font.PLAIN, 15));

		timeTip = new JLabel("00:00");
		timeTip.setBounds(30, 190, 90, 20);
		timeTip.setForeground(Color.BLACK);
		timeTip.setFont(new Font("宋体", Font.PLAIN, 15));

		JPanel soundLevelPanel = new JPanel();
		soundLevelPanel.setLayout(new GridLayout(0, 1, 1, 1));
		soundLevelLabelArray = new JPanel[levelSum];
		for (int i = 0; i < soundLevelLabelArray.length; ++i) {
			soundLevelLabelArray[i] = new JPanel();
			soundLevelLabelArray[i].setSize(25, 10);
			soundLevelLabelArray[i].setBackground(new Color(210, 210, 210));
			soundLevelPanel.add(soundLevelLabelArray[i]);
			soundLevelPanel.add(soundLevelLabelArray[i]);
		}
		soundLevelPanel.setBounds(100, 140, 25, 80);

		JPanel recordSoundControl = new JPanel();
		recordSoundControl.setLayout(new GridLayout(0, 3, 10, 10));
		recordSoundControl.setBounds(140, 175, 300, 100);
		recordSoundControl.setBackground(Color.WHITE);
		TitledBorder midDisplayBorder = BorderFactory.createTitledBorder("控制");
		recordSoundControl.setBorder(midDisplayBorder);

		// 模式选择
		switchType = new JPanel();
		switchType.setLayout(new GridLayout(0, 2, 10, 10));
		switchType.setBounds(170, 120, 200, 50);
		switchType.setBackground(Color.WHITE);
		switchType.setBorder(BorderFactory.createTitledBorder("模式选择"));

		ButtonGroup group = new ButtonGroup();
		recordSoundType = new JRadioButton("录音模式");
		playSoundType = new JRadioButton("播音模式");
		recordSoundType.setBackground(Color.white);
		playSoundType.setBackground(Color.white);
		recordSoundType.addActionListener(this);
		playSoundType.addActionListener(this);
		switchType.add(recordSoundType);
		switchType.add(playSoundType);
		group.add(recordSoundType);
		group.add(playSoundType);

		recordSoundType.setSelected(true);
		recordType = true;

		recordSound = addControlButton("开始录音", recordSoundControl);
		stopRecordSound = addControlButton("录音停止", recordSoundControl);
		saveRecordSound = addControlButton("保存录音", recordSoundControl);
		playBack = addControlButton("回放录音", recordSoundControl);
		stopPlayBack = addControlButton("回放停止", recordSoundControl);
		openFiled = addControlButton("打开音频", recordSoundControl);

		SetButtonEnabled(new Boolean[] { true, false, false, false, false,
				false, true });

		existRecordSound = new JButton("退出");
		existRecordSound.setBounds(330, 280, 90, 30);
		existRecordSound.addActionListener(this);

		panel.add(commentTitle);
		panel.add(scrollPane);
		panel.add(recordTip);
		panel.add(timeTip);
		panel.add(soundLevelPanel);
		panel.add(recordSoundControl);
		panel.add(existRecordSound);
		panel.add(switchType);

		this.setLayout(null);
		this.getContentPane().add(panel);
		this.setSize(460, 350);
		this.setLocation(new Point(screenSize.width / 2 - 460 / 2,
				screenSize.height / 2 - 350 / 2));

		showDateTimer = new Timer(10, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (startRecordFlag)
					recordShowDate += 10;
				else if (stopRecordFlag) {
					recordShowDate += 10;
					showDateTimer.stop();
				}
				int s = recordShowDate / 1000;
				int ms = (recordShowDate % 1000) / 10;
				String showDateString = new String();
				if (s < 10)
					showDateString = "0" + s + ":";
				else
					showDateString = s + ":";

				if (ms < 10)
					showDateString += "0" + ms;
				else
					showDateString += ms;

				timeTip.setText(showDateString);
			}
		});
		showDateTimer.setInitialDelay(0);
	}

	// show sound level as label
	public void ShowSoundLevel(int soundLevel) {
		int level = levelSum - soundLevel / 2;
		level = level > 1 ? level : 0;
		DebugTool.printLogDebug("level:" + level);

		for (int i = 0; i < level; ++i) {
			soundLevelLabelArray[i].setBackground(new Color(210, 210, 210));
		}
		for (int i = level; i < levelSum; i++) {
			soundLevelLabelArray[i].setBackground(Color.GREEN);
		}
	}

	public DoctorCommentDialog(Frame owner) {
		this(owner, false);
		// TODO Auto-generated constructor stub
	}

	private JButton addControlButton(String butString, JPanel panel) {
		JButton button = new JButton();
		button.setSize(Style.InfoButtonDimension);
		button.addActionListener(this);
		button.setText(butString);
		button.setPreferredSize(Style.InfoButtonDimension);
		button.setBackground(Color.WHITE);
		panel.add(button);
		return button;
	}

	public static void main(String[] args) {
		DoctorCommentDialog doctorCommentDialog = new DoctorCommentDialog(null,
				true);
		doctorCommentDialog.setVisible(true);
	}

	private void SetButtonEnabled(Boolean enableArr[]) {
		if (enableArr[0] != null)
			recordSound.setEnabled(enableArr[0] && recordType);
		if (enableArr[0] != null)
			stopRecordSound.setEnabled(enableArr[1] && recordType);
		if (enableArr[0] != null)
			saveRecordSound.setEnabled(enableArr[2] && recordType);
		if (enableArr[0] != null)
			playBack.setEnabled(enableArr[3]);
		if (enableArr[0] != null)
			stopPlayBack.setEnabled(enableArr[4]);
		if (enableArr[0] != null)
			openFiled.setEnabled(enableArr[5]);
		if (enableArr[0] != null)
			recordSoundType.setEnabled(enableArr[6]);
		if (enableArr[0] != null)
			playSoundType.setEnabled(enableArr[6]);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == recordSound) {
			// 开始录音
			if (stopRecordFlag == true) {
				// show warnning dialog
				if (saveFlagFlag == false) {
					int option = JOptionPane.showConfirmDialog(this,
							"录音未保存,请先保存录音,想要放弃刚才录音请按\"确定\"", "警告",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
						saveFlagFlag = true;
					} else
						return;
				}
				stopRecordFlag = false;
				startRecordFlag = true;
				pauseRecordFlag = false;
				recordTip.setText("正在录音....");
				recordSound.setText("暂停录音");

				recordPlayer.StartRecord();

				saveFlagFlag = false;
				SetButtonEnabled(new Boolean[] { true, true, false, false,
						false, !recordType, false });

				recordShowDate = 0;
				showDateTimer.start();
			}
			// 暂停录音
			else if (startRecordFlag == true && pauseRecordFlag == false) {
				startRecordFlag = false;
				pauseRecordFlag = true;
				recordTip.setText("暂停录音....");
				recordSound.setText("恢复录音");

				ShowSoundLevel(100);
				recordPlayer.PauseRecord();
				SetButtonEnabled(new Boolean[] { true, true, false, true,
						false, !recordType, false });
			}
			// 恢复录音
			else if (startRecordFlag == false && pauseRecordFlag == true) {
				startRecordFlag = true;
				pauseRecordFlag = false;
				recordTip.setText("正在录音....");
				recordSound.setText("暂停录音");

				recordPlayer.ResumRecord();
				SetButtonEnabled(new Boolean[] { true, true, false, false,
						false, !recordType, false });
			}
		}
		// 停止录音
		else if (e.getSource() == stopRecordSound) {
			startRecordFlag = false;
			pauseRecordFlag = false;
			stopRecordFlag = true;
			recordTip.setText("录音停止....");
			recordSound.setText("开始录音");

			recordPlayer.StopRecord();
			SetButtonEnabled(new Boolean[] { true, false, true, true, false,
					!recordType, true });
		}

		else if (e.getSource() == existRecordSound) {
			closeDialog();
		}

		else if (e.getSource() == saveRecordSound) {
			JFileChooser file = new SaveFileChooser(".", "保存录音文件");
			int result = file.showSaveDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				String saveFilePath = file.getSelectedFile().getAbsolutePath()
						+ "(" + commentField.getText() + ")" + ".mp3";
				recordPlayer.SaveRecordSound(saveFilePath);
				saveFlagFlag = true;

				DebugTool.printLogDebug("save");
			}
		}

		else if (e.getSource() == playBack) {
			// 开始回放
			if (stopPlayBackFlag == true) {
				startPlayBackFlag = true;
				pausePlayBackFlag = false;
				stopPlayBackFlag = false;
				playBack.setText("暂停回放");
				recordTip.setText("正在回放....");
				recordPlayer.StartPlay();
			}
			// 暂停回放
			else if (startPlayBackFlag == true) {
				startPlayBackFlag = false;
				pausePlayBackFlag = true;
				stopPlayBackFlag = false;
				playBack.setText("继续回放");
				recordTip.setText("暂停回放....");
				recordPlayer.PausePlay();
			}
			// 继续回放
			else if (pausePlayBackFlag == true) {
				startPlayBackFlag = true;
				pausePlayBackFlag = false;
				stopPlayBackFlag = false;
				playBack.setText("暂停回放");
				recordTip.setText("正在回放....");
				recordPlayer.ResumPlay();
			}
			SetButtonEnabled(new Boolean[] { false, false, false, true, true,
					!recordType, false });
		}

		else if (e.getSource() == stopPlayBack) {
			startPlayBackFlag = false;
			pausePlayBackFlag = false;
			stopPlayBackFlag = true;
			playBack.setText("回放录音");
			recordTip.setText("回放停止....");
			SetButtonEnabled(new Boolean[] { true, stopRecordFlag != true,
					stopRecordFlag == true, true, false, !recordType,
					stopRecordFlag == true });
			recordPlayer.StopPlay();
		}

		else if (e.getSource() == openFiled) {
			if (saveFlagFlag == false) {
				int option = JOptionPane.showConfirmDialog(this,
						"录音未保存,请先保存录音,想要放弃刚才录音请按\"确定\"", "警告",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.OK_OPTION) {
					saveFlagFlag = true;
				} else
					return;
			}
			JFileChooser file = new SaveFileChooser(".", "打开录音文件");
			int result = file.showOpenDialog(this);

			if (result == JFileChooser.OPEN_DIALOG) {
				String saveFilePath = file.getSelectedFile().getAbsolutePath();
				File fileExsit = new File(saveFilePath);
				if (!fileExsit.exists()) {
					JOptionPane.showMessageDialog(DoctorCommentDialog.this,
							"文件不存在请重新选择");
				} else {
					try {
						recordPlayer.ReadSoundFile(saveFilePath);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					SetButtonEnabled(new Boolean[] { false, false, false, true,
							false, !recordType, true });
				}
			}
		}

		else if (e.getSource() == recordSoundType) {
			recordType = true;
			SetButtonEnabled(new Boolean[] { true, false, false, false, false,
					!recordType, true });
		}

		else if (e.getSource() == playSoundType) {
			if (saveFlagFlag == false) {
				int option = JOptionPane.showConfirmDialog(this,
						"录音未保存,请先保存录音,想要放弃刚才录音请按\"确定\"", "警告",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.OK_OPTION) {
					saveFlagFlag = true;
					recordType = false;
					SetButtonEnabled(new Boolean[] { false, false, false,
							false, false, !recordType, true });
				} else
					recordSoundType.setSelected(true);
			}
			else{
				recordType = false;
				SetButtonEnabled(new Boolean[] { false, false, false,
						false, false, !recordType, true });
			}
		}
	}

	/*
	 * 关闭对话框
	 */
	public void closeDialog() {
		if (startRecordFlag || pauseRecordFlag)
			recordPlayer.StopRecord();
		if (startPlayBackFlag || pausePlayBackFlag)
			recordPlayer.StopPlay();
		this.dispose();
	}

	public void PlayBackEnd() {
		startPlayBackFlag = false;
		pausePlayBackFlag = false;
		stopPlayBackFlag = true;
		playBack.setText("回放录音");
		recordTip.setText("回放结束....");
		SetButtonEnabled(new Boolean[] { true, stopRecordFlag != true,
				stopRecordFlag == true, true, false, !recordType,
				stopRecordFlag == true });
	}

	/*
	 * JFileChooser class and renew approveSelection funtion
	 */
	class SaveFileChooser extends JFileChooser {
		public SaveFileChooser(String dialogTitle) {
			this(".", dialogTitle);
		}

		public SaveFileChooser(String path, String dialogTitle) {
			super(path);
			this.setDialogTitle(dialogTitle);
		}

		public void approveSelection() {
			if (getDialogType() == JFileChooser.SAVE_DIALOG) {
				File file = this.getSelectedFile();
				if (file.exists()) {
					int copy = JOptionPane.showConfirmDialog(null,
							"是否要覆盖当前文件？", "保存", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (copy == JOptionPane.YES_OPTION)
						super.approveSelection();
					else
						return;
				}
			}
			super.approveSelection();
		}

		/*
		 * FileFilter
		 */
		private class WAVFileFilter extends FileFilter {

			String ext;

			public WAVFileFilter(String ext) {
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
				return "WAV 文件(*.wav)";
			}
		}
	}
}
