package com.mulodo.fiveneed.bean.request;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mulodo.fiveneed.bean.RequestBean;

public class ListInfoPaymentRequestBean extends RequestBean {
	@JsonProperty("status_request")
	private int StatusRequest;
	
	@JsonProperty("request_date_start")
	private Long RequestDateStart;
	
	@JsonProperty("request_date_end")
	private Long RequestDateEnd;

	public int getStatusRequest() {
		return StatusRequest;
	}

	public void setStatusRequest(int statusRequest) {
		StatusRequest = statusRequest;
	}

	public Long getRequestDateStart() {
		return RequestDateStart;
	}

	public void setRequestDateStart(Long requestDateStart) {
		RequestDateStart = requestDateStart;
	}

	public Long getRequestDateEnd() {
		return RequestDateEnd;
	}

	public void setRequestDateEnd(Long requestDateEnd) {
		RequestDateEnd = requestDateEnd;
	}

	
	
}
