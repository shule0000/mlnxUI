package ui.medlinx.com.frame.heart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import junit.framework.Assert;
//import com.standbysoft.component.date.DateSelectionModel;
//import com.standbysoft.component.date.DateSelectionModel.SelectionMode;
//import com.standbysoft.component.date.swing.JDateComponent;
//import com.standbysoft.component.date.swing.JDatePicker;
//import com.standbysoft.component.date.swing.JMonthView;
//import com.standbysoft.component.date.swing.JDateComponent.DateAction;
//import com.standbysoft.component.date.swing.plaf.basic.SpinnerTimePickerUI;
import org.jdesktop.swingx.JXDatePicker;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.dialog.PatientSelectHeartRateDialog;
import ui.medlinx.com.dialog.WaitLayerUI;
import ui.medlinx.com.extra.Style;
import ui.medlinx.com.resource.SystemResources;

import com.medlinx.core.databuff.HeartRateDataBuffer;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.DataClientBuilder;
import com.mlnx.pms.client.ServerProcessingException;
import com.mlnx.pms.core.Patient.Gender;

/**
 * Frame to show history data
 * @author Andong
 *
 */
public class HeartRateFrame extends JFrame implements ActionListener, ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7844200664714875388L;
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	private JButton buttonPrev, buttonNext;
	private HeartRateDrawingPanel drawingPanel;
	private HeartRateDataBuffer DataBufferInterface;
	private JSlider slider;
	private JLabel labelStartDate, labelEndDate, labelCurrentTime, labelTime,labelHour,labelMin;
	private JLabel labelSkipMin,labelMin2nd;
	private JXDatePicker jDatePicker;
	private JPanel north, south, center;
	private JButton buttonSearch,buttonSelect;
	private JTextField textStateDate, textPatient;
	private JComboBox comboBoxStateTime, comboBoxEndTime,comboBoxStartHour,comboBoxStartMin;
	private JComboBox comboBoxSkipMin;
	private String[] TIMES, patientStr;
	private String[] hoursStr;
	private String[] minStr;
	private String connectStr;
	public static final String DATE_FORMAT_TODAY = "yyyy-MM-dd";
	private DataClient dataClient;
	private com.mlnx.pms.core.Patient patient;
	private ArrayList<com.mlnx.pms.core.Patient> patients;
	private Patient patientSelected;
	private Date startDate;
	private float skipMin;
	private JLayer<JPanel> jlayer;
	private WaitLayerUI layerUI;
	private Timer stopper;
	private boolean readyForSouth;
