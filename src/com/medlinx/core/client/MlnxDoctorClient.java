package com.medlinx.core.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ui.medlinx.com.extra.SettingParameters;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.medlinx.core.patient.DeviceInfo;
import com.medlinx.core.patient.Patient;
import com.medlinx.core.patient.PatientEventList;
import com.medlinx.core.patient.PatientInfo;
import com.mlnx.pms.client.DataClient;
import com.mlnx.pms.client.DataClientBuilder;
import com.mlnx.pms.client.ServerProcessingException;
import com.mlnx.pms.core.Device;
import com.mlnx.pms.core.Group;
import com.mlnx.pms.core.Patient.Gender;
import com.mlnx.pms.core.PatientEvent;
import com.mlnx.pms.core.PatientGroup;
import com.mlnx.pms.core.User;
import com.mlnx.pms.pojo.ResponseRegisterUser;

public class MlnxDoctorClient {

	private static DataClient client = null;
	private static boolean isException = false;

	public synchronized static void closeDataClient() {
		if (client != null)
			client.close();
	}

	private static void testCreateClient() {
		if (isException) {
			if (client != null) {
				client.close();
				client = null;
			}
			client = DataClientFactory.getLoginDataClient();
			isException = false;
		}
	}

	/**
	 * 用户登入
	 * 
	 * @return
	 */
	public synchronized static User logIn() {
		SettingParameters settingParameters = SettingParameters.getInstance();
		client = DataClientBuilder
				.newBuilder()
				.withServerHostname(settingParameters.getIpString())
				.withCredentials(settingParameters.getUserID(),
						settingParameters.getPwd()).build();
		if (client == null)
			return null;
		try {
			User user = client.logIn();
			if (user == null) {
				client.close();
				client = null;
			}
			return user;
		} catch (ServerProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (client != null) {
			client.close();
			client = null;
		}
		return null;
	}

	/**
	 * 用户注册
	 * 
	 * @param registerUser
	 * @throws IOException
	 * @throws ServerProcessingException
	 */
	public static ResponseRegisterUser userRegister(User registerUser)
			throws ServerProcessingException, IOException {

		SettingParameters parameter = SettingParameters.getInstance();
		DataClient client = DataClientBuilder.newBuilder()
				.withServerHostname(parameter.getIpString()).build();
		ResponseRegisterUser responseRegisterUser = client
				.registUser(registerUser);
		client.close();
		return responseRegisterUser;
	}

	/**
	 * 增加组
	 * 
	 * @param addGroup
	 * @return
	 */
	public synchronized static Group buildGroup(Group addGroup) {
		Group group = null;
		try {
			group = client.addGroup(addGroup);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return group;
	}

	/**
	 * 删除组
	 */
	public synchronized static Group deleteGroup(Group deleteGroup) {
		Group group = null;
		try {
			group = client.deleteGroup(deleteGroup);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return group;
	}

	/**
	 * 根据手机或者身份证查找病人
	 */
	public synchronized static List<com.mlnx.pms.core.Patient> searchPatient(
			com.mlnx.pms.core.Patient patient) {
		List<com.mlnx.pms.core.Patient> p = null;
		try {
			p = client.searchPatient(patient);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return p;
	}

	/**
	 * 保存病人所在组
	 */
	public synchronized static PatientGroup addPatientGroup(
			PatientGroup patientGroup) {
		PatientGroup p = null;
		try {
			p = client.addPatientGroup(patientGroup);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return p;
	}

	/**
	 * 删除该组下的病人
	 */
	public synchronized static PatientGroup deletePatientGroup(
			PatientGroup patientGroup) {
		PatientGroup p = null;
		try {
			p = client.deletePatientGroup(patientGroup);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return p;
	}

	/**
	 * 增加病人资料
	 */
	public synchronized static com.mlnx.pms.core.Patient addPatient(
			com.mlnx.pms.core.Patient addPatient) {
		com.mlnx.pms.core.Patient g = null;
		try {
			g = client.addPatient(addPatient);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return g;
	}

	/**
	 * 修改病人资料
	 */
	public synchronized static com.mlnx.pms.core.Patient modifyPatient(
			com.mlnx.pms.core.Patient patient) {
		com.mlnx.pms.core.Patient g = null;
		try {
			g = client.modifyPatient(patient);
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return g;
	}

	/**
	 * 查找所有组
	 * 
	 * @return
	 */
	public synchronized static List<Group> getAllGroup() {
		List<Group> groups = new ArrayList<Group>();
		try {
			groups = client.getAllGroups();
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return groups;
	}

	/**
	 * 查找组下所有的病人
	 */
	public synchronized static List<Patient> getGroupPatients(long groupId) {
		List<Patient> patients = new ArrayList<Patient>();
		try {
			List<com.mlnx.pms.core.Patient> patientList = client
					.seachGroupPatients(groupId);
			if (patientList == null)
				return null;
			for (com.mlnx.pms.core.Patient patient : patientList) {
				PatientInfo info = new PatientInfo();
				info.setId(patient.getId());
				info.setName(patient.getName());
				switch (patient.getGender()) {
					case FEMALE :
						info.setGender(Gender.FEMALE);
						break;
					case MALE :
						info.setGender(Gender.MALE);
						break;
					case OTHER :
						info.setGender(Gender.OTHER);
						break;
				}
				info.setAge(patient.getAge());
				info.setContact(patient.getContact());
				info.setBirthday(patient.getBirthday());
				info.setLastFourNumber(patient.getLastFourNumber());
				info.setPastMedicalHistory(patient.getPastMedicalHistory());
				info.setRemark(patient.getRemark());
				patients.add(new Patient(info, groupId));
			}
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return patients;
	}

	/**
	 * 查询所有设备
	 * 
	 * @return
	 */
	public synchronized static List<Device> getAllDevices() {
		List<Device> devices = null;
		try {
			devices = client.getAllDevices();
		} catch (ServerProcessingException e) {
			e.printStackTrace();
			isException = true;
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();

		return devices;
	}

	public synchronized static boolean configureDevicePatient(Device device) {
		boolean sucess = false;
		try {
			client.configureDevicePatient(device);
			sucess = true;
		} catch (ServerProcessingException e) {
			e.printStackTrace();
			isException = true;
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return sucess;
	}

	public synchronized static boolean configureDevice(Device device) {
		boolean sucess = false;
		try {
			client.configureDevice(device);
			sucess = true;
		} catch (ServerProcessingException e) {
			e.printStackTrace();
			isException = true;
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return sucess;
	}

	/**
	 * 查找所在组的在线病人
	 */
	public synchronized static List<Patient> getOnlinePatients(
			List<Integer> patientIDList) {
		List<Patient> patients = new ArrayList<Patient>();
		try {
			List<Device> devices = client
					.seachGroupOnlinePatients(patientIDList);
			if (devices == null)
				return null;
			for (Device device : devices) {

				PatientInfo info = new PatientInfo();
				info.setId(device.getPatientId());

				DeviceInfo devInfo = new DeviceInfo(device);

				Patient p = new Patient(info, (long) 0);
				p.setDevInfo(devInfo);
				patients.add(p);
			}
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return patients;
	}

	/**
	 * 获取心电数据存在时间事件
	 * 
	 * @param pid
	 * @param start
	 * @param end
	 * @return
	 */
	public synchronized static PatientEventList getPatientEvents(int pid,
			Date start, Date end) {
		List<PatientEvent> pelist = null;
		PatientEventList pellist = null;
		try {
			pelist = client.getPatientEvents(pid, start, end);
			if (pelist != null && pelist.size() != 0) {
				pellist = new PatientEventList();
				pellist.setList(pelist);
				pellist.setRange(start, end);
			}
		} catch (JSONObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			isException = true;
		}
		testCreateClient();
		return pellist;

	}

}
