package ui.medlinx.com.frame.Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import ui.medlinx.com.dialog.AddPatientDialog;
import ui.medlinx.com.dialog.ChangePBindDDialog;
import ui.medlinx.com.dialog.ModifyPatientDialog;
import ui.medlinx.com.dialog.SearchPatientListDialog;
import ui.medlinx.com.dialog.WaitingTipDialog;
import ui.medlinx.com.extra.SettingParameters;
import ui.medlinx.com.frame.ECGhistory.HistoryFrame;

import com.medlinx.core.client.MlnxDoctorClient;
import com.medlinx.core.patient.DeviceInfo;
import com.medlinx.core.patient.Patient;
import com.medlinx.core.patient.PatientList;
import com.medlinx.core.patient.UsrData;
import com.mlnx.pms.core.Group;
import com.mlnx.pms.core.Patient.Gender;
import com.mlnx.pms.core.Device;
import com.mlnx.pms.core.PatientGroup;
import com.mlnx.pms.core.User;

public class PatientManagePanel extends JSplitPane implements ItemListener {

	private static final double UsrLocation = 0.2;
	private static final double GroupLocation = 0.6;
	private static final double PatientGroupLocation = 0.8;
	private static final String GROUPFIELDS_STRINGS[] = {"群组名", "群组类型", "病人数量"};
	private static final String PATIENTFIELDS_STRINGS[] = {"病人ID", "病人状态",
			"病人名字", "病人性别", "设备信息", "监护信息"};
	private static final String[] PATIENTSTATE_STRINGS = {"全部病人", "监护病人",
			"在线未监护病人", "在线病人"};
	private static final String[] PATIENTSEX_STRINGS = {"全部", "男", "女"};
	private static final Color TitleColor = Color.BLUE;

	private MLnxClient mLnxClient;
	private JSplitPane groupPanel, patientGroupPane;
	private JTable groupTable, patientGroup;
	private String showTip;
	// 病人第一行筛选列表
	private JComboBox<String> patientStateComboBox, patientSexComboBox; // 病人状态筛选
	private JTextField searchPIDLabel, searchPNameLabel; // 病人名字筛选
	private int selectPatientStateIndex = 0, selectPatientSexIndex = 0;

	// change group
	private JLabel changeGroupIDLabel;
	private JTextField changeGroupNameTextField, changeGroupTypeTextField;
	private JButton changeGroupButton, deleteGroupButton;
	// register group
	private JTextField registerGroupNameTextField, registerGroupTypeTextField;
	private JButton registerGroupButton;
	// search patient
	private JTextField searchPIDTextField, searchPPTextField,
			searchPITextField;
	private JButton serchPatientButton;
	// change patient

	private JButton registerPButton, changePButton, deletePButton,
			changePStateButton, realECGButton, historyECGButton, changeDeice;

	private int groupTableSelection = -1;
	private int patientTableSelection = -1;

	private List<Group> groupList; // group列表
	private Group selectGroup;
	private Map<Long, PatientList> patientGroupMap; // 不同group的病人列表
	// private Map<Long, HashMap<Integer, Patient>>
	// recordSelectedOnlinePatients;
	private PatientList selectPatientList;
	private Patient[] selectPatients;
	private Patient selectPatient;
	private List<com.mlnx.pms.core.Patient> searchPatients;
	private List<Device> devices;

