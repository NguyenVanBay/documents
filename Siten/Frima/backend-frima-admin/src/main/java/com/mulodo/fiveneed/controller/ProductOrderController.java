package com.mulodo.fiveneed.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.ConfigParam;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.service.ProductOrderService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class ProductOrderController extends BaseController {
	@Autowired
	ProductOrderService productOrderService;

	@RequestMapping(value = "/product_order", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> searchProducts(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,

			@RequestParam(value = "id", required = false, defaultValue = "0") long id,
			@RequestParam(value = "product_id", required = false, defaultValue = "0") long productId,
			@RequestParam(value = "user_sell_id", required = false, defaultValue = "0") long userSellId,
			@RequestParam(value = "product_name", required = false, defaultValue = "")  String productName,
			@RequestParam(value = "user_sell_name", required = false, defaultValue = "") String userSellName,

			@RequestParam(value = "user_buy_id", required = false, defaultValue = "0") long userBuyId,
			@RequestParam(value = "user_buy_name", required = false, defaultValue = "") String userBuyName,

			@RequestParam(value = "status_buy_list", required = false, defaultValue = "") String statusListStr,
			@RequestParam(value = "status_alert_list", required = false, defaultValue = "") String statusAlertListStr) {
		ResponseBean response = new ResponseBean();
		try {
			List<Integer> statusList = CommonUtil.arrayStrToIntVector(statusListStr, Constants.SEPERATE_CHARACTER);

			List<Integer> statusAlertList = CommonUtil.arrayStrToIntVector(statusAlertListStr,
					Constants.SEPERATE_CHARACTER);
			productOrderService.search(page, size, sortBy, sortType, id,productId,productName, userSellId, userSellName, userBuyId,
					userBuyName, statusList, statusAlertList, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/product_order/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> getProductDetail(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			productOrderService.get(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/product_order/confirm_payment_buyer/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> confirmPaymentBuyer(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			productOrderService.confirmPaymentBuyer(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/product_order/un_confirm_payment_buyer/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> unConfirmPaymentBuyer(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			productOrderService.unConfirmPaymentBuyer(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
}
