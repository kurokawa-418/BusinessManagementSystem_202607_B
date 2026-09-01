package com.nexus.whc.models;

import lombok.Data;

@Data
public class CalendarDetail {
	
	private String date;
	private boolean holidayFlg;
	private String comment;
	private Integer dayOfWeek;

}
