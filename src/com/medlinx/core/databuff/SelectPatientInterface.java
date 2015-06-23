package com.medlinx.core.databuff;

import java.util.ArrayList;

import com.medlinx.core.patient.Patient;

public interface SelectPatientInterface {
public void setURL(String URL);
public ArrayList<Patient> getAllPatientOnline();
public int checkServerStatus();
}
