package com.innvo.web.rest.dto;

import java.math.BigDecimal;

public class FinancialDTO {
	
	String projectName;
	
	String recordtypeName;
	
	String fiscalyearValue;
	
	String requeststateName;
	
	BigDecimal amountrequested;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRecordtypeName() {
		return recordtypeName;
	}

	public void setRecordtypeName(String recordtypeName) {
		this.recordtypeName = recordtypeName;
	}

	public String getFiscalyearValue() {
		return fiscalyearValue;
	}

	public void setFiscalyearValue(String fiscalyearValue) {
		this.fiscalyearValue = fiscalyearValue;
	}

	public BigDecimal getAmountrequested() {
		return amountrequested;
	}

	public void setAmountrequested(BigDecimal amountrequested) {
		this.amountrequested = amountrequested;
	}

	public String getRequeststateName() {
		return requeststateName;
	}

	public void setRequeststateName(String requeststateName) {
		this.requeststateName = requeststateName;
	}
	
}
