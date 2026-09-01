package com.nexus.whc.models;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CalendarData{
	
	private String clientName;
	private Integer employeeId;
	private String employeeName;
	private LocalDate yearMonth;
	private String allYearRoundComment;
}
