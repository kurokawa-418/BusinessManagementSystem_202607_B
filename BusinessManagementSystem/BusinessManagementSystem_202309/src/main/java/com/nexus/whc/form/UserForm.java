package com.nexus.whc.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/*
 * UserForm.java
 * 
 * UserFormクラス
 */

/*
 * Formクラス
 */

public class UserForm {
	@NotBlank(message = "ユーザIDは必ず入力してください。")
	private String userId;
	@NotBlank(message = "ユーザ名は必ず入力してください。")
	private String userName;
	@NotBlank(message = "権限は必ず入力してください。")
	private String authId;
	@NotBlank(message = "メールアドレスは必ず入力してください。")
	@Email(message = "メールアドレス(xxx@example.comなど)で入力してください。")
	private String mailAddress;

	private String password;

	private Integer seqId;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSeqId() {
		return seqId;
	}

	public void setSeqId(Integer seqId) {
		this.seqId = seqId;
	}
}
