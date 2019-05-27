package com.mulodo.fiveneed.controller;

import java.math.BigDecimal;
import java.util.List;

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
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstCategory;
import com.mulodo.fiveneed.entity.TblQuestion;
import com.mulodo.fiveneed.entity.TblReportViolation;
import com.mulodo.fiveneed.service.MasterDataService;
import com.mulodo.fiveneed.service.ReportViolationService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class ReportViolationController extends BaseController {
	@Autowired
	ReportViolationService reportService;
	
	@Autowired
	MasterDataService dataService;

	@RequestMapping(value = "/report_violation", method = RequestMethod.GET)
	public ResponseEntity<?> searchReportViolation(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
			@RequestParam(value = "created_by", required = false, defaultValue = "0") Long createdBy,
			@RequestParam(value = "date_start", required = false, defaultValue = "0") String dateStart,
			@RequestParam(value = "date_end", required = false, defaultValue = "0") String dateEnd,
			@RequestParam(value = "upload_status", required = false, defaultValue = "") String uploadStatus,
			@RequestParam(value = "status", required = false, defaultValue = "") String status) {
		ResponseBean response = new ResponseBean();
		List<Integer> statusList = CommonUtil.arrayStrToIntVector(status, Constants.SEPERATE_CHARACTER);

		List<String> statusUploadList = CommonUtil.arrayToVector(uploadStatus.split(Constants.SEPERATE_CHARACTER));

		try {
			reportService.searchReport(page, size, sortBy, sortType, createdBy, dateStart, dateEnd, statusList,
					statusUploadList, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/report_violation/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getReportViolation(@PathVariable("id") long id) {
		ResponseBean response = new ResponseBean();
		try {
			reportService.getReportViolation(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/report_violation/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateReport(@PathVariable("id") long id, @RequestBody TblReportViolation report) {
		ResponseBean response = new ResponseBean();
		try {
			reportService.updateReport(id,report, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
}
