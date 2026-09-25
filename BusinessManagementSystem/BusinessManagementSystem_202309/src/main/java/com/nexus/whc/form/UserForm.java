package com.nexus.whc.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	@Size(min = 8, max = 16, message = "ユーザIDは8～16桁で入力してください。")
	private String userId;
	@NotBlank(message = "ユーザ名は必ず入力してください。")
	@Size(max = 16, message = "ユーザ名は16文字以内で入力してください。")
	private String userName;
	@NotBlank(message = "権限は必ず入力してください。")
	private String authId;
	@NotBlank(message = "メールアドレスは必ず入力してください。")
	@Email(message = "メールアドレス(xxx@example.comなど)で入力してください。")
	@Size(max = 254, message = "メールアドレスは254文字以内で入力してください。")
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
