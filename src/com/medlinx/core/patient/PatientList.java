package com.medlinx.core.patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ui.medlinx.com.debug.DebugTool;
import ui.medlinx.com.frame.Main.MLnxClient;

public class PatientList {

	public static List<Patient> recordOnlinePatients = new ArrayList<Patient>();
	public static List<Patient> recordOfflinePatients = new ArrayList<Patient>();

	private HashMap<Integer, Patient> recordSelectedOnlinePatients; // 刷新全部病人时使用
	private List<Integer> reSelectedOnlinePatients; // 自动掉线后重新上线

	private HashMap<Integer, Patient> patients;
	private HashMap<Integer, Patient> onlinePatients;
	private HashMap<Integer, Patient> selectedOnlinePatients;
	private HashMap<Integer, Patient> unSelectedOnlinePatients;

	private List<HashMap<Integer, Patient>> patientHashMapList;

	public PatientList() {

		patients = new HashMap<Integer, Patient>();
		onlinePatients = new HashMap<Integer, Patient>();
		selectedOnlinePatients = new HashMap<Integer, Patient>();
		unSelectedOnlinePatients = new HashMap<Integer, Patient>();

		patientHashMapList = new ArrayList<HashMap<Integer, Patient>>();
		patientHashMapList.add(patients);
		patientHashMapList.add(onlinePatients);
		patientHashMapList.add(selectedOnlinePatients);
		patientHashMapList.add(unSelectedOnlinePatients);

	}

	public PatientList(List<Patient> allPs) {

		this();
		for (Patient patient : allPs) {
			patients.put(patient.getPatientID(), patient);
		}
	}

	public void setRecordSelectedOnlinePatients(
			HashMap<Integer, Patient> recordSelectedOnlinePatients) {
		this.recordSelectedOnlinePatients = recordSelectedOnlinePatients;
		reSelectedOnlinePatients = new ArrayList<Integer>();
	}

	/**
	 * 暂时忽略全部病人在另外一个管理员操作过程中进行增加和删除
	 * 
	 * @param onlinePs
	 *            在线病人列表
	 */
	public void renewOnlinePatient(List<Patient> onlinePs, MLnxClient mLnxClient) {
		HashMap<Integer, Patient> onlinePHashMap = new HashMap<Integer, Patient>();
		for (Patient patient : onlinePs) {
			onlinePHashMap.put(patient.getPatientID(), patient);
		}
		renewOnlinePatient(onlinePHashMap, mLnxClient);
	}

	/**
	 * 暂时忽略全部病人在另外一个管理员操作过程中进行增加和删除
	 * 
	 * @param onlinePs
	 *            在线病人列表
	 */
	public void renewOnlinePatient(HashMap<Integer, Patient> onlinePs,
			MLnxClient mLnxClient) {

		recordOfflinePatients.clear();
		recordOnlinePatients.clear();

		// 判断在线的是否还在线
		for (Iterator iterator = onlinePatients.values().iterator(); iterator
				.hasNext();) {
			Patient patient = (Patient) iterator.next();
			// 已经下线
			if (!onlinePs.containsKey(patient.getPatientID())) {
				removeOnline(patient);
				if (patient.isSelected()) {
					patient.setSelected(false, null);
					removeSelect(patient);
					reSelectedOnlinePatients.add(patient.getPatientID());
				} else
					removeUnselect(patient);
				recordOfflinePatients.add(patient);
			}
		}
		// 判断刚上线的病人
		for (Iterator iterator = onlinePs.values().iterator(); iterator
				.hasNext();) {
			Patient patient = (Patient) iterator.next();
			// 刚上线
			if (!onlinePatients.containsKey(patient.getPatientID())) {
				Patient p = patients.get(patient.getPatientID());
				p.setDevInfo(patient.getDevInfo());
				addOnline(p);

				// 恢复监控
				if (recordSelectedOnlinePatients != null
						&& recordSelectedOnlinePatients.containsKey(p
								.getPatientID())) {
					p.setDrawingPanel(recordSelectedOnlinePatients.get(
							p.getPatientID()).getDrawingPanel());
					p.setSelected(true);
					p.setPatientData(recordSelectedOnlinePatients.get(
							p.getPatientID()).getPatientData());
					addSelect(p);
				} else {
					boolean isSelect = false;
					for (Iterator iterator2 = reSelectedOnlinePatients
							.iterator(); iterator2.hasNext();) {
						Integer patientID = (Integer) iterator2.next();
						if (patientID == p.getPatientID()) {
							isSelect = true;
							p.setSelected(true, mLnxClient);
							addSelect(p);
							iterator2.remove();
							break;
						}
					}
					if (!isSelect)
						addUnselect(p);
				}
				recordOnlinePatients.add(p);
			}
		}
		recordSelectedOnlinePatients = null;

		String string = "";
		for (Patient patient : recordOfflinePatients) {
			string += patient.toString() + "\n";
		}
		if (string.length() > 0)
			DebugTool.printLogDebug("下线病人：\n" + string);
		string = "";
		for (Patient patient : recordOnlinePatients) {
			string += patient.toString() + "\n";
		}
		if (string.length() > 0)
			DebugTool.printLogDebug("上线病人：\n" + string);
	}

	/*
	 * change patient state
	 */
	public void changePatientState(Patient patient) {
		if (patient.isOnline() && !onlinePatients.containsValue(patient)) {
			addOnline(patient);
		} else if (!patient.isOnline() && onlinePatients.containsValue(patient)) {
			removeOnline(patient);
		}

		if (patient.isOnline() && patient.isSelected()
				&& !selectedOnlinePatients.containsValue(patient)) {
			addSelect(patient);
		} else if (!(patient.isOnline() && patient.isSelected())
				&& selectedOnlinePatients.containsValue(patient)) {
			removeSelect(patient);
		}

		if (patient.isOnline() && !patient.isSelected()
				&& !unSelectedOnlinePatients.containsValue(patient)) {
			addUnselect(patient);
		} else if (!(patient.isOnline() && !patient.isSelected())
				&& unSelectedOnlinePatients.containsValue(patient)) {
			removeUnselect(patient);
		}
	}

	private void addOnline(Patient patient) {
		patient.setOnline(true);
		onlinePatients.put(patient.getPatientID(), patient);
	}

	private void removeOnline(Patient patient) {
		onlinePatients.remove(patient.getPatientID());
		patient.setOnline(false);
	}

	private void addSelect(Patient patient) {
		selectedOnlinePatients.put(patient.getPatientID(), patient);
	}

	private void removeSelect(Patient patient) {
		selectedOnlinePatients.remove(patient.getPatientID());
	}

	private void addUnselect(Patient patient) {
		unSelectedOnlinePatients.put(patient.getPatientID(), patient);
	}

	private void removeUnselect(Patient patient) {
		unSelectedOnlinePatients.remove(patient.getPatientID());
	}

	public HashMap<Integer, Patient> getPatients() {
		return patients;
	}

	public HashMap<Integer, Patient> getOnlinePatients() {
		return onlinePatients;
	}

	public HashMap<Integer, Patient> getSelectedOnlinePatients() {
		return selectedOnlinePatients;
	}

	public HashMap<Integer, Patient> getUnSelectedOnlinePatients() {
		return unSelectedOnlinePatients;
	}

	public List<HashMap<Integer, Patient>> getPatientHashMapList() {
		return patientHashMapList;
	}
}
