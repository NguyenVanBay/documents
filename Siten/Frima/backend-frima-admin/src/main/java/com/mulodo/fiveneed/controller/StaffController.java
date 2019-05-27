package com.mulodo.fiveneed.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.service.StaffService;

/**
 * StaffController
 *
 * @author phuongdd
 *
 */

@Controller
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class StaffController extends BaseController {

	private static final Logger logger = Logger.getLogger(StaffController.class);

	@Autowired
	private StaffService staffService;

	@RequestMapping(value = "/staff", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ResponseBean> findAllStaff(
			@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
			@RequestParam(value = "user_name", required = false, defaultValue = "") String email,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType) {

		ResponseBean response = new ResponseBean();
		try {
			staffService.findAllStaff(id, email, page, size, sortBy, sortType, response);

			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/staff/{id}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ResponseBean> findOne(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			staffService.findOne(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/staff", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	@ResponseBody
	public ResponseEntity<ResponseBean> create(@RequestBody MstUserAdmin staff) {
		ResponseBean response = new ResponseBean();
		try {
			staffService.create(staff, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/staff", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ResponseBean> update(@RequestBody MstUserAdmin staff) {
		ResponseBean response = new ResponseBean();
		try {
			staffService.update(staff, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

}
