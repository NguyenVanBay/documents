package com.mulodo.fiveneed.bean.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListDetailPaymentRequestBean {
	@JsonProperty("status")
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}



}
