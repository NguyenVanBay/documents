package com.mulodo.fiveneed.bean.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DashboardResponseBean {
	private long membership;
	private long numberProduct;
	private long waitingDeposit;
	private long waitingDelivery;
	private long pendingReceipt;
	private long alertWaitingDeposit;
	private long alertWaitingDelivery;
	private long alertPendingReceipt;
	private long transferRequest;
	private long newInquiries;
	private long newViolationReported;

	public long getMembership() {
		return membership;
	}

	public void setMembership(long membership) {
		this.membership = membership;
	}

	public long getNumberProduct() {
		return numberProduct;
	}

	public void setNumberProduct(long numberProduct) {
		this.numberProduct = numberProduct;
	}

	public long getWaitingDeposit() {
		return waitingDeposit;
	}

	public void setWaitingDeposit(long waitingDeposit) {
		this.waitingDeposit = waitingDeposit;
	}

	public long getWaitingDelivery() {
		return waitingDelivery;
	}

	public void setWaitingDelivery(long waitingDelivery) {
		this.waitingDelivery = waitingDelivery;
	}

	public long getPendingReceipt() {
		return pendingReceipt;
	}

	public void setPendingReceipt(long pendingReceipt) {
		this.pendingReceipt = pendingReceipt;
	}

	public long getAlertWaitingDeposit() {
		return alertWaitingDeposit;
	}

	public void setAlertWaitingDeposit(long alertWaitingDeposit) {
		this.alertWaitingDeposit = alertWaitingDeposit;
	}

	public long getAlertWaitingDelivery() {
		return alertWaitingDelivery;
	}

	public void setAlertWaitingDelivery(long alertWaitingDelivery) {
		this.alertWaitingDelivery = alertWaitingDelivery;
	}

	public long getAlertPendingReceipt() {
		return alertPendingReceipt;
	}

	public void setAlertPendingReceipt(long alertPendingReceipt) {
		this.alertPendingReceipt = alertPendingReceipt;
	}

	public long getTransferRequest() {
		return transferRequest;
	}

	public void setTransferRequest(long transferRequest) {
		this.transferRequest = transferRequest;
	}

	public long getNewInquiries() {
		return newInquiries;
	}

	public void setNewInquiries(long newInquiries) {
		this.newInquiries = newInquiries;
	}

	public long getNewViolationReported() {
		return newViolationReported;
	}

	public void setNewViolationReported(long newViolationReported) {
		this.newViolationReported = newViolationReported;
	}


}
