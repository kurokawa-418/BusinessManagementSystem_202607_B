package com.nexus.whc.form;

public class EmployeeForm {

	private String employeeId;
	private String employeeName;
	private String clientId;
	private String clientName;
	private String hourlyWage;
	private String paidHolidayStd;
	private String deleteFlg;
	private String createdAt;
	private String createdUser;
	private String updatedAt;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getHourlyWage() {
		return hourlyWage;
	}

	public void setHourlyWage(String hourlyWage) {
		this.hourlyWage = hourlyWage;
	}

	public String getPaidHolidayStd() {
		return paidHolidayStd;
	}

	public void setPaidHolidayStd(String paidHolidayStd) {
		this.paidHolidayStd = paidHolidayStd;
	}

	public String getDeleteFlg() {
		return deleteFlg;
	}

	public void setDeleteFlg(String deleteFlg) {
		this.deleteFlg = deleteFlg;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedUser() {
		return updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

	private String updatedUser;
}