	public PatientManagePanel(MLnxClient mLnxClient) {
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		this.mLnxClient = mLnxClient;
		initData();
		addComponent();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				PatientManagePanel.this.setDividerLocation(UsrLocation);
				PatientManagePanel.this.groupPanel
						.setDividerLocation(GroupLocation);
				PatientManagePanel.this.patientGroupPane
						.setDividerLocation(PatientGroupLocation);
			}
		});

	}

	public List<Group> getGroupList() {
		return groupList;
	}

	public Map<Long, PatientList> getPatientGroupMap() {
		return patientGroupMap;
	}

	private void initData() {
		groupList = new ArrayList<Group>();
		patientGroupMap = new HashMap<Long, PatientList>();
	}

	/*
	 * 添加组件
	 */
	private void addComponent() {

		// // left
		addGroupPanel();
		// right
		addPatientPanle();
	}

	// GroupPanel
	private void addGroupPanel() {
		groupPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		this.setLeftComponent(groupPanel);

		// 上方
		GroupTableModel groupTableModel = new GroupTableModel();
		groupTable = new JTable(groupTableModel);
		groupTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				setSelectGroup(groupTable.getSelectedRow());
			}
		});

		// 设置表格高度
		groupTable.setRowHeight(40);

		// 设置字体
		JTableHeader tableHeader = groupTable.getTableHeader();
		tableHeader.setFont(new Font("宋体", 0, 15));
		groupTable.setFont(new Font("宋体", Font.PLAIN, 13));

		// 居中显示
		DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.CENTER);
		groupTable.setDefaultRenderer(Object.class, cr);

		GroupCellRenderer groupCellRenderer = new GroupCellRenderer();
		for (int i = 0; i < GROUPFIELDS_STRINGS.length; ++i) {
			groupTable.getColumn(GROUPFIELDS_STRINGS[i]).setCellRenderer(
					groupCellRenderer);
		}

		TitledBorder titledBorder = BorderFactory.createTitledBorder("群组列表");
		titledBorder.setTitleColor(TitleColor);
		titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
		JScrollPane scrollPane = new JScrollPane(groupTable);
		scrollPane.setBorder(titledBorder);
		groupPanel.setLeftComponent(scrollPane);

		// 下方
		JPanel registerGroupPanel = new JPanel();
		scrollPane = new JScrollPane(registerGroupPanel);
		groupPanel.setRightComponent(scrollPane);

		registerGroupPanel.setLayout(new GridLayout(2, 1));
		{
			titledBorder = BorderFactory.createTitledBorder("修改群组信息");
			titledBorder.setTitleColor(TitleColor);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(titledBorder);
			registerGroupPanel.add(panel);
			{
				JPanel panel2 = new JPanel(new GridLayout(2, 3));
				changeGroupIDLabel = new JLabel("未选择");
				changeGroupNameTextField = new JTextField("未选择");
				changeGroupTypeTextField = new JTextField("未选择");
				panel2.add(new JLabel("群组ID"));
				panel2.add(new JLabel("群组名"));
				panel2.add(new JLabel("群组类型"));
				panel2.add(changeGroupIDLabel);
				panel2.add(changeGroupNameTextField);
				panel2.add(changeGroupTypeTextField);
				panel.add(panel2);
			}
			{
				JPanel panel2 = new JPanel(new GridLayout(1, 2));
				panel.add(panel2);
				changeGroupButton = new JButton("修改群组信息");
				panel2.add(changeGroupButton);
				deleteGroupButton = new JButton("删除群组");
				panel2.add(deleteGroupButton);
				deleteGroupButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						User loginUser = SettingParameters.getInstance()
								.getLoginUser();
						if (loginUser == null) {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, "请先登入");
							return;
						}
						if (changeGroupIDLabel.getText().equals("未选择")) {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, "请选择要删除的群组");
							return;
						}
						String groupId = changeGroupIDLabel.getText();
						Group deleteGroup = new Group();
						deleteGroup.setGroupId(Long.valueOf(groupId));
						deleteGroup.setUserId(loginUser.getId());
						PatientManageSwingWorker swingWorker = new PatientManageSwingWorker();
						swingWorker.deleteGroup(deleteGroup);
					}
				});
			}
		}
		{
			titledBorder = BorderFactory.createTitledBorder("添加群组");
			titledBorder.setTitleColor(TitleColor);
			titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(titledBorder);
			registerGroupPanel.add(panel);
			{
				JPanel panel2 = new JPanel(new GridLayout(2, 2));
				registerGroupNameTextField = new JTextField();
				registerGroupTypeTextField = new JTextField();
				panel2.add(new JLabel("群组名"));
				panel2.add(new JLabel("群组类型"));
				panel2.add(registerGroupNameTextField);
				panel2.add(registerGroupTypeTextField);
				panel.add(panel2);
			}
			{
				JPanel panel2 = new JPanel(new GridLayout(1, 1));
				registerGroupButton = new JButton("添加群组");
				registerGroupButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String groupName = registerGroupNameTextField.getText();
						String groupType = registerGroupTypeTextField.getText();
						if (groupName.isEmpty()) {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, "群组名不能为空");
							return;
						} else if (groupType.isEmpty()) {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, "群组类型不能为空");
							return;
						} else {
							User loginUser = SettingParameters.getInstance()
									.getLoginUser();
							if (loginUser == null) {
								JOptionPane.showMessageDialog(
										PatientManagePanel.this, "请先登入");
								return;
							}
							Group addGroup = new Group();
							addGroup.setName(groupName);
							addGroup.setType(groupType);
							addGroup.setUserId(loginUser.getId());
							PatientManageSwingWorker swingWorker = new PatientManageSwingWorker();
							swingWorker.buildGroup(addGroup);
						}
					}
				});
				panel2.add(registerGroupButton);
				panel.add(panel2);
			}
		}
	}

	// PatientPanle
	private void addPatientPanle() {
		// 上方
		{
			patientGroupPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
			this.setRightComponent(patientGroupPane);
			// 病人列表
			{
				// 上方
				PatientTableModel patientTableModel = new PatientTableModel();
				patientGroup = new JTable(patientTableModel);
				patientGroup
						.addMouseListener(new java.awt.event.MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								super.mouseClicked(e);
								setSelectPatient(patientGroup.getSelectedRow());
							}
						});

				// 设置表格高度
				patientGroup.setRowHeight(40);

				// 设置字体
				patientGroup.getTableHeader().setFont(new Font("宋体", 0, 15));
				patientGroup.setFont(new Font("宋体", Font.PLAIN, 13));

				// 添加列表效果
				PatientCellRenderer patientCellRenderer = new PatientCellRenderer();
				PatientCellEditor patientCellEditor = new PatientCellEditor();
				for (int i = 0; i < PATIENTFIELDS_STRINGS.length; ++i) {
					patientGroup.getColumn(PATIENTFIELDS_STRINGS[i])
							.setCellRenderer(patientCellRenderer);
					patientGroup.getColumn(PATIENTFIELDS_STRINGS[i])
							.setCellEditor(patientCellEditor);
				}

				// 居中显示
				DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
				cr.setHorizontalAlignment(JLabel.CENTER);
				patientGroup.setDefaultRenderer(Object.class, cr);

				JScrollPane scrollPane = new JScrollPane(patientGroup);
				TitledBorder titledBorder = BorderFactory
						.createTitledBorder("病人列表");
				titledBorder.setTitleColor(TitleColor);
				titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
				scrollPane.setBorder(titledBorder);
				patientGroupPane.setLeftComponent(scrollPane);
			}

			// 病人操作
			{
				JPanel panel2 = new JPanel(new GridLayout(0, 2, 5, 0));
				patientGroupPane.setRightComponent(panel2);
				// 搜索病人
				{
					TitledBorder titledBorder = BorderFactory
							.createTitledBorder("病人搜索");
					titledBorder.setTitleColor(TitleColor);
					titledBorder.setTitleFont(new Font("宋体", Font.PLAIN, 20));
					JPanel searchPatientPanel = new JPanel();
					searchPatientPanel.setBorder(titledBorder);

					panel2.add(searchPatientPanel);
					searchPatientPanel.setLayout(new GridLayout(0, 2));
					JPanel panel3 = new JPanel(new GridLayout(3, 2));
					searchPatientPanel.add(panel3);
					{

						JLabel label = new JLabel("输入病人ID:");
						panel3.add(label);
						searchPIDTextField = new JTextField();
						panel3.add(searchPIDTextField);

						label = new JLabel("输入病人手机号:");
						panel3.add(label);
						searchPPTextField = new JTextField();
						panel3.add(searchPPTextField);

						label = new JLabel("输入病人身份证号:");
						panel3.add(label);
						searchPITextField = new JTextField();
						panel3.add(searchPITextField);

						serchPatientButton = new JButton("搜索病人");
						registerPButton = new JButton("注册病人并添加到群组");
						JPanel panel4 = new JPanel(new GridLayout(2, 1));
						panel4.add(serchPatientButton);
						panel4.add(registerPButton);
						searchPatientPanel.add(panel4);
						serchPatientButton
								.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										User loginUser = SettingParameters
												.getInstance().getLoginUser();
										if (loginUser == null) {
											JOptionPane.showMessageDialog(
													PatientManagePanel.this,
													"请先登入");
											return;
										}
										com.mlnx.pms.core.Patient patient = new com.mlnx.pms.core.Patient();

										if (!searchPIDTextField.getText()
												.isEmpty())
											patient.setId(Integer
													.parseInt(searchPIDTextField
															.getText()));
										if (!searchPPTextField.getText()
												.isEmpty())
											patient.setContact(searchPPTextField
													.getText());
										if (!searchPITextField.getText()
												.isEmpty())
											patient.setLastFourNumber(searchPITextField
													.getText());
										if (searchPIDTextField.getText()
												.isEmpty()
												&& searchPPTextField.getText()
														.isEmpty()
												&& searchPITextField.getText()
														.isEmpty()) {
											JOptionPane.showMessageDialog(
													PatientManagePanel.this,
													"请输入要搜索病人的ID或手机号或身份证号");
											return;
										}

										PatientManageSwingWorker swingWorker = new PatientManageSwingWorker();
										swingWorker.searchPatient(patient);
									}
								});

						// 注册病人
						registerPButton.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								User loginUser = SettingParameters
										.getInstance().getLoginUser();
								if (loginUser == null) {
									JOptionPane.showMessageDialog(
											PatientManagePanel.this, "请先登入");
									return;
								}
								if (selectGroup == null) {
									JOptionPane.showMessageDialog(
											PatientManagePanel.this, "请先选择群组");
									return;
								}
								if (loginUser != null && selectGroup != null) {
									AddPatientDialog addPatient = new AddPatientDialog(
											PatientManagePanel.this);
									addPatient.setVisible(true);
								}
							}
						});
					}

				}

				// 病人信息panel
				{
					{
						TitledBorder titledBorder = BorderFactory
								.createTitledBorder("病人信息");
						titledBorder.setTitleColor(TitleColor);
						titledBorder
								.setTitleFont(new Font("宋体", Font.PLAIN, 20));
						JPanel changePatientPanel = new JPanel();
						changePatientPanel.setBorder(titledBorder);

						panel2.add(changePatientPanel);
						changePatientPanel.setLayout(new BoxLayout(
								changePatientPanel, BoxLayout.Y_AXIS));

						{
							JPanel panel = new JPanel(
									new GridLayout(2, 3, 2, 2));

							// 修改病人信息
							changePButton = new JButton("病人信息");
							changePButton
									.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent e) {
											User loginUser = SettingParameters
													.getInstance()
													.getLoginUser();
											if (loginUser == null) {
												JOptionPane
														.showMessageDialog(
																PatientManagePanel.this,
																"请先登入");
												return;
											}
											if (selectPatient == null) {
												JOptionPane
														.showMessageDialog(
																PatientManagePanel.this,
																"请先选择病人");
												return;
											}
											if (loginUser != null
													&& selectPatient != null) {
												setSelectPatient(patientGroup
														.getSelectedRow());
												com.mlnx.pms.core.Patient patient = new com.mlnx.pms.core.Patient();

												patient.setId(selectPatient
														.getPatientID());
												patient.setName(selectPatient
														.getPatientName());
												patient.setAge(selectPatient
														.getPatientAge());
												patient.setContact(selectPatient
														.getPatientInfo()
														.getContact());
												patient.setLastFourNumber(selectPatient
														.getPatientInfo()
														.getLastFourNumber());
												patient.setGender(selectPatient
														.getPatientInfo()
														.getGender());
												patient.setPastMedicalHistory(selectPatient
														.getPatientInfo()
														.getPastMedicalHistory());
												patient.setRemark(selectPatient
														.getPatientInfo()
														.getRemark());
												ModifyPatientDialog modifyPatientDialog = new ModifyPatientDialog(
														PatientManagePanel.this,
														patient);
												modifyPatientDialog
														.setVisible(true);
											}
										}
									});

							// 删除病人
							deletePButton = new JButton("删除病人");
							deletePButton
									.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent e) {
											User loginUser = SettingParameters
													.getInstance()
													.getLoginUser();
											if (loginUser == null) {
												JOptionPane
														.showMessageDialog(
																PatientManagePanel.this,
																"请先登入");
												return;
											}
											if (selectPatient == null) {
												JOptionPane
														.showMessageDialog(
																PatientManagePanel.this,
																"请先选择病人");
												return;
											}
											PatientGroup patientGroup = new PatientGroup();
											patientGroup.setGroupId(selectGroup
													.getGroupId());
											patientGroup
													.setPatientId(selectPatient
															.getPatientID());
											PatientManageSwingWorker swingWorker = new PatientManageSwingWorker();
											swingWorker
													.deletePatientGroup(patientGroup);
										}
									});

							// 监控病人
							changePStateButton = new JButton("监控病人");
							changePStateButton.setEnabled(false);
							changePStateButton
									.addActionListener(new PatientStateActionListener());

							// 实时心电
							realECGButton = new JButton("实时心电");
							realECGButton.setEnabled(false);

							// 历史心电
							historyECGButton = new JButton("历史心电");
							historyECGButton.setEnabled(false);
							realECGButton
									.addActionListener(new RHECGActionListener(
											true));
							historyECGButton
									.addActionListener(new RHECGActionListener(
											false));

							// 修改设备信息
							changeDeice = new JButton("修改绑定设备");
							changeDeice.setEnabled(false);
							changeDeice.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									if (selectPatient == null) {
										JOptionPane.showMessageDialog(
												PatientManagePanel.this,
												"请先选择需要监控的病人");
										return;
									}

									new ChangePBindDDialog(
											PatientManagePanel.this,
											selectPatient.getPatientID(),
											devices.toArray(new Device[0]));

								}
							});
							panel.add(changePStateButton);
							panel.add(realECGButton);
							panel.add(historyECGButton);
							panel.add(changePButton);
							panel.add(deletePButton);
							panel.add(changeDeice);
							changePatientPanel.add(panel);
						}
					}
					patientGroupPane.setRightComponent(panel2);
				}
			}
		}
	}
	// JComboBox 筛选监听
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			String itemName = (String) e.getItem();
			// 病人状态
			if (e.getSource().equals(patientStateComboBox)) {
				for (int i = 0; i < PATIENTSTATE_STRINGS.length; i++) {
					if (PATIENTSTATE_STRINGS[i].equals(itemName)) {
						selectPatientStateIndex = i;
					}
				}
				patientScreen();
			}
			// 性别
			else if (e.getSource().equals(patientSexComboBox)) {
				for (int i = 0; i < PATIENTSEX_STRINGS.length; i++) {
					if (PATIENTSEX_STRINGS[i].equals(itemName)) {
						selectPatientSexIndex = i;
					}
				}
				patientScreen();
			}
		}
	}

	/*
	 * 病人筛选
	 */
	private void patientScreen() {
		// 病人状态
		List<Patient> patients = new ArrayList<Patient>();
		if (selectPatientList == null) {
			selectPatients = patients.toArray(new Patient[0]);
			patientTableSelection = -1;
			groupTable.updateUI();
			patientGroup.updateUI();
			return;
		}
		switch (selectPatientStateIndex) {
			case 0 :
				patients.addAll(selectPatientList.getPatients().values());
				break;
			case 1 :
				patients.addAll(selectPatientList.getSelectedOnlinePatients()
						.values());
				break;
			case 2 :
				patients.addAll(selectPatientList.getUnSelectedOnlinePatients()
						.values());
				break;
			case 3 :
				patients.addAll(selectPatientList.getOnlinePatients().values());
				break;
		}
		// 性别
		if (selectPatientSexIndex > 0) {
			Gender gender;
			if (selectPatientSexIndex == 1)
				gender = Gender.MALE;
			else
				gender = Gender.FEMALE;
			for (Iterator<Patient> iterator = patients.iterator(); iterator
					.hasNext();) {
				Patient patient = (Patient) iterator.next();
				if (!patient.getGender().equals(gender.toString()))
					iterator.remove();
			}
		}
		// 筛选病人ID 姓名
		String searchPID = searchPIDLabel.getText();
		if (!searchPID.isEmpty()) {
			for (Iterator<Patient> iterator = patients.iterator(); iterator
					.hasNext();) {
				Patient patient = (Patient) iterator.next();
				if (!(searchPID.equals((patient.getPatientID() + "").substring(
						0, searchPID.length()))))
					iterator.remove();
			}
		}
		// 筛选名字
		String searchPName = searchPNameLabel.getText();
		if (!searchPName.isEmpty()) {
			List<Patient> fPatients = new ArrayList<Patient>();
			List<Patient> sPatients = new ArrayList<Patient>();
			List<Patient> tPatients = new ArrayList<Patient>();
			List<Patient> fourPatients = new ArrayList<Patient>();
			for (Iterator<Patient> iterator = patients.iterator(); iterator
					.hasNext();) {
				Patient patient = (Patient) iterator.next();
				if (patient.getPatientSort()
						.NameAsInputSearchFrist(searchPName))
					fPatients.add(patient);
				else if (patient.getPatientSort().NameAsInputSearchSecond(
						searchPName))
					sPatients.add(patient);
				else if (patient.getPatientSort().NameAsInputSearchThird(
						searchPName))
					tPatients.add(patient);
				else if (patient.getPatientSort().NameAsInputSearchFourth(
						searchPName))
					fourPatients.add(patient);
			}
			patients = new ArrayList<Patient>();
			patients.addAll(fPatients);
			patients.addAll(sPatients);
			patients.addAll(tPatients);
			patients.addAll(fourPatients);
		}
		selectPatients = patients.toArray(new Patient[0]);
		patientTableSelection = -1;
		if (selectPatient != null) {
			for (int i = 0; i < selectPatients.length; i++) {
				if (selectPatient.getPatientID() == selectPatients[i]
						.getPatientID()) {
					patientTableSelection = i + 1;
					break;
				}
			}
			// set change p state button
			historyECGButton.setEnabled(true);
			changeDeice.setEnabled(true);
			if (selectPatient.isOnline() && selectPatient.isSelected()) {
				changePStateButton.setText("取消监控");
				changePStateButton.setEnabled(true);
				realECGButton.setEnabled(true);
			} else if (selectPatient.isOnline()) {
				changePStateButton.setText("监控病人");
				changePStateButton.setEnabled(true);
				realECGButton.setEnabled(false);
			} else {
				changePStateButton.setText("监控病人");
				changePStateButton.setEnabled(false);
				realECGButton.setEnabled(false);
			}
		}

		groupTable.updateUI();
		patientGroup.updateUI();
	}

	// 筛选监听
	class InputID_NameListener implements KeyListener {

		private void process(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				patientScreen();
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			process(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	// 搜索 MouseListener
	public class SearchMouseListener extends MouseAdapter {
		private int patientID;
		private SearchPatientListDialog searchPatientListDialog;

		public SearchMouseListener(int patientID,
				SearchPatientListDialog searchPatientListDialog) {
			super();
			this.patientID = patientID;
			this.searchPatientListDialog = searchPatientListDialog;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (groupTableSelection == -1) {
				JOptionPane
						.showMessageDialog(PatientManagePanel.this, "请先选择群组");
				return;
			}
			PatientGroup patientGroup = new PatientGroup();
			patientGroup.setGroupId(groupList.get(groupTableSelection)
					.getGroupId());
			patientGroup.setPatientId(patientID);
			PatientManageSwingWorker swingWorker = new PatientManageSwingWorker();
			swingWorker.addPatientGroup(patientGroup);
			this.searchPatientListDialog.dispose();

		}
	}

	/**
	 * 设置被选择的group
	 */
	private void setSelectGroup(int groupTableSelection) {
		if (groupTableSelection == -1 && groupList.size() > 0)
			groupTableSelection = 0;
		else if (groupTableSelection == -1 && groupList.size() == 0)
			return;
		else if (groupTableSelection >= groupList.size()
				&& groupList.size() > 0)
			groupTableSelection--;

		Group sGroup = groupList.get(groupTableSelection);
		// 初始化选择的病人
		if (selectGroup != null
				&& !sGroup.getGroupId().equals(selectGroup.getGroupId())) {
			selectPatient = null;
			patientTableSelection = -1;

		}

		this.groupTableSelection = groupTableSelection;
		selectGroup = groupList.get(groupTableSelection);
		changeGroupIDLabel.setText(selectGroup.getGroupId() + "");
		changeGroupNameTextField.setText(selectGroup.getName() + "");
		changeGroupTypeTextField.setText(selectGroup.getType() + "");
		selectPatientList = patientGroupMap.get(selectGroup.getGroupId());

		patientScreen();
	}

	/**
	 * 设置被选择的Patient
	 */
	private void setSelectPatient(int patientTableSelection) {
		if (patientTableSelection == 0 || patientTableSelection == -1)
			return;
		this.patientTableSelection = patientTableSelection;
		selectPatient = selectPatients[patientTableSelection - 1];
		historyECGButton.setEnabled(true);
		changeDeice.setEnabled(true);
		if (selectPatient.isOnline() && selectPatient.isSelected()) {
			changePStateButton.setText("取消监控");
			changePStateButton.setEnabled(true);
			realECGButton.setEnabled(true);
		} else if (selectPatient.isOnline()) {
			changePStateButton.setText("监控病人");
			changePStateButton.setEnabled(true);
			realECGButton.setEnabled(false);
		} else {
			changePStateButton.setText("监控病人");
			changePStateButton.setEnabled(false);
			realECGButton.setEnabled(false);
		}
		patientGroupPane.updateUI();
	}

	// 群组表
	class GroupTableModel extends AbstractTableModel {
		public GroupTableModel() {
		}

		@Override
		public int getColumnCount() {
			return GROUPFIELDS_STRINGS.length;
		}

		@Override
		public int getRowCount() {
			return groupList.size();
		}

		@Override
		public String getColumnName(int column) {
			return GROUPFIELDS_STRINGS[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			// "群组ID", "群组名", "群组类型","病人数量", "操作病人"
			Group group = groupList.get(rowIndex);
			switch (columnIndex) {
				case 0 :
					return group.getName();
				case 1 :
					return group.getType();
				case 2 :
					return group.getCount();
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

		// // 可以编辑
		// @Override
		// public boolean isCellEditable(int rowIndex, int columnIndex) {
		// // TODO Auto-generated method stub
		// return true;
		// }
		//
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}
	}

	// 群组表效果
	class GroupCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JPanel panel = new JPanel(new GridLayout(1, 1));
			JLabel label = new JLabel("", JLabel.CENTER);
			panel.add(label);
			label.setText(value == null ? "" : String.valueOf(value));

			if (groupTableSelection != -1 && groupTableSelection == row) {
				panel.setBackground(Color.GREEN);
			}
			label.setForeground(Color.BLUE);
			return panel;
		}

	}

	// 病人表
	class PatientTableModel extends AbstractTableModel {
		public PatientTableModel() {
		}

		@Override
		public int getColumnCount() {
			return PATIENTFIELDS_STRINGS.length;
		}

		@Override
		public int getRowCount() {
			if (selectPatientList == null)
				return 1;
			return selectPatients.length + 1;
		}

		@Override
		public String getColumnName(int column) {
			return PATIENTFIELDS_STRINGS[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

		// 可以编辑
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (rowIndex == 0) {
				switch (columnIndex) {
					case 0 :
						return true;
					case 1 :
						return true;
					case 2 :
						return true;
					case 3 :
						return true;
				}
				return false;
			} else {
				return false;
			}
		}

		// @Override
		// public Class<?> getColumnClass(int columnIndex) {
		// return getValueAt(0, columnIndex).getClass();
		// }
	}

	// 病人表效果
	class PatientCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JPanel panel = new JPanel(new GridLayout(1, 1));
			if (row == 0) {
				switch (column) {
					case 0 :
						if (searchPIDLabel == null) {
							searchPIDLabel = new JTextField("",
									JTextField.CENTER);
							searchPIDLabel
									.addKeyListener(new InputID_NameListener());
						}
						panel.add(searchPIDLabel);
						break;
					case 1 :
						if (patientStateComboBox == null)
							patientStateComboBox = new JComboBox<String>(
									PATIENTSTATE_STRINGS);
						panel.add(patientStateComboBox);
						break;
					case 2 :
						if (searchPNameLabel == null) {
							searchPNameLabel = new JTextField("",
									JTextField.CENTER);
							searchPNameLabel
									.addKeyListener(new InputID_NameListener());
						}
						panel.add(searchPNameLabel);
						break;
					case 3 :
						if (patientSexComboBox == null)
							patientSexComboBox = new JComboBox<String>(
									PATIENTSEX_STRINGS);
						panel.add(patientSexComboBox);
						break;
				}
			} else {
				if (patientTableSelection != -1 && patientTableSelection == row) {
					panel.setBackground(Color.GREEN);
				}
				// "病人ID", "病人状态","病人名字", "病人性别", "设备信息", "监护信息"
				if (row > 0) {
					Patient patient = selectPatients[row - 1];
					Color labelForgenColor = Color.BLACK;
					if (patient.isSelected() && patient.isOnline()) {
						labelForgenColor = Color.RED;
					} else if (patient.isOnline()) {
						labelForgenColor = Color.BLUE;
					}
					JLabel label = new JLabel("", JLabel.CENTER);
					label.setForeground(labelForgenColor);
					switch (column) {
						case 0 :
							label.setText(patient.getPatientID() + "");
							panel.add(label);
							break;
						case 1 :
							if (patient.isSelected() && patient.isOnline()) {
								label.setText(PATIENTSTATE_STRINGS[1]);
							} else if (patient.isOnline()) {
								label.setText(PATIENTSTATE_STRINGS[2]);
							} else {
								label.setText("离线病人");
							}
							panel.add(label);
							break;
						case 2 :
							label.setText(patient.getPatientName() + "");
							panel.add(label);
							break;
						case 3 :
							label.setText(patient.getGender().equals(
									Gender.MALE.toString()) ? "男" : "女");
							panel.add(label);
							break;
						case 4 :
							if (patient.getDevInfo() != null) {
								panel.setLayout(new GridLayout(2, 1));
								JLabel idLabel = new JLabel("设备ID: ");
								JLabel modeLabel = new JLabel("设备模式:");
								JLabel iidLabel = new JLabel(patient
										.getDevInfo().getDeviceID());
								JLabel mmodeLabel = new JLabel(patient
										.getDevInfo().getModeName());
								idLabel.setForeground(labelForgenColor);
								modeLabel.setForeground(labelForgenColor);
								mmodeLabel.setForeground(labelForgenColor);
								iidLabel.setForeground(labelForgenColor);

								JPanel panel2 = new JPanel();
								panel2.setBackground(panel.getBackground());
								panel.add(panel2);
								panel2.add(idLabel);
								panel2.add(iidLabel);
								panel.add(panel2);
								panel2 = new JPanel();
								panel2.setBackground(panel.getBackground());
								panel2.add(modeLabel);
								panel2.add(mmodeLabel);
								panel.add(panel2);
							} else {
								label.setText("未绑定设备");
								panel.add(label);
							}
							break;
					}
				}
			}
			return panel;
		}
	}

	class PatientCellEditor extends DefaultCellEditor {

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -6546334664166791132L;

		public PatientCellEditor() {
			// DefautlCellEditor有此构造器，需要传入一个，但这个不会使用到，直接new一个即可。
			super(new JTextField());
			this.setClickCountToStart(0);
		}

		/**
		 * 这里重写父类的编辑方法，返回一个JPanel对象即可（也可以直接返回一个Button对象，但是那样会填充满整个单元格）
		 */
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			JPanel panel = new JPanel(new GridLayout(1, 1));
			if (row == 0) {
				switch (column) {
					case 0 :
						if (searchPIDLabel == null)
							searchPIDLabel = new JTextField("",
									JTextField.CENTER);
						panel.add(searchPIDLabel);
						break;
					case 1 :
						if (patientStateComboBox == null)
							patientStateComboBox = new JComboBox<String>(
									PATIENTSTATE_STRINGS);
						patientStateComboBox
								.addItemListener(PatientManagePanel.this);
						panel.add(patientStateComboBox);
						break;
					case 2 :
						if (searchPNameLabel == null)
							searchPNameLabel = new JTextField("",
									JTextField.CENTER);
						panel.add(searchPNameLabel);
						break;
					case 3 :
						if (patientSexComboBox == null)
							patientSexComboBox = new JComboBox<String>(
									PATIENTSEX_STRINGS);
						patientSexComboBox
								.addItemListener(PatientManagePanel.this);
						panel.add(patientSexComboBox);
						break;
				}
			} else {
			}
			return panel;
		}
	}

	// 病人状态切换按钮监听器
	class PatientStateActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectPatient == null) {
				JOptionPane.showMessageDialog(PatientManagePanel.this,
						"请先选择需要监控的病人");
				return;
			}
			if (selectPatient.isOnline() && selectPatient.isSelected()) {
				selectPatient.setSelected(false, mLnxClient);
				changePStateButton.setText("监控病人");
				changePStateButton.setEnabled(true);
				realECGButton.setEnabled(false);
			} else if (selectPatient.isOnline()) {
				WaitingTipDialog.showDialog(mLnxClient, "数据接收初始化中，需要等待数秒钟，请稍后");
				selectPatient.setSelected(true, mLnxClient);
				changePStateButton.setText("取消监控");
			}
			System.out.println(selectPatient);
			selectPatientList.changePatientState(selectPatient);
			setSelectGroup(groupTableSelection);
		}
	}

	/**
	 * 取消被选择的病人
	 */
	public void unSelectP() {

		for (Group group : groupList) {
			PatientList patientList = patientGroupMap.get(group.getGroupId());
			Patient[] patients = patientList.getSelectedOnlinePatients()
					.values().toArray(new Patient[0]);
			for (int i = 0; i < patients.length; i++) {
				patients[i].setSelected(false, null);
			}
		}
	}

	/**
	 * 完成监控病人选择
	 */
	public void finishSelectP() {
		if (selectPatient.isOnline() && selectPatient.isSelected()) {
			changePStateButton.setEnabled(true);
			realECGButton.setEnabled(true);
			mLnxClient.broadcastPGData();
		}
	}

	// 历史心电，实时心电监听器
	class RHECGActionListener implements ActionListener {

		private boolean isReal;

		public RHECGActionListener(boolean isReal) {
			this.isReal = isReal;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectPatient == null) {
				JOptionPane.showMessageDialog(PatientManagePanel.this,
						"请先选择需要监控的病人");
				return;
			}
			if (isReal) {
				mLnxClient.watchRealECG(selectPatient);
			} else {
				HistoryFrame histFrame = new HistoryFrame("历史数据查询");
				histFrame.setPatientSelected(selectPatient);
				histFrame.setConnectStr(SettingParameters.getInstance()
						.getIpString());
			}
		}

	}

	// 网络交互
	public class PatientManageSwingWorker {

		public void buildGroup(final Group addGroup) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Group group = MlnxDoctorClient.buildGroup(addGroup);
					if (group == null)
						showTip = "添加群组失败!";
					else {
						showTip = "添加群组成功!";
						UsrData.renewAllData();
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (group != null) {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
					}
				}
			}).start();
		}

		public void deleteGroup(final Group deleteGroup) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Group group = MlnxDoctorClient.deleteGroup(deleteGroup);
					if (group == null)
						showTip = "删除群组失败!";
					else {
						showTip = "删除群组成功!";
						UsrData.renewAllData();
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (group != null) {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
					}
				}
			}).start();
		}

		public void searchPatient(final com.mlnx.pms.core.Patient patient) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					List<com.mlnx.pms.core.Patient> patients = MlnxDoctorClient
							.searchPatient(patient);
					if (patients == null)
						showTip = "查找病人失败!";
					else {
						showTip = "查找病人成功!";
						searchPatients = patients;
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
							SearchPatientListDialog searchPatientListDialog = new SearchPatientListDialog(
									searchPatients, PatientManagePanel.this);
							searchPatientListDialog.setVisible(true);
						}
					});
				}
			}).start();
		}

		public void addPatient(final com.mlnx.pms.core.Patient patient) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					com.mlnx.pms.core.Patient p = MlnxDoctorClient
							.addPatient(patient);
					if (p == null)
						showTip = "注册病人失败!";
					else {
						showTip = "注册病人成功!";
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (p != null) {
						PatientGroup patientGroup = new PatientGroup();
						patientGroup.setPatientId(p.getId());
						patientGroup.setGroupId(selectGroup.getGroupId());
						addPatientGroup(patientGroup);
					}
				}
			}).start();
		}
		public void modifyPatient(final com.mlnx.pms.core.Patient patient) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					com.mlnx.pms.core.Patient p = MlnxDoctorClient
							.modifyPatient(patient);
					if (p == null)
						showTip = "修改病人信息失败!";
					else {
						showTip = "修改病人信息成功!";
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (p != null) {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
					}
				}
			}).start();
		}

		public void deletePatientGroup(final PatientGroup patientGroup) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					PatientGroup pg = MlnxDoctorClient
							.deletePatientGroup(patientGroup);
					if (pg == null)
						showTip = "删除失败!";
					else {
						showTip = "删除成功!";
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (pg != null) {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
					}
				}
			}).start();
		}

		public void addPatientGroup(final PatientGroup patientGroup) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					PatientGroup pg = MlnxDoctorClient
							.addPatientGroup(patientGroup);
					if (pg == null)
						showTip = "添加病人到群组失败!";
					else {
						showTip = "添加病人到群组成功!";
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(
									PatientManagePanel.this, showTip);
						}
					});
					if (pg != null) {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
					}
				}
			}).start();
		}

		public void refreshPatientListTask() {
			if (SettingParameters.getInstance().getLoginUser() == null) {
				JOptionPane.showMessageDialog(PatientManagePanel.this, "请先登入");
				return;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (refreshPatientList()) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								setSelectGroup(groupTableSelection);
								mLnxClient.broadcastPGData();
								if (MLnxClient.showRefrshStateDialog)
									JOptionPane.showMessageDialog(
											PatientManagePanel.this, "更新数据成功");
							}
						});
					} else if (MLnxClient.showRefrshStateDialog) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								JOptionPane.showMessageDialog(
										PatientManagePanel.this, "更新数据失败");
							}
						});
					}
				}
			}).start();
		}

		public void refreshOnlinePatientListTask() {
			if (SettingParameters.getInstance().getLoginUser() == null) {
				JOptionPane.showMessageDialog(PatientManagePanel.this, "请先登入");
				return;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					refreshOnlinePatientList();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							setSelectGroup(groupTableSelection);
							mLnxClient.broadcastPGData();
						}
					});
				}
			}).start();
		}

		public void changePBindD(final JDialog dialog, final Device device) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					boolean success = MlnxDoctorClient
							.configureDevicePatient(device);
					if (!success) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								JOptionPane.showMessageDialog(dialog,
										"绑定设备失败，设备可能正在被使用");
							}
						});
					} else {
						MLnxClient.showRefrshStateDialog = false;
						refreshPatientListTask();
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								JOptionPane.showMessageDialog(dialog, "绑定设备成功");
							}
						});
					}
				}
			}).start();
		}

		/**
		 * 更新所有 group列表 病人列表 在线病人列表
		 * 
		 * @return true：成功，flase：失败
		 */
		private synchronized boolean refreshPatientList() {

			boolean finish = true;

			// 获取所有设备
			List<Device> devices2 = MlnxDoctorClient.getAllDevices();
			devices = new ArrayList<Device>();
			devices.addAll(devices2);
			// 获取group列表
			List<Group> tempGroupList = MlnxDoctorClient.getAllGroup();
			if (tempGroupList == null)
				return false;

			Map<Long, PatientList> pgMap = new HashMap<Long, PatientList>();
			for (Group group : tempGroupList) {
				// 获取所有病人
				List<Patient> patients = MlnxDoctorClient
						.getGroupPatients(group.getGroupId());
				if (patients == null)
					return false;
				PatientList patientList = new PatientList();
				patientList = new PatientList(patients);
				if (patientGroupMap.get(group.getGroupId()) != null)
					patientList.setRecordSelectedOnlinePatients(patientGroupMap
							.get(group.getGroupId())
							.getSelectedOnlinePatients());
				else
					patientList.setRecordSelectedOnlinePatients(null);

				// 获取在线病人
				if (patients != null && patients.size() > 0) {
					List<Integer> patientIDList = new ArrayList<Integer>();
					for (Patient patient : patients) {
						patientIDList.add(patient.getPatientID());
						// 设置其device
						for (Iterator<Device> iterator = devices.iterator(); iterator
								.hasNext();) {
							Device device = (Device) iterator.next();
							if (device.getPatientId() == patient.getPatientID()) {
								patient.setDevInfo(new DeviceInfo(device));
								break;
							} else
								patient.setDevInfo(null);
						}

					}
					List<Patient> onlinePatientList = MlnxDoctorClient
							.getOnlinePatients(patientIDList);
					if (onlinePatientList == null)
						return false;
					patientList.renewOnlinePatient(onlinePatientList,
							mLnxClient);
				}
				pgMap.put(group.getGroupId(), patientList);
			}

			groupList = tempGroupList;
			patientGroupMap = pgMap;
			if (selectGroup != null) {
				for (int i = 0; i < groupList.size(); i++) {
					if (selectGroup.getGroupId() == groupList.get(i)
							.getGroupId()) {
						groupTableSelection = i;
						break;
					}
				}
			}
			return finish;
		}

		/**
		 * 更新在线病人
		 * 
		 * @return true：成功，flase：失败
		 */
		private synchronized boolean refreshOnlinePatientList() {

			// 获取所有设备

			List<Device> devices2 = MlnxDoctorClient.getAllDevices();
			devices = new ArrayList<Device>();
			devices.addAll(devices2);

			if (groupList == null)
				return false;
			for (Group group : groupList) {
				PatientList patientList = patientGroupMap.get(Long
						.valueOf(group.getGroupId()));
				// 获取在线病人
				List<Integer> patientIDList = new ArrayList<Integer>();
				for (Iterator<Patient> iterator = patientList.getPatients()
						.values().iterator(); iterator.hasNext();) {
					Patient patient = (Patient) iterator.next();
					patientIDList.add(patient.getPatientID());
					// 设置其device
					for (int i = 0; i < devices.size(); i++) {
						if (devices.get(i).getPatientId() == patient
								.getPatientID()) {
							patient.setDevInfo(new DeviceInfo(devices.get(i)));
							break;
						} else
							patient.setDevInfo(null);
					}

				}
				List<Patient> onlinePatientList = MlnxDoctorClient
						.getOnlinePatients(patientIDList);
				patientList.renewOnlinePatient(onlinePatientList, mLnxClient);
			}
			return true;
		}
	}

	/**
	 * 修改设备绑定的病人
	 * 
	 * @param device
	 */
	public void changePBindD(JDialog dialog, Device device) {
		PatientManageSwingWorker manageSwingWorker = new PatientManageSwingWorker();
		manageSwingWorker.changePBindD(dialog, device);
	}

	/*
	 * 刷新病人和群组列表
	 */
	public void refreshPatientListTask() {
		PatientManageSwingWorker manageSwingWorker = new PatientManageSwingWorker();
		manageSwingWorker.refreshPatientListTask();
	}

	/*
	 * 刷新在线病人列表
	 */
	public void refreshOnlinePatientListTask() {
		PatientManageSwingWorker manageSwingWorker = new PatientManageSwingWorker();
		manageSwingWorker.refreshOnlinePatientListTask();
	}

	/**
	 * 更新生理信息
	 * 
	 * @return 报警等级
	 */
	public int updataAnalysisData() {
		int minAlarmLevel = 4;
		for (Group group : groupList) {
			HashMap<Integer, Patient> selectedOnlinePatients = patientGroupMap
					.get(group.getGroupId()).getSelectedOnlinePatients();
			Patient[] patients = selectedOnlinePatients.values().toArray(
					new Patient[0]);
			for (int i = 0; i < patients.length; i++) {
				patients[i].getDrawingPanel().updateInfoPanel();
				if (minAlarmLevel > patients[i].getDrawingPanel().getLevel())
					minAlarmLevel = patients[i].getDrawingPanel().getLevel();
			}
		}
		return minAlarmLevel;
	}
}
