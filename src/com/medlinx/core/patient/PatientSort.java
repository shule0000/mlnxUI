package com.medlinx.core.patient;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PatientSort{

	private String pingyinPatientName;
	private int visibilityState;
	private String groupName;
	private Patient patient;
	
	public PatientSort(Patient patient)
	{
		this.patient = patient;
		pingyinPatientName = RemoveSpace(getPinYin(patient.getPatientName()));
		groupName = buildGroupName();
	}
	
	/*
	 * frist rule to change patientNAME: start with key  
	 * 规则： 全部相同
	 */
	public boolean NameAsInputSearchFrist(String key)
	{
		if (key.length() > patient.getPatientName().length())
			return false;
		else
		{
//			return (key.equals(patient.getPatientName().substring(0, key.length())));
			return patient.getPatientName().startsWith(key);
		}
	}
	
	/*
	 * second rule to change patientNAME  
	 * 规则： 首字母对应
	 */
	public boolean NameAsInputSearchSecond(String key)
	{
		//change to little char
		key = key.toLowerCase();
		
		String pingyingNameArr[] = getPinYin(patient.getPatientName()).split(" ");
		if (pingyingNameArr.length < key.length())
			return false;
		else 
		{
			for (int i = 0; i < key.length(); ++i)
			{
				if (key.charAt(i) != pingyingNameArr[i].charAt(0))
					return false;
			}
		}
		return true;
	}
	
	/*
	 * third rule to change patientNAME 
	 * 规则： 从前面开始部分对于
	 */
	public boolean NameAsInputSearchThird(String key)
	{
		key = key.toLowerCase();
		if (key.length() > pingyinPatientName.length())
			return false;
		else
			return key.equals(pingyinPatientName.substring(0, key.length()));
	}
	
	/*
	 * third rule to change patientNAME 
	 * 规则： 末尾病人编号对应
	 */
	public boolean NameAsInputSearchFourth(String key)
	{
		key = key.toLowerCase();
		if (key.length() > pingyinPatientName.length())
			return false;
		else
			return key.equals(pingyinPatientName.substring(0, key.length()));
	}
	
	/*
	 * remove space in string
	 */
	String RemoveSpace(String string)
	{
		String newString = new String();
		String name[] = string.split(" ");
		for (int i = 0; i < name.length; ++i)
			newString += name[i];
		
		return newString;
	}
	
	public String getPinYin(String inputString) {
		
		//http://www.360doc.com/content/09/0216/20/15103_2563474.shtml#
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		char[] input = inputString.trim().toCharArray();
		StringBuffer output = new StringBuffer("");

		try {
			for (int i = 0; i < input.length; i++) {
				if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
					output.append(temp[0]);
					output.append(" ");
				} else
					output.append(Character.toString(input[i]));
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public int compare(PatientSort another) {
		if (this.pingyinPatientName.compareTo(another.pingyinPatientName) > 0)
			return 1;
		else 
			return -1;
	}
	
	public String buildGroupName()
	{
		if (pingyinPatientName.length() == 0)
			return "#";
		char fristZIMU = pingyinPatientName.charAt(0);
		if (fristZIMU >= 'a' && fristZIMU <= 'z')
			fristZIMU = (char)('A' + fristZIMU - 'a');
		if (fristZIMU >= 'A' && fristZIMU <= 'Z')
			return fristZIMU+"";
		else 
			return '#'+"";
	}

	public String getPingyinPatientName() {
		return pingyinPatientName;
	}

	public void setPingyinPatientName(String pingyinPatientName) {
		this.pingyinPatientName = pingyinPatientName;
	}

	public int getVisibilityState() {
		return visibilityState;
	}

	public void setVisibilityState(int visibilityState) {
		this.visibilityState = visibilityState;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
