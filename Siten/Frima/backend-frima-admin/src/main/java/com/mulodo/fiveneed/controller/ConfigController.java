package com.mulodo.fiveneed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.dto.ConfigBankDTO;
import com.mulodo.fiveneed.dto.ConfigSystemDTO;
import com.mulodo.fiveneed.service.MasterDataService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class ConfigController extends BaseController {

	@Autowired
	MasterDataService dataService;
	
	@RequestMapping(value = "/configBank", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> updateConfig(@RequestBody ConfigBankDTO config) {
		
		ResponseBean response = new ResponseBean();
		try {
			dataService.updateConfigBank(config, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/configSystem", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> updateConfig(@RequestBody ConfigSystemDTO config) {
		
		ResponseBean response = new ResponseBean();
		try {
			dataService.updateConfigSystem(config, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/configBank", method = RequestMethod.GET)
	public ConfigBankDTO   getConfigBank() {
		return dataService.getConfigBank();
	}
	
	@RequestMapping(value = "/configSystem", method = RequestMethod.GET)
	public ConfigSystemDTO getConfigSystem() {
		return dataService.getConfigSystem();
	}
	
}
