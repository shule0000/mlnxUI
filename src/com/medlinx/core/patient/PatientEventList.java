package com.medlinx.core.patient;


import java.util.Date;
import java.util.List;

import com.mlnx.pms.core.PatientEvent;

public class PatientEventList {

	private List<PatientEvent> list;
	
	private Date start, end;
	
	public List<PatientEvent> getList(){
		return list;
	}

	public void setList(List<PatientEvent> list) {
		this.list = list;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(PatientEvent event : list){
			if(event != null)
				sb.append(event.toString());
		}
		return sb.toString();
		
	}

	public void setRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}


}
