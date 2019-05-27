package com.mulodo.fiveneed.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mulodo.fiveneed.entity.MstUserApp;

public class LoginResponseBean {
	@JsonProperty("access_token")
	private String accessToken = "";
	private String domainHttp;

	private Object user;

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getDomainHttp() {
		return domainHttp;
	}

	public void setDomainHttp(String domainHttp) {
		this.domainHttp = domainHttp;
	}
	
}
