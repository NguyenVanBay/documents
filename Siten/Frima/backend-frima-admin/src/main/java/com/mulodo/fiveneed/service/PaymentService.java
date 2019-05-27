package com.mulodo.fiveneed.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.ConfigParam;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstDataType;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.entity.TblRequestPayment;
import com.mulodo.fiveneed.entity.TblRequestPaymentDetail;
import com.mulodo.fiveneed.repository.ConfigRepository;
import com.mulodo.fiveneed.repository.DataTypeRepository;
import com.mulodo.fiveneed.repository.PaymentRepository;
import com.mulodo.fiveneed.repository.PaymentRepositoryJPA;
import com.mulodo.fiveneed.repository.ProductOrderRepository;
import com.mulodo.fiveneed.repository.RequestPaymentDetailRepositoryJPA;

@Service("PaymentService")
@Transactional(rollbackFor = Exception.class)
public class PaymentService extends BaseService {

	@Autowired
	PaymentRepository paymentDao;

	@Autowired
	RequestPaymentDetailRepositoryJPA paymentDetailDao;

	@Autowired
	PaymentRepositoryJPA paymentDaoJpa;
	@Autowired
	DataTypeRepository dataRepo;

	@Autowired
	ProductOrderRepository poRepo;

	@Autowired
	ConfigRepository configRepo;

	public void getRequestPayment(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblRequestPayment payment = paymentDaoJpa.findOne(id);
		if (payment == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		} else {
			MstDataType bank = dataRepo.findOne(payment.getBankId());
			if (bank != null)
				payment.setBank(bank.getName());

			MstDataType accType = dataRepo.findOne(payment.getAccountType());
			if (accType != null)
				payment.setAccountTypeName(accType.getName());
			response.setData(payment);
		}
	}

	public void searchRequestPayment(int page, int size, String sortBy, String sortType, String statusRequest,
			String requestDateStart, String requestDateEnd, ResponseBean response) throws ParseException {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);
		if (!"id".equalsIgnoreCase(sortBy) && !"total".equalsIgnoreCase(sortBy)) {
			order.ignoreCase();
		}
		Date createdAtFromDate = null;
		Date createdAtToDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		if (!requestDateStart.equals("0")) {
			Date d = dateFormat.parse(requestDateStart);
			Timestamp datetime = new Timestamp(d.getTime());
			createdAtFromDate = DateUtils.beginOfDay(datetime);
		}

		if (!requestDateEnd.equals("0")) {
			Date d = dateFormat.parse(requestDateEnd);
			Timestamp datetime = new Timestamp(d.getTime());
			createdAtToDate = DateUtils.endOfDay(datetime);

		}

		List<String> statusList = CommonUtil.arrayToVector(statusRequest.split(Constants.SEPERATE_CHARACTER));
		List<Integer> statusIntList = new LinkedList<>();
		statusList.forEach(data -> {
			try {
				if (!CommonUtil.isEmpty(data))
					statusIntList.add(Integer.parseInt(data));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});

		int statusSize = 0;
		if (statusIntList.isEmpty()) {
			statusIntList.add(0);
			statusSize = 0;
		} else {
			statusSize = statusIntList.size();
		}

		Page<TblRequestPayment> resultPage = paymentDaoJpa.searchRequestPayment(createdAtFromDate, createdAtToDate,
				statusIntList, statusSize, new PageRequest(page, size, new Sort(order)));
		response.setData(resultPage);
	}

	public void updatePayment(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblRequestPayment payment = paymentDaoJpa.findOne(id);
		if (payment == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		} else {
			Date currentTime = getCurrentTime();
			payment.setUpdatedAt(currentTime);
			payment.setStatus(TblRequestPayment.STATUS_PAID);
			paymentDaoJpa.save(payment);

			BigDecimal total = payment.getTotal();

			// Lấy ra danh sách product_order mà có thể chia tiền.
			// Điều kiện: không quá hạn,
			Sort.Order order = new Sort.Order(Direction.ASC, "receivedAt");
			int date = Integer.parseInt(
					configRepo.findOneByParamName(ConfigParam.EXPIRED_REQUEST_PAYMENT.getName()).getParamValue());
			Date expiredDate = DateUtils.getDayBefore(currentTime, date);

			Page<TblProductOrder> resultPage = poRepo
					.findByUserSell_IdAndStatusGreaterThanAndRemainPaymentGreaterThanAndReceivedAtGreaterThanEqual(
							payment.getCreatedBy(), TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE, BigDecimal.ZERO,
							expiredDate, new PageRequest(0, 1000000, new Sort(order)));

			List<TblProductOrder> poList = resultPage.getContent();
			List<TblProductOrder> updatedPoList = new LinkedList<>();
			// Thực hiện chia tiền cho các product order
			int i = 0;
			for (TblProductOrder o : poList) {
				if (total.compareTo(BigDecimal.ZERO) == 0)
					break;

				TblRequestPaymentDetail detail = new TblRequestPaymentDetail();
				detail.setRequestPaymentId(payment.getId());
				detail.setProductOrderId(o.getId());
				detail.setOrderNo(i);

				if (total.compareTo(BigDecimal.ZERO) > 0 && total.compareTo(o.getRemainPayment()) >= 0) {
					total = total.subtract(o.getRemainPayment());
					detail.setMoney(o.getRemainPayment());
					o.setRemainPayment(BigDecimal.ZERO);
					o.setStatus(TblProductOrder.STATUS_5_COMPLETED);
					o.setPaymentSellerAt(getCurrentTime());
					o.setUpdatedAt(o.getPaymentSellerAt());
					// o.setUpdatedBy(checkuser.getId());

				} else if (total.compareTo(o.getRemainPayment()) < 0) {
					o.setRemainPayment(o.getRemainPayment().subtract(total));
					detail.setMoney(total);
					o.setStatus(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER);
					o.setUpdatedAt(getCurrentTime());
					// o.setUpdatedBy(checkuser.getId());
					total = BigDecimal.ZERO;
				}

				if (detail.getMoney().compareTo(BigDecimal.ZERO) > 0) {
					paymentDetailDao.save(detail);
					i++;
				}
				updatedPoList.add(o);
			}

			poRepo.save(updatedPoList);

		}

	}

}
