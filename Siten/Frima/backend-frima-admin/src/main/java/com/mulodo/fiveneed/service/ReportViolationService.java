package com.mulodo.fiveneed.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulodo.fiveneed.bean.ReportViolationBean;
import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.entity.TblReportViolation;
import com.mulodo.fiveneed.repository.DataTypeRepository;
import com.mulodo.fiveneed.repository.ProductRepository;
import com.mulodo.fiveneed.repository.ReportViolationRepository;

@Service("ReportViolationService")
@Transactional(rollbackFor = Exception.class)
public class ReportViolationService extends BaseService {
	@Autowired
	ReportViolationRepository reportRepo;

	@Autowired
	private DataTypeRepository dataRepo;
	@Autowired
	ProductRepository productRepo;

	public void searchReport(int page, int size, String sortBy, String sortType, Long createdBy, String createdAtFrom,
			String createdAtTo, List<Integer> statusList, List<String> statusUploadList, ResponseBean response)
			throws Exception {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);

		if (!"id".equalsIgnoreCase(sortBy) && !"created_at".equalsIgnoreCase(sortBy)) {
			order=order.ignoreCase();
		}

		Date createdAtFromDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		if (!createdAtFrom.equals("0")) {
			Date d = dateFormat.parse(createdAtFrom);
			Timestamp datetime = new Timestamp(d.getTime());
			createdAtFromDate = DateUtils.beginOfDay(datetime);
		}

		Date createdAtToDate = null;

		if (!createdAtTo.equals("0")) {
			Date d = dateFormat.parse(createdAtTo);
			Timestamp datetime = new Timestamp(d.getTime());
			createdAtToDate = DateUtils.endOfDay(datetime);

		}

		int statusSize = 0;
		if (CommonUtil.isEmpty(statusList)) {
			statusList = new Vector<>();
			statusList.add(-1);
			statusSize = 0;
		} else
			statusSize = statusList.size();

		int statusUploadSize = 0;
		if (statusUploadList.size() == 1 && statusUploadList.get(0).equals("")) {
			statusUploadSize = 0;
		} else
			statusUploadSize = statusList.size();

		Page<TblReportViolation> resultPage = reportRepo.searchReport(createdAtFromDate, createdAtToDate, createdBy,
				statusList, statusSize, statusUploadList, statusUploadSize,
				new PageRequest(page, size, new Sort(order)));
		response.setData(resultPage);

	}

	public void getReportViolation(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblReportViolation report = reportRepo.findOne(id);
		if (report == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		}
		TblProduct product = productRepo.findOne(report.getProductId());

		ReportViolationBean bean = new ReportViolationBean(product, report);
		bean.setUser(userDao.findOne(report.getCreatedBy()));
		bean.setDataType(dataRepo.findOne(report.getDataTypeId()));
		response.setData(bean);
	}

	public void updateReport(long id, TblReportViolation report, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblReportViolation reportUpdate = reportRepo.findOne(id);
		if (report == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_UPDATE_DATA);
			return;
		}
		reportUpdate.setStatus(report.getStatus());
		reportUpdate.setUpdatedBy(checkuser.getId());
		reportUpdate.setUpdatedAt(getCurrentTime());

		reportRepo.save(reportUpdate);

	}

}
