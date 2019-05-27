package com.mulodo.fiveneed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.entity.TblTodo;
import com.mulodo.fiveneed.service.MasterDataService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class TodoController extends BaseController {

	@Autowired
	MasterDataService dataService;
	
	@RequestMapping(value = "/todo", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> addConTact(@RequestBody TblTodo todo) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.createTodo(todo, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}	
}
