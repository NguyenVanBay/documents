package com.mulodo.fiveneed.bean.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mulodo.fiveneed.bean.RequestBean;

public class LoginRequestBean extends RequestBean {
	@JsonProperty("password")
	private String password;

	@JsonProperty("username")
	private String username;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}



}
