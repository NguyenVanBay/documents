package com.mulodo.fiveneed.bean.response;

import java.util.List;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mulodo.fiveneed.entity.TblChat;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.entity.TblProductImage;
import com.mulodo.fiveneed.entity.TblReportViolation;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductDetailBean {
	private TblProduct product;
	private List<TblProductImage> productImageList;
	private List<TblChat> chatList;
	private List<TblReportViolation> reportList;



	public List<TblChat> getChatList() {
		return chatList;
	}

	public void setChatList(List<TblChat> chatList) {
		this.chatList = chatList;
	}

	public List<TblReportViolation> getReportList() {
		return reportList;
	}

	public void setReportList(List<TblReportViolation> reportList) {
		this.reportList = reportList;
	}

	public TblProduct getProduct() {
		return product;
	}

	public void setProduct(TblProduct product) {
		this.product = product;
	}

	public List<TblProductImage> getProductImageList() {
		return productImageList;
	}

	public void setProductImageList(List<TblProductImage> productImageList) {
		this.productImageList = productImageList;
	}

}
