package ui.medlinx.com.frame.Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.medlinx.core.databuff.MultiChannelBuffer;
import com.medlinx.core.patient.Patient;
import com.medlinx.core.patient.PatientList;
import com.mlnx.pms.core.Group;

public class RealECGPanel extends JPanel {

	private final JSplitPane splitPane = new JSplitPane();
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel multiPanel = new JPanel(new GridLayout(2, 2));
	private JPanel panel1 = new JPanel(new GridLayout(1, 1));
	private JPanel panel2 = new JPanel(new GridLayout(1, 1));
	private JPanel panel3 = new JPanel(new GridLayout(1, 1));
	private JPanel panel4 = new JPanel(new GridLayout(1, 1));
	private boolean _panel1;
	private boolean _panel2;
	private boolean _panel3;
	private boolean _panel4;
	private List<Patient> allPatients = new ArrayList<Patient>();
	private List<Patient> prePatients = new ArrayList<Patient>();
	private List<Patient> prePatients2 = new ArrayList<Patient>();

	private static final double Location = 0.15;
	private static final String TOPNODE_STRING = "被选择的在线病人列表";
	private MLnxClient mLnxClient;

	private JPanel selectPatientspanel;
	private DrawingPanel selectDrawingPanel;
	private JTree selectPatientsTree;
	private DefaultMutableTreeNode top;
	private JScrollPane selectPatientsTreeScrollPane;

	private boolean isPreview = true;

	private List<RealECGGroup> groupList = new ArrayList<RealECGGroup>(); // group列表
	private Map<Long, Patient[]> patientsMap = new HashMap<Long, Patient[]>(); // 不同group的病人列表
	private Patient selectPatient;

	// timer
	private Timer refreshECGTimer;

	// tree group
	class RealECGGroup {
		private Group group;

		public RealECGGroup(Group group) {
			super();
			this.group = group;
		}

		public Group getGroup() {
			return group;
		}

		public Long getGroupId() {
			return group.getGroupId();
		}

		@Override
		public String toString() {
			return group.getName();
		}
	}

