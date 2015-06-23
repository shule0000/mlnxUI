package ui.medlinx.com.dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.frame.heart.HeartRateFrame;

import com.medlinx.core.constant.SystemConstant;
import com.mlnx.pms.core.Patient;


/**
 * A dialog to select patients to be shown from all patients online
 * @author jfeng
 *
 */
public class PatientSelectHeartRateDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private ArrayList<Patient> candidateInfoList; 
	private ArrayList<com.medlinx.core.patient.Patient> candidateList;// list of candidate patients
	private JLabel lblLeads;
	private boolean initializeFlag;
	private Font textFont;
	private JPanel panel;
	@SuppressWarnings("rawtypes")
	private JList candidateJList;

	private JButton okButton, cancelButton;
	private ButtonActionListener buttonActionListener;
	private JCheckBox chckbxI,chckbxII,chckbxIII,chckbxAvr,chckbxAvl,chckbxAvf;
	private JCheckBox chckbxV1,chckbxV2,chckbxV3,chckbxV4,chckbxV5,chckbxV6,chckbxSelectAllnone;
	private ItemListenerClass chckbxListener;
	private HeartRateFrame client;
	private ArrayList<JCheckBox> checkBoxList;


	@SuppressWarnings("unchecked")
	public void setupTheList(){
		DebugTool.printLogDebug("setup candidate lists.");
		if(candidateList==null||candidateList.size()==0)
			return;
		Vector<String> nameListCandidate = new Vector<String>();
		for (com.medlinx.core.patient.Patient patient : candidateList) {
			nameListCandidate.add( patient.getPatientID() + patient.getPatientName());
			DebugTool.printLogDebug( patient.getPatientID() + patient.getPatientName());
		}
		candidateJList.setListData(nameListCandidate);
		candidateJList.setSelectedIndex(0);
		panel.revalidate();
		panel.repaint();
	}
	private void convertList()
	{
		if(candidateInfoList==null||candidateInfoList.size()==0)
			return;
		//setup the patient list from the list of paitentInfo
		for(Patient p:candidateInfoList)
		{
			DebugTool.printLogDebug("size of list:"+candidateInfoList.size());
			com.medlinx.core.patient.Patient tempPatient = new com.medlinx.core.patient.Patient();
			tempPatient.setPatientID(p.getId());
			tempPatient.setPatientName(p.getName());
			if(p.getGender()==Patient.Gender.FEMALE)
				tempPatient.setGender("FEMALE");
			if(p.getGender()==Patient.Gender.MALE)
				tempPatient.setGender("MALE");
			if(p.getGender()==Patient.Gender.OTHER)
				tempPatient.setGender("OTHER");
			candidateList.add(tempPatient);
		}
	}
	// testing main function
	public static void main(String[] args) {
		try {
			PatientSelectHeartRateDialog dialog = new PatientSelectHeartRateDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * constructor with the list of selected patients given 
	 * @param optionListIn the list of selected patients
	 * @param connectStrIn URL (String) to connect to database (get the list of online patients)
	 */
	public PatientSelectHeartRateDialog(ArrayList<Patient> optionListIn, HeartRateFrame mainFrame)
	{
		candidateInfoList = optionListIn;
		client = mainFrame;
		initialize();
	}
	/**
	 * constructor
	 */
	public PatientSelectHeartRateDialog()
	{
		setAlwaysOnTop(true);
		candidateInfoList = new ArrayList<Patient>();
		initialize();
	}
	/**
	 * initialize everything
	 */
	@SuppressWarnings({"rawtypes" })
	public void initialize() {
		// set the icon for this dialog
		ImageIcon imageIcon = new ImageIcon("res/icon.png");
		int loadingDone = MediaTracker.ABORTED | MediaTracker.ERRORED | MediaTracker.COMPLETE;
		while((imageIcon.getImageLoadStatus() & loadingDone) == 0){
			//just wait a bit...
		}
		if(imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE)
			setIconImage(imageIcon.getImage());
		else {
			//something went wrong loading the image...
			DebugTool.printLogDebug("Error: loading icon image.");
		} 
		candidateList = new ArrayList<com.medlinx.core.patient.Patient>();
		convertList();
		buttonActionListener = new ButtonActionListener();
		chckbxListener = new ItemListenerClass ();
		textFont = new Font("FangSong", Font.BOLD, 11);
		initializeFlag = true;
		setTitle("选择显示病人");
		checkBoxList = new ArrayList<JCheckBox>();
		setBounds(100, 100, 276, 293);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(1, 0, 0, 0));
		{
			panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(null);
			{
				JLabel lblcandidate = new JLabel("全部病人");
				lblcandidate.setBounds(0, 5, 98, 15);
				panel.add(lblcandidate);
			}
			{
				candidateJList = new JList();
				candidateJList.setBounds(0, 26, 98, 187);
				candidateJList.setFont(textFont);
				candidateJList.addListSelectionListener(new SelectedJListListener());
				panel.add(candidateJList);
			}
		}
		/*{
			ButtonGroup group = new ButtonGroup();
		}*/

		JPanel panel = new JPanel();
		contentPanel.add(panel);
		panel.setLayout(null);

		lblLeads = new JLabel("导�?�");
		lblLeads.setBounds(32, 5, 66, 15);
		panel.add(lblLeads);

		chckbxI = new JCheckBox("I");
		chckbxI.addItemListener(chckbxListener);
		chckbxI.setBounds(6, 27, 31, 23);
		panel.add(chckbxI);
		checkBoxList.add(chckbxI);

		chckbxII = new JCheckBox("II");
		chckbxII.addItemListener(chckbxListener);
		chckbxII.setBounds(6, 53, 37, 23);
		panel.add(chckbxII);
		checkBoxList.add(chckbxII);

		chckbxIII = new JCheckBox("III");
		chckbxIII.addItemListener(chckbxListener);
		chckbxIII.setBounds(6, 78, 43, 23);
		panel.add(chckbxIII);
		checkBoxList.add(chckbxIII);

		chckbxAvr = new JCheckBox("aVR");
		chckbxAvr.addItemListener(chckbxListener);
		chckbxAvr.setBounds(6, 103, 55, 23);
		panel.add(chckbxAvr);
		checkBoxList.add(chckbxAvr);
		{
			chckbxAvl = new JCheckBox("aVL");
			chckbxAvl.addItemListener(chckbxListener);
			chckbxAvl.setBounds(6, 128, 55, 23);
			panel.add(chckbxAvl);
			checkBoxList.add(chckbxAvl);
		}
		{
			chckbxAvf = new JCheckBox("aVF");
			chckbxAvf.addItemListener(chckbxListener);
			chckbxAvf.setBounds(6, 154, 55, 23);
			panel.add(chckbxAvf);
			checkBoxList.add(chckbxAvf);
		}
		{
			chckbxV1 = new JCheckBox("V1");
			chckbxV1.addItemListener(chckbxListener);
			chckbxV1.setBounds(70, 27, 43, 23);
			panel.add(chckbxV1);
			checkBoxList.add(chckbxV1);
		}
		{
			chckbxV2 = new JCheckBox("V2");
			chckbxV2.addItemListener(chckbxListener);
			chckbxV2.setBounds(70, 53, 43, 23);
			panel.add(chckbxV2);
			checkBoxList.add(chckbxV2);
		}
		{
			chckbxV3 = new JCheckBox("V3");
			chckbxV3.addItemListener(chckbxListener);
			chckbxV3.setBounds(70, 78, 43, 23);
			panel.add(chckbxV3);
			checkBoxList.add(chckbxV3);
		}
		{
			chckbxV4 = new JCheckBox("V4");
			chckbxV4.addItemListener(chckbxListener);
			chckbxV4.setBounds(70, 103, 43, 23);
			panel.add(chckbxV4);
			checkBoxList.add(chckbxV4);
		}
		{
			chckbxV5 = new JCheckBox("V5");
			chckbxV5.addItemListener(chckbxListener);
			chckbxV5.setBounds(70, 128, 43, 23);
			panel.add(chckbxV5);
			checkBoxList.add(chckbxV5);
		}
		{
			chckbxV6 = new JCheckBox("V6");
			chckbxV6.addItemListener(chckbxListener);
			chckbxV6.setBounds(70, 154, 43, 23);
			panel.add(chckbxV6);
			checkBoxList.add(chckbxV6);
		}

		chckbxSelectAllnone = new JCheckBox("select all/none");
		chckbxSelectAllnone.addItemListener(chckbxListener);
		chckbxSelectAllnone.setBounds(6, 183, 116, 23);
		panel.add(chckbxSelectAllnone);
		checkBoxList.add(chckbxSelectAllnone);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("确认");
				// okButton.setActionCommand("OK");
				okButton.addActionListener(new ButtonActionListener());
				buttonPane.add(okButton);
				// getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("�?�消");
				// cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ButtonActionListener());
				buttonPane.add(cancelButton);
			}
		}
		
		setupTheList();
	}

	private void onExit() {
		this.dispose();
	}

	/**
	 * getter of main frame window
	 * @return object of main frame window
	 */
	public HeartRateFrame getClient() {
		return client;
	}

	/**
	 * setter of main frame window
	 * @param client main frame window
	 */
	public void setClient(HeartRateFrame client) {
		this.client = client;
	}

	class SelectedJListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if(arg0.getSource().equals(candidateJList)){
				int index = candidateJList.getSelectedIndex();
				if (index == -1)
					return;
				com.medlinx.core.patient.Patient temp = null;
				if (candidateList.size() > index)
					temp = candidateList.get(index);
				if (temp == null)
					return;
				lblLeads.setText(temp.getPatientName());
				DebugTool.printLogDebug(temp.getPatientName());
				
				initializeFlag = true;
				for(int i=0;i<SystemConstant.ECGCHANNELNUM;++i)
				{
					DebugTool.printLogDebug(i+":"+temp.getChannelFlag(i));
					checkBoxList.get(i).setSelected(temp.getChannelFlag(i));
				}
				if(checkBoxList.size()>SystemConstant.ECGCHANNELNUM)
					checkBoxList.get(SystemConstant.ECGCHANNELNUM).setSelected(false);
				initializeFlag = false;
			}
		}
		
	}
	//the listener of the selected list
	/**
	 * This ItemListener define the behavior when a patient is selected from the list of selected patients. 
	 * The list of channels will be updated, means user can select channels for this patient. 
	 * @author jfeng
	 *
	 */
	private class ItemListenerClass implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			int indexChckbx = 0;
			for (int i=0; i<checkBoxList.size(); ++i)
			{
				if(arg0.getSource().equals(checkBoxList.get(i)))
				{
					indexChckbx = i;
					break;
				}
			}
			if (indexChckbx<0||indexChckbx>12)
				return;
			if (indexChckbx<12) // <12 means select one of leads
			{
				if(initializeFlag)
					return;
				int index = candidateJList.getSelectedIndex();
				if (index == -1)
					return;
				com.medlinx.core.patient.Patient temp = null;
				if (candidateList.size() > index)
				{
					temp = candidateList.get(index);
				}
				else
				{
					return;
				}

				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					temp.setChannelFlag(indexChckbx);
				} else {
					temp.cancelChannelFlag(indexChckbx);
				}
			}
			else //indexChckbx == 12 means select all/none
			{
				if(initializeFlag)
					return;
				int index = candidateJList.getSelectedIndex();
				if (index == -1)
					return;

				com.medlinx.core.patient.Patient temp = null;
				if (candidateList.size() > index)
				{
					temp = candidateList.get(index);
				}
				else
				{
					return;
				}
				initializeFlag = true;
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					for(int i=0;i<SystemConstant.ECGCHANNELNUM;++i)
					{
						DebugTool.printLogDebug(i+":"+temp.getChannelFlag(i));
						checkBoxList.get(i).setSelected(true);
						temp.setChannelFlag(i);
					}
				} else {
					for(int i=0;i<SystemConstant.ECGCHANNELNUM;++i)
					{
						DebugTool.printLogDebug(i+":"+temp.getChannelFlag(i));
						checkBoxList.get(i).setSelected(false);
						temp.cancelChannelFlag(i);
					}
				}
				initializeFlag = false;
			}
		}
	}
	private class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(okButton)) {
				DebugTool.printLogDebug("OK");
				if (client != null) {
					// XXX Client starts to update
					//client.updateFromUser(selectedList);
					//String output = "all patients " + selectedList.size() + ": ";
					//for (Patient p : selectedList)
					//	output = output + p.getPatientName() + ", "
					//			+ p.getPatientID() + ", " + p.getChannelNum()
					//			+ "; ";
					DebugTool.printLogDebug("OK");
					int index = candidateJList.getSelectedIndex();
					if (index == -1)
						return;

					com.medlinx.core.patient.Patient temp = null;
					if (candidateList.size() > index)
					{
						temp = candidateList.get(index);
						client.setPatientSelected(temp);
						String output = "push back this selected patient, " + temp.getPatientID() +
								": "+temp.getPatientName()+
								": "+temp.getGender();
						DebugTool.printLogDebug(output);
						for(int i=0;i<SystemConstant.ECGCHANNELNUM;++i)
						{
							DebugTool.printLogDebug(i+":"+temp.getChannelFlag(i));
						}
						
					}
					else
					{
						return;
					}
				}
				onExit();
			}
			if (arg0.getSource().equals(cancelButton)){
				candidateInfoList.clear();
				onExit();
			}
		}
	}
}
