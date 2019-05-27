package com.mulodo.fiveneed.controller;

import java.math.BigDecimal;

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
import com.mulodo.fiveneed.service.UserService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class MemberController extends BaseController {
	@Autowired
	UserService userService;

	@RequestMapping(value = "/member", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> searchMember(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
			@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
			@RequestParam(value = "user_name", required = false, defaultValue = "") String username,
			@RequestParam(value = "email", required = false, defaultValue = "") String email,
			@RequestParam(value = "revenueFrom", required = false, defaultValue = "0") BigDecimal revenueFrom,
			@RequestParam(value = "revenueTo", required = false, defaultValue = "0") BigDecimal revenueTo,
			@RequestParam(value = "productFrom", required = false, defaultValue = "0") Long productFrom,
			@RequestParam(value = "productTo", required = false, defaultValue = "0") Long productTo) {
		ResponseBean response = new ResponseBean();
		try {
			userService.searchMember(page, size, sortBy, sortType, id, username, email, revenueFrom, revenueTo,
					productFrom, productTo, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/member/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> getMember(@PathVariable("id") int id) {
		ResponseBean response = new ResponseBean();
		try {
			userService.getMember(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/member/leave/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> leaveMember(@PathVariable("id") int id) {
		ResponseBean response = new ResponseBean();
		try {
			userService.leaveMember(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

}
