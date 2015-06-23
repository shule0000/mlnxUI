package com.medlinx.core.databuff;

import java.util.ArrayList;
import java.util.List;

import com.medlinx.core.constant.SystemConstant;
import com.medlinx.core.patient.Patient;
import com.mlnx.pms.client.DataClient;

public class SelectPatient implements SelectPatientInterface {

    private String serviceUrl;

    public SelectPatient() {

        serviceUrl = "https://localhost/";
    }

    @Override
    public void setURL(String serviceUrl) {

        this.serviceUrl = serviceUrl;
    }

    public ArrayList<Patient> getAllPatientOnline() {
        List<com.mlnx.pms.core.Patient> output = new ArrayList<com.mlnx.pms.core.Patient>();
        try {
        	DataClient dataClient = SystemConstant.constructDataClient();
        	output = dataClient.getPatientsWithRealTimeEcg();
        } catch (Exception e) {
            // TODO How to handle exceptions?
            e.printStackTrace();
            return null;
        }
        ArrayList<Patient> patients = new ArrayList<Patient>(
                output.size());
        for (com.mlnx.pms.core.Patient patientInfo : output) {
        	Patient tempPatient = new Patient();
        	tempPatient.setChannelFlag(0);
        	tempPatient.setGender(patientInfo.getGender().toString());
        	tempPatient.setPatientID(patientInfo.getId());
        	tempPatient.setPatientName(patientInfo.getName());
            patients.add(tempPatient);
        }
        return patients;
    }

    @Override
    public int checkServerStatus() {

        // TODO Implement
        return 0;
    }
}
