package com.nexus.whc.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ClientForm {

	@NotNull
	@Min(0)
	@Max(999)
	private Integer clientId;

	@NotBlank
	@Size(max = 50)
	private String clientName;

	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
	private String openTime;

	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
	private String closeTime;

	@NotBlank
	@Pattern(regexp = "^\\d{1,3}(\\.\\d{1,2})?$")
	private String workingTime;

	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
	private String rest1Start;

	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
	private String rest1End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest2Start;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest2End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest3Start;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest3End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest4Start;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest4End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest5Start;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest5End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest6Start;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String rest6End;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String adjustRestTimeStart;

	@Pattern(regexp = "^(|([01]\\d|2[0-3]):[0-5]\\d)$")
	private String adjustRestTimeEnd;

	@Size(max = 100)
	private String comment;

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(String workingTime) {
		this.workingTime = workingTime;
	}

	public String getRest1Start() {
		return rest1Start;
	}

	public void setRest1Start(String rest1Start) {
		this.rest1Start = rest1Start;
	}

	public String getRest1End() {
		return rest1End;
	}

	public void setRest1End(String rest1End) {
		this.rest1End = rest1End;
	}

	public String getRest2Start() {
		return rest2Start;
	}

	public void setRest2Start(String rest2Start) {
		this.rest2Start = rest2Start;
	}

	public String getRest2End() {
		return rest2End;
	}

	public void setRest2End(String rest2End) {
		this.rest2End = rest2End;
	}

	public String getRest3Start() {
		return rest3Start;
	}

	public void setRest3Start(String rest3Start) {
		this.rest3Start = rest3Start;
	}

	public String getRest3End() {
		return rest3End;
	}

	public void setRest3End(String rest3End) {
		this.rest3End = rest3End;
	}

	public String getRest4Start() {
		return rest4Start;
	}

	public void setRest4Start(String rest4Start) {
		this.rest4Start = rest4Start;
	}

	public String getRest4End() {
		return rest4End;
	}

	public void setRest4End(String rest4End) {
		this.rest4End = rest4End;
	}

	public String getRest5Start() {
		return rest5Start;
	}

	public void setRest5Start(String rest5Start) {
		this.rest5Start = rest5Start;
	}

	public String getRest5End() {
		return rest5End;
	}

	public void setRest5End(String rest5End) {
		this.rest5End = rest5End;
	}

	public String getRest6Start() {
		return rest6Start;
	}

	public void setRest6Start(String rest6Start) {
		this.rest6Start = rest6Start;
	}

	public String getRest6End() {
		return rest6End;
	}

	public void setRest6End(String rest6End) {
		this.rest6End = rest6End;
	}

	public String getAdjustRestTimeStart() {
		return adjustRestTimeStart;
	}

	public void setAdjustRestTimeStart(String adjustRestTimeStart) {
		this.adjustRestTimeStart = adjustRestTimeStart;
	}

	public String getAdjustRestTimeEnd() {
		return adjustRestTimeEnd;
	}

	public void setAdjustRestTimeEnd(String adjustRestTimeEnd) {
		this.adjustRestTimeEnd = adjustRestTimeEnd;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
