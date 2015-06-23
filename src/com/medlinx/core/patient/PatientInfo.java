package com.medlinx.core.patient;

import javax.xml.bind.annotation.XmlRootElement;

import com.mlnx.pms.core.Patient.Gender;

@XmlRootElement(name = "patient")
public class PatientInfo implements Cloneable {

	private Integer id = -1;
    private String name = "";
    private Gender gender = Gender.OTHER;
    private Integer age;
    private String remark;
    private String birthday;
    private String lastFourNumber;
    private String pastMedicalHistory;
    private String contact;

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Gender getGender() {

        return gender;
    }

    public void setGender(Gender gender) {

        this.gender = gender;
    }

    public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLastFourNumber() {
		return lastFourNumber;
	}

	public void setLastFourNumber(String lastFourNumber) {
		this.lastFourNumber = lastFourNumber;
	}

	public String getPastMedicalHistory() {
		return pastMedicalHistory;
	}

	public void setPastMedicalHistory(String pastMedicalHistory) {
		this.pastMedicalHistory = pastMedicalHistory;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

    @Override
    public Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}
