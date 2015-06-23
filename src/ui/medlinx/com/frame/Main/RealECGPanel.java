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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.medlinx.core.databuff.MultiChannelBuffer;
import com.medlinx.core.patient.Patient;
import com.medlinx.core.patient.PatientList;
import com.mlnx.pms.core.Group;

public class RealECGPanel extends JSplitPane {

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
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		this.mLnxClient = mLnxClient;
		addComponent();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				RealECGPanel.this.setDividerLocation(Location);
			}
		});

		// refreshECGTimer
		refreshECGTimer = new Timer(30, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectDrawingPanel != null)
					selectDrawingPanel.repaint();
			}
		});
	}

	/*
	 * 添加组件
	 */
	private void addComponent() {

		this.setOneTouchExpandable(true);
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
		this.setLeftComponent(selectPatientspanel);
		this.validate();
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
							selectPatient(selectPatient);
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
	 * 选中某个病人
	 */
	public void selectPatient(Patient selectPatient) {
		refreshECGTimer.stop();

		this.selectPatient = selectPatient;
		if (selectPatient != null) {
			selectDrawingPanel = this.selectPatient.getDrawingPanel();
			isPreview = false;
			this.setRightComponent(selectDrawingPanel);
		} else {
			if (!isPreview) {
				isPreview = true;
				selectDrawingPanel = new DrawingPanel(mLnxClient,
						new MultiChannelBuffer(new Patient()));
				enableComponents(selectDrawingPanel, false);
				this.setRightComponent(selectDrawingPanel);
			}
		}
		this.updateUI();

		refreshECGTimer.start();
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
		this.setRightComponent(selectDrawingPanel);
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
