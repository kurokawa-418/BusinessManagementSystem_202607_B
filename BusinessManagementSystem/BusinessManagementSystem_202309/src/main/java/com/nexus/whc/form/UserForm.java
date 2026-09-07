package com.nexus.whc.form;

import javax.validation.constraints.NotBlank;

/* UserForm.java
* 
* UserFormクラス
*/
public class UserForm {
	@NotBlank(message = "{COM01E001}")
	private String userId;
	@NotBlank(message = "{COM01E001}")
	private String userName;	
	@NotBlank(message = "{COM01E001}")
	private String authId;
	@NotBlank(message = "{COM01E001}")
	private String mailAddress;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
}
