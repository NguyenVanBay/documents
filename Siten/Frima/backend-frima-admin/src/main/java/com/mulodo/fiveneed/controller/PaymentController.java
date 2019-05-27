package com.mulodo.fiveneed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.service.PaymentService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class PaymentController extends BaseController {
	@Autowired
	PaymentService paymentService;

	/**
	 * API F1
	 * 
	 * @author trann
	 * 
	 */
	@RequestMapping(value = "/request_payment", method = RequestMethod.GET)
	public ResponseEntity<?> searchRequestPayment(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
			@RequestParam(value = "status_request_list", required = false, defaultValue = "") String statusRequest,
			@RequestParam(value = "request_date_start", required = false, defaultValue = "0") String requestDateStart,
			@RequestParam(value = "request_date_end", required = false, defaultValue = "0") String requestDateEnd) {
		ResponseBean response = new ResponseBean();
		try {
			paymentService.searchRequestPayment(page, size, sortBy, sortType, statusRequest, requestDateStart,
					requestDateEnd, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	/**
	 * API F2
	 * 
	 * @author trann
	 */
	@RequestMapping(value = "/request_payment/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getRequestPayment(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			paymentService.getRequestPayment(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/request_payment/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePayment(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			paymentService.updatePayment(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
}