	public RealECGPanel(MLnxClient mLnxClient) {
		panel1.setBorder(new MatteBorder(0, 0, 1, 1, (Color) new Color(255,
				200, 0)));
		panel2.setBorder(new MatteBorder(0, 1, 1, 0, (Color) new Color(255,
				200, 0)));
		panel3.setBorder(new MatteBorder(1, 0, 0, 1, (Color) new Color(255,
				200, 0)));
		panel4.setBorder(new MatteBorder(1, 1, 0, 0, (Color) new Color(255,
				200, 0)));
		this.setLayout(new GridLayout(1, 1));
		this.add(splitPane);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.mLnxClient = mLnxClient;
		addComponent();
		splitPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				splitPane.setDividerLocation(Location);
			}
		});

		// refreshECGTimer
		refreshECGTimer = new Timer(30, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectDrawingPanel != null)
					selectDrawingPanel.repaint();
				multiPanel.repaint();
			}
		});
	}

	/*
	 * 添加组件
	 */
	private void addComponent() {

		splitPane.setOneTouchExpandable(true);
		// left
		addSelectPatientsPanel();
		// right
		addShowECGPanel();
	}

	// left
	private void addSelectPatientsPanel() {
		selectPatientspanel = new JPanel(new GridLayout(1, 1));
		selectPatientspanel.setBackground(Color.BLACK);
		refreshPatientsTree();
		splitPane.setLeftComponent(selectPatientspanel);
		splitPane.validate();
	}

	/**
	 * 更新数据
	 * 
	 * @param groupList
	 * @param patientGroupMap
	 */
	public void setGPData(List<Group> groups,
			Map<Long, PatientList> patientGroupMap) {
		if (groupList == null)
			return;

		// 更新groupList patientsMap
		groupList = new ArrayList<RealECGPanel.RealECGGroup>();
		patientsMap = new HashMap<Long, Patient[]>();
		for (Group group : groups) {
			groupList.add(new RealECGGroup(group));
			Long groupID = group.getGroupId();
			patientsMap.put(groupID,
					patientGroupMap.get(groupID).getSelectedOnlinePatients()
							.values().toArray(new Patient[0]));
		}
		refreshPatientsTree();

		boolean isfind = false;
		// 寻找原来选择的病人
		if (!isPreview) {
			for (Group group : groups) {
				Patient[] patients = patientsMap.get(group.getGroupId());
				if (patients.length > 0) {
					for (int i = 0; i < patients.length; i++) {
						if (patients[i].getPatientID() == selectPatient
								.getPatientID()) {
							isfind = true;
							return;
						}
					}
				}
			}
		}
		// 没有找到原来选择的病人
		if (!isfind) {
			for (Group group : groups) {
				Patient[] patients = patientsMap.get(group.getGroupId());
				if (patients.length > 0) {
					selectPatient(patients[0]);
					isfind = true;
					return;
				}
			}
		}

		// 一个病人都没有
		if (!isfind) {
			selectPatient(null);
			refreshECGTimer.stop();
		}
	}

	/*
	 * 在线选择病人列表树
	 */
	public void refreshPatientsTree() {
		top = new DefaultMutableTreeNode(TOPNODE_STRING);
		selectPatientsTree = new JTree(top);
		selectPatientsTree.setBackground(Color.BLACK);
		selectPatientsTree.setCellRenderer(new PatientTreeCell());
		selectPatientsTree
				.addTreeSelectionListener(new TreeSelectionListener() {

					@Override
					public void valueChanged(TreeSelectionEvent e) {

						DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
								.getPath().getLastPathComponent();
						if (node.getUserObject() instanceof Patient) {
							Patient selectPatient = (Patient) node
									.getUserObject();
							selectPatient2(selectPatient);
						}
					}
				});
		selectPatientsTreeScrollPane = new JScrollPane(selectPatientsTree);
		selectPatientspanel.removeAll();
		selectPatientspanel.add(selectPatientsTreeScrollPane);
		boolean isSelect = true;
		if (groupList == null || groupList.size() == 0) {
			top.add(new DefaultMutableTreeNode("该用户下没有监护病人"));
		} else {
			isSelect = false;
			for (RealECGGroup ecgGroup : groupList) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						ecgGroup);
				Patient[] patients = patientsMap.get(ecgGroup.getGroupId());
				if (patients.length > 0) {
					for (int i = 0; i < patients.length; i++) {
						node.add(new DefaultMutableTreeNode(patients[i]));
					}
					top.add(node);
					isSelect = true;
				}
			}
		}
		if (!isSelect)
			top.add(new DefaultMutableTreeNode("该用户下没有监护病人"));
		for (int i = 0; i < 50; i++) {
			selectPatientsTree.expandRow(i);
		}
		selectPatientspanel.updateUI();
	}

	/**
	 * 选中某个病人(病人管理界面点击实时心电调用)
	 */
	public void selectPatient(Patient selectPatient) {
		allPatients.clear();
		for (RealECGGroup ecgGroup : groupList) {
			Patient[] patients = patientsMap.get(ecgGroup.getGroupId());
			if (patients.length > 0) {
				for (int i = 0; i < patients.length; i++) {
					allPatients.add(patients[i]);
				}
			}

		}
		_panel1 = false;
		_panel2 = false;
		_panel3 = false;
		_panel4 = false;
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		refreshECGTimer.stop();
		this.selectPatient = selectPatient;
		if (selectPatient != null && allPatients.size() > 0) {
			selectDrawingPanel = this.selectPatient.getDrawingPanel();
			isPreview = false;
			panel1.removeAll();
			panel2.removeAll();
			panel3.removeAll();
			panel4.removeAll();

			panel1.add(this.selectPatient.getDrawingPanel2());
			_panel1 = true;
			prePatients.add(this.selectPatient);
			for (int i = 0; i < allPatients.size(); i++) {
				if (_panel4) {
					break;
				} else {
					if (allPatients.get(i) != selectPatient) {

						if (!_panel2) {
							panel2.add(allPatients.get(i).getDrawingPanel2());
							_panel2 = true;
							prePatients.add(allPatients.get(i));
						} else if (!_panel3) {
							panel3.add(allPatients.get(i).getDrawingPanel2());
							_panel3 = true;
							prePatients.add(allPatients.get(i));
						} else if (!_panel4) {
							panel4.add(allPatients.get(i).getDrawingPanel2());
							_panel4 = true;
							prePatients.add(allPatients.get(i));
						}
					}
				}

			}

			multiPanel.add(panel1);
			multiPanel.add(panel2);
			multiPanel.add(panel3);
			multiPanel.add(panel4);
			tabbedPane.addTab("单病人显示", null, selectDrawingPanel, null);
			tabbedPane.addTab("多病人显示", null, multiPanel, null);
			splitPane.setRightComponent(tabbedPane);
		} else {
			if (!isPreview) {
				isPreview = true;
				selectDrawingPanel = new DrawingPanel(mLnxClient,
						new MultiChannelBuffer(new Patient()));
				enableComponents(selectDrawingPanel, false);
				tabbedPane.addTab("单病人显示", null, selectDrawingPanel, null);
				tabbedPane.addTab("多病人显示", null, multiPanel, null);
				splitPane.setRightComponent(tabbedPane);
			}
		}
		tabbedPane.updateUI();
		splitPane.updateUI();
		refreshECGTimer.start();
	}

	/**
	 * 选中某个病人(病人列表树点击病人调用)
	 * 
	 * @param selectPatient
	 */
	public void selectPatient2(Patient selectPatient) {

		prePatients2.clear();
		_panel1 = false;
		_panel2 = false;
		_panel3 = false;
		_panel4 = false;
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		refreshECGTimer.stop();
		this.selectPatient = selectPatient;

		selectDrawingPanel = this.selectPatient.getDrawingPanel();
		isPreview = false;
		panel1.removeAll();
		panel2.removeAll();
		panel3.removeAll();
		panel4.removeAll();

		panel1.add(this.selectPatient.getDrawingPanel2());
		_panel1 = true;
		prePatients2.add(this.selectPatient);
		for (int i = 0; i < prePatients.size(); i++) {
			if (_panel4) {
				break;
			} else {
				if (prePatients.get(i) != selectPatient) {

					if (!_panel2) {
						panel2.add(prePatients.get(i).getDrawingPanel2());
						_panel2 = true;
						prePatients2.add(prePatients.get(i));
					} else if (!_panel3) {
						panel3.add(prePatients.get(i).getDrawingPanel2());
						_panel3 = true;
						prePatients2.add(prePatients.get(i));
					} else if (!_panel4) {
						panel4.add(prePatients.get(i).getDrawingPanel2());
						_panel4 = true;
						prePatients2.add(prePatients.get(i));
					}
				}
			}

		}
		prePatients.clear();
		prePatients.addAll(prePatients2);
		multiPanel.add(panel1);
		multiPanel.add(panel2);
		multiPanel.add(panel3);
		multiPanel.add(panel4);
		tabbedPane.addTab("单病人显示", null, selectDrawingPanel, null);
		tabbedPane.addTab("多病人显示", null, multiPanel, null);
		splitPane.setRightComponent(tabbedPane);

		tabbedPane.updateUI();
		splitPane.updateUI();
		refreshECGTimer.start();
	}

	public void test() {
		System.out.println("size11111111----" + prePatients.size());
		System.out.println("size22222222----" + prePatients2.size());
	}
	// 病人列表效果
	class PatientTreeCell extends JPanel implements TreeCellRenderer {

		public PatientTreeCell() {
			this.setLayout(new FlowLayout(FlowLayout.CENTER));
			this.setBackground(Color.BLACK);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			JLabel label = new JLabel();
			// top node
			if (leaf) {
				label.setFont(new Font("楷体", Font.PLAIN, 13));
				if (selectPatient != null
						&& selectPatient.toString().equals(value.toString())) {
					label.setForeground(Color.GREEN);
				} else
					label.setForeground(Color.CYAN);
			} else {
				label.setFont(new Font("楷体", Font.BOLD, 15));
				label.setForeground(Color.ORANGE);
			}
			label.setText(value.toString());
			this.removeAll();
			this.add(label);
			return this;
		}
	}

	// right
	private void addShowECGPanel() {
		selectDrawingPanel = new DrawingPanel(mLnxClient,
				new MultiChannelBuffer(new Patient()));
		enableComponents(selectDrawingPanel, false);
		splitPane.setRightComponent(selectDrawingPanel);
	}

	private void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}
}
