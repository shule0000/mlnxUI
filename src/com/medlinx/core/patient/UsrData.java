package com.medlinx.core.patient;

import java.util.List;
import java.util.Map;

import com.mlnx.pms.core.Group;


public class UsrData {
	private static UsrData _instance;
	
	private static List<Group> groupList; // group列表
	private static Map<Long, PatientList> patientGroupMap; // 不同group的病人列表

	/**
	 * 更新所有数据
	 */
	public static String renewAllData(){
		return "更新所有数据成功";
	}
	
}