//	private JDatePicker datePicker;

	private void initHoursStr(){
		TIMES = new String[48];
		for(int i=0;i<10;i++){
			TIMES[2*i] = "0" + i + ":00";
			TIMES[2*i+1] = "0" + i + ":30";
		}
		for(int i=10;i<24;i++){
			//int hour = i==0?12:i;
			TIMES[2*i] = i + ":00";
			TIMES[2*i+1] = i + ":30";
		}
	}
	private void initHours()
	{
		hoursStr = new String[24];
		for(int i=0;i<10;i++){
			hoursStr[i] = "0" + i;
		}
		for(int i=10;i<24;i++){
			hoursStr[i] = i+"";
		}
	}
	private void initMin()
	{
		minStr = new String[12];
		for(int i=0;i<2;i++){
			minStr[i] = "0" + i*5;
		}
		for(int i=2;i<12;i++){
			minStr[i] = (i*5)+"";
		}
	}
	public HeartRateFrame(String header, String _connectStr) {
		super(header);
		this.setSize(900, 600);
	    // Get the size of the screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    connectStr = "localhost";
	    // Setup the data from DB
	    dataClient = null;
	    setConnectStr(_connectStr);//setup data client to get patient list
	    
	    startDate = null;
	    skipMin = 1;
	    // Determine the new location of the window
	    int w = this.getSize().width;
	    int h = this.getSize().height;
	    int x = (dim.width-w)/2;
	    int y = (dim.height-h)/2;

	    // Move the window
	    this.setLocation(x, y);
	    setNorth();
	    
	    setCenter();
	    setSouth();
	    this.setBackground(Style.InfoAreaBackgroundColor);
	    this.setVisible(true);
	    patientSelected = null;//new com.medlinx.core.Patient();
	    setupLoadingUI();
	    readyForSouth = false;
	    enableComponents(south,false);
	    setupListFromDB();
	}

	private void setupLoadingUI()
	{
		layerUI = new WaitLayerUI();
		jlayer = new JLayer<JPanel>(center, layerUI);
	    this.add(jlayer);
	    stopper = new Timer(1000, new ActionListener() {
		      public void actionPerformed(ActionEvent ae) {
		    	  //DebugTool.printLogDebug("timer is on!!"+drawingPanel.getDataBuffer().isLoading());
		          if(drawingPanel!=null&&!drawingPanel.getDataBuffer().isLoading())
		          {
		        	  //DebugTool.printLogDebug("stop!!");
		        	  enableComponents(north,true);
		        	  if(readyForSouth)
		        		  enableComponents(south,true);
		        	  layerUI.stop();
		          }
		        }
		      });
	    //stopper.start();
	    center.remove(drawingPanel);
	    Patient patientTemp = new Patient(); // fake patient
		for (int indexC = 0; indexC<2; ++indexC)
			patientTemp.setChannelFlag(indexC);
		patient = new com.mlnx.pms.core.Patient();
		patient.setId(-1);
		patient.setName("张三");
		patient.setGender(Gender.FEMALE);
		DataBufferInterface = new HeartRateDataBuffer(patient, "localhost:8787");
		//DataBufferInterface.start();
		drawingPanel = new HeartRateDrawingPanel(this, DataBufferInterface);
		center.add(drawingPanel, BorderLayout.CENTER);
		center.revalidate();
		center.repaint();
	}
	public void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
        }
    }
	private String today(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_TODAY);
		return sdf.format(cal.getTime());
	}

	private void setNorth() {
		north = new JPanel();
		north.setLayout(new BoxLayout(north, BoxLayout.LINE_AXIS));
		labelStartDate = new JLabel("日期:"); 
		labelTime = new JLabel("     时间");
		labelHour = new JLabel("小时");
		labelMin = new JLabel("分钟    ");
		labelMin2nd = new JLabel("分钟       ");
		labelEndDate = new JLabel("选择病人:");
		labelSkipMin = new JLabel("间隔时间：");

		initHoursStr();
		initHours();
		initMin();
		jDatePicker = new JXDatePicker();
		comboBoxStartHour = new JComboBox(hoursStr);
		comboBoxStartMin = new JComboBox(minStr);
		comboBoxStartHour.setSelectedIndex(0);
		comboBoxStartMin.setSelectedIndex(0);
		comboBoxEndTime = new JComboBox();//(patientStr);
		//comboBoxEndTime.setSelectedIndex(0);
		{//setup the comboxbox to select skip min
			String[] skipMinArray = new String[6];
			skipMinArray[0] = "0.5";
			for (int i = 1;i<skipMinArray.length;++i)
				skipMinArray[i] = ""+(i);
			comboBoxSkipMin = new JComboBox(skipMinArray);
			comboBoxSkipMin.setSelectedIndex(0);
			comboBoxSkipMin.addActionListener (new ActionListener () {
			    public void actionPerformed(ActionEvent e) {
			        float skipMinTemp = 0.5f;
			        if (comboBoxSkipMin.getSelectedIndex()<0)
			        	return;
			        if (comboBoxSkipMin.getSelectedIndex()>0)
			        	skipMinTemp = comboBoxSkipMin.getSelectedIndex();
			        Hashtable<Integer, JLabel> labels =
			                new Hashtable<Integer, JLabel>();
			        for(int i = 0;i<=50;i+=10)
			        	labels.put((int)(i), new JLabel((int)(i*skipMinTemp)+"分钟"));
			        slider.setLabelTable(labels);
			        revalidate();
			        repaint();
			    }
			});
		}
		textStateDate = new JTextField(today());
		textStateDate.addActionListener(this);
		textPatient = new JTextField("请选择病人");
		buttonSearch = new JButton("获取数据");
		buttonSelect = new JButton("");
		north.add(labelStartDate);
		//north.add(textStateDate);
		north.add(jDatePicker);
		Date testDate = Calendar.getInstance().getTime();
		jDatePicker.setDate(testDate);//.setDate()ï¼›
		north.add(labelTime);
		north.add(comboBoxStartHour);
		north.add(labelHour);
		north.add(comboBoxStartMin);
		north.add(labelMin);
		north.add(labelSkipMin);
		north.add(comboBoxSkipMin);
		north.add(labelMin2nd);
		north.add(labelEndDate);
		//north.add(textPatient);
		north.add(comboBoxEndTime);
		north.add(buttonSearch);
		buttonSelect.addActionListener(this);
		buttonSearch.addActionListener(this);
		this.getContentPane().add(north, BorderLayout.NORTH);
	}

	private void setCenter() {
		//default layout 1x1
		center = new JPanel();
		center.setLayout(new BorderLayout());
		Patient patientTemp = new Patient(); // fake patient
		for (int indexC = 0; indexC<6; ++indexC)
			patientTemp.setChannelFlag(indexC);
		patient = new com.mlnx.pms.core.Patient();
		patient.setId(-1);
		patient.setName("张三");
		patient.setGender(Gender.FEMALE);
		DataBufferInterface = new HeartRateDataBuffer(patient, "localhost:8787");
		//DataBufferInterface.start();
		drawingPanel = new HeartRateDrawingPanel(this, DataBufferInterface);
		this.getContentPane().add(center, BorderLayout.CENTER);
	}

	private void setSouth() {
		south = new JPanel();
		labelCurrentTime = new JLabel("当前时间");
		buttonPrev = addButton(SystemResources.prevIcon, "向前");
		buttonNext = addButton(SystemResources.nextIcon, "向后");
		slider = new JSlider();

		slider.setBackground(Color.black);
		//slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(10);
//		slider.setPaintTicks(true);
		slider.setPreferredSize(new Dimension(getWidth()-100,30));
		slider.setPaintLabels(true);
		slider.setMinimum(0);
		slider.setMaximum(50);
		slider.setValue(drawingPanel.getDisplayStartPoint());
		slider.addChangeListener(this);
		Hashtable<Integer, JLabel> labels =
                new Hashtable<Integer, JLabel>();
        for(int i = 0;i<=50;i+=10)
        	labels.put((int)(i), new JLabel((int)(i/2)+"分钟"));
        slider.setLabelTable(labels);
		south.add(buttonPrev,BorderLayout.WEST);
		south.add(slider, BorderLayout.CENTER);
		south.add(buttonNext, BorderLayout.EAST);

		south.setBackground(Color.black);
		this.getContentPane().add(south, BorderLayout.SOUTH);
	}
	public String getConnectStr() {
		return connectStr;
	}

	public void setConnectStr(String connectStr) {
		this.connectStr = connectStr;
		dataClient = DataClientBuilder.newBuilder()
        .withServerHostname(connectStr).build();

	}
	private JButton addButton(ImageIcon icon, String tip){
		JButton button = new JButton();
		button.setIcon(icon);
		button.setSize(Style.HistoryButtonDimension);
		button.setBackground(Style.InfoAreaBackgroundColor);
		button.setForeground(Style.InfoAreaForegroundColor);
		button.addActionListener(this);
		button.setToolTipText(tip);

		removeBorder(button);
		return button;
	}

	private void removeBorder(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		button.setContentAreaFilled(false);
	}

	private class SwingWorkerSetting extends SwingWorker<Void, Void>{
		public SwingWorkerSetting(){}
		@Override
		protected Void doInBackground() throws Exception {
			enableComponents(north,false);
			if (dataClient ==null)
				return null;
			patients = null;
			try {
				patients = (ArrayList<com.mlnx.pms.core.Patient>) dataClient.getAllPatients();
			} catch (ServerProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				dataClient.close();

	        //Assert.assertTrue(patients.size() >= 0);
			if(patients==null||patients.size()==0)
				return null;
			patientStr = new String[patients.size()];
			int indexTemp = 0;
	        for(com.mlnx.pms.core.Patient p:patients)
	        {
	        	
	        	patientStr[indexTemp] = ("编号："+p.getId()+" 姓名："+p.getName()+
	        			" 性别："+p.getGender().toString());
	        	comboBoxEndTime.addItem(patientStr[indexTemp]);
	        	++indexTemp;
	        }
	        comboBoxEndTime.setSelectedIndex(0);
	        textPatient.setText("     编号:"+patientSelected.getPatientID()+" 姓名："+
	        		patientSelected.getPatientName()+" 性别："+patientSelected.getGender());
	        		int indexSelected = 0;
	        		for(com.mlnx.pms.core.Patient p:patients)
	                {
	        			if(p.getId()==patientSelected.getPatientID())
	        				comboBoxEndTime.setSelectedIndex(indexSelected);
	        			++indexSelected;
	                }
	        enableComponents(north,true);
			return null;
		}
		}
	/**
	 * set up the lists of patients from database
	 * By Jianqiao 07072013
	 */
	public void setupListFromDB() {
		// XXX set up the list of patients (i.e. load all patients' information into
		// system)
		SwingWorkerSetting sws = new SwingWorkerSetting();
		sws.execute();
	}
	public void setupListFromDBTest() {
		// XXX set up the list of patients (i.e. load all patients' information into
		// system)
		patients = new ArrayList<com.mlnx.pms.core.Patient>();
		com.mlnx.pms.core.Patient tempPatient = new com.mlnx.pms.core.Patient();
		tempPatient.setGender(Gender.FEMALE);
		tempPatient.setId(100);
		tempPatient.setName("zhangsan");
		patients.add(tempPatient);

		patientStr = new String[patients.size()];
		int indexTemp = 0;
        for(com.mlnx.pms.core.Patient p:patients)
        {
        	patientStr[indexTemp] = (p.getId()+":"+p.getName());
        }
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.buttonNext){
			boolean needLoading = drawingPanel.next();
			if (needLoading)
				startLoadingMask();
			slider.setValue(drawingPanel.getDisplayStartPoint());
			this.repaint();
		}else if (e.getSource() == this.buttonPrev){
			boolean needLoading = drawingPanel.prev();
			slider.setValue(drawingPanel.getDisplayStartPoint());
			if (needLoading)
				startLoadingMask();

			this.repaint();
		}else if (e.getSource() == this.buttonSelect){

			if(patients==null || patients.size()==0)
			{
				JOptionPane.showMessageDialog(this, "请先选择病人");
				return;
			}
			DebugTool.printLogDebug("size of patient list:"+patients.size());
			PatientSelectHeartRateDialog dialog = new PatientSelectHeartRateDialog(patients,this);
			dialog.setVisible(true);
			dialog.repaint();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.setLocation(new Point(screenSize.width/2-dialog.getWidth()/2, screenSize.height/2-dialog.getHeight()/2));
		}else if (e.getSource() == this.buttonSearch)
		{
			// to retrieve data from server
			float intervalMin = 0.0f;
			if (comboBoxSkipMin.getSelectedIndex()==0)
				intervalMin = 0.5f;
			else
				intervalMin = comboBoxSkipMin.getSelectedIndex();
			Date dateTemp = jDatePicker.getDate();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	        String startDateStr = df.format(dateTemp);
			DebugTool.printLogDebug("Time: "+startDateStr+" " +  
			(String)comboBoxStartHour.getSelectedItem()+":"
					+(String)comboBoxStartMin.getSelectedItem()
					+":00.000");
			String startTimeStr = startDateStr+" " +  
			(String)comboBoxStartHour.getSelectedItem()+":"
					+(String)comboBoxStartMin.getSelectedItem()
					+":00.000";
					//textStateDate.getText()+" " +String)comboBoxStateTime.getSelectedItem()+":"
					//+"00.000";
			// parse the data from user's input
			try{
				startDate = dateFormat.parse(startTimeStr);
	        } catch (ParseException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "日期输入格式有误");
	            return;
	        }

		    patient = null;
		    if(comboBoxEndTime.getSelectedIndex()>=0&&
		    		comboBoxEndTime.getSelectedIndex()<patients.size())
		    	patient = patients.get(comboBoxEndTime.getSelectedIndex());

			if (patient==null||patient.getId()<0)
			{
				JOptionPane.showMessageDialog(this, "请先选择病人");
				return;
			}
			this.getContentPane().remove(drawingPanel);
			center.remove(drawingPanel);
			DataBufferInterface = new HeartRateDataBuffer(patient,connectStr);

			drawingPanel = new HeartRateDrawingPanel(this, DataBufferInterface);
			center.add(drawingPanel, BorderLayout.CENTER);
			slider.setValue(drawingPanel.getDisplayStartPoint());
			center.revalidate();
			center.repaint();
			startLoadingMask();
			DataBufferInterface.loadData(startDate,intervalMin);
			layerUI.start();
		} 
	}
	private void startLoadingMask()
	{
		stopper.start();
		readyForSouth = true;
		DataBufferInterface.setLoading(true);
		layerUI.start();
		enableComponents(north,false);
		enableComponents(south,false);
	}
	public com.mlnx.pms.core.Patient getPatient() {
		return patient;
	}

	public void setPatient(com.mlnx.pms.core.Patient patient) {
		this.patient = patient;
	}

	/**
	 * Test this frame.
	 */
	public static void main(String[] args) {
		new HeartRateFrame("test","218.192.1.201");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == slider){
			drawingPanel.setDisplayStartPoint(slider.getValue());
			this.repaint();
		}
	}

	public com.medlinx.core.patient.Patient getPatientSelected() {
		return patientSelected;
	}

	public void setPatientSelected(com.medlinx.core.patient.Patient patientSelected) {
		this.patientSelected = patientSelected;
	}


}