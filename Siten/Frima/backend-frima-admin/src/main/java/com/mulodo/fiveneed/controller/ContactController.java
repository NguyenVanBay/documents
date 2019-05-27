package com.mulodo.fiveneed.controller;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblQuestion;
import com.mulodo.fiveneed.service.MasterDataService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class ContactController extends BaseController {

	@Autowired
	MasterDataService dataService;
	
	@RequestMapping(value = "/contact", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ResponseBean> findAllContact(
			@RequestParam(value = "status_request_list", required = false, defaultValue = "") String statusRequest,
			@RequestParam(value = "type", required = false, defaultValue = "0") Long type,
			@RequestParam(value = "category", required = false, defaultValue = "0") Long category,
			@RequestParam(value = "id", required = false, defaultValue = "0") Long id,
			@RequestParam(value = "content", required = false, defaultValue = "") String content,
			@RequestParam(value = "userName", required = false, defaultValue = "") String userName,
			@RequestParam(value = "replyContent", required = false, defaultValue = "0") String replyContent,
			@RequestParam(value = "createdBy", required = false, defaultValue = "0") Long createdBy,
			@RequestParam(value = "replyBy", required = false, defaultValue = "") Long replyBy,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
			@RequestParam(value = "request_date_start", required = false, defaultValue = "0") String requestDateStart,
			@RequestParam(value = "request_date_end", required = false, defaultValue = "0") String requestDateEnd) {
		

		ResponseBean response = new ResponseBean();
		try {
			dataService.findAllContact(userName, statusRequest, type ,category, id, content, replyContent, createdBy, replyBy, page, size, sortBy, sortType, requestDateStart,requestDateEnd, response);

			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/contact/{id}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<ResponseBean> findOne(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.findOne(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> addConTact(@RequestBody TblQuestion question) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.createContact(question, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> setConTact(@RequestBody TblQuestion question) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.updateContact(question, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	
}
