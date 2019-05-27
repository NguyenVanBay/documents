package com.mulodo.fiveneed.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulodo.fiveneed.bean.RequestBean;
import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.bean.request.LoginRequestBean;
import com.mulodo.fiveneed.bean.response.DashboardResponseBean;
import com.mulodo.fiveneed.bean.response.LoginResponseBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.ConfigParam;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.constant.EnvironmentKey;
import com.mulodo.fiveneed.entity.MstDataType;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.entity.TblReportViolation;
import com.mulodo.fiveneed.entity.TblRequestPayment;
import com.mulodo.fiveneed.repository.ConfigRepository;
import com.mulodo.fiveneed.repository.DataTypeRepository;
import com.mulodo.fiveneed.repository.PaymentRepositoryJPA;
import com.mulodo.fiveneed.repository.ProductOrderRepository;
import com.mulodo.fiveneed.repository.ProductRepository;
import com.mulodo.fiveneed.repository.QuestionRepository;
import com.mulodo.fiveneed.repository.ReportViolationRepository;
import com.mulodo.fiveneed.repository.UserRepository;

@Service("UserService")
@Transactional(rollbackFor = Exception.class)
public class UserService extends BaseService {
	@Autowired
	ProductRepository productRepo;

	@Autowired
	ConfigRepository configRepo;

	@Autowired
	private UserRepository profileRepo;

	@Autowired
	private ProductOrderRepository productOrderRepo;

	@Autowired
	private PaymentRepositoryJPA paymentRepo;

	@Autowired
	private ReportViolationRepository reportRepo;

	@Autowired
	private QuestionRepository questionRepo;

	@Autowired
	private DataTypeRepository dataTypeRepo;

	/**
	 * API A001
	 * 
	 * @author ThoMC
	 * @param request
	 * @param response
	 */
	public void login(LoginRequestBean request, ResponseBean response) {
		LoginResponseBean bean = new LoginResponseBean();
		bean.setDomainHttp(environment.getProperty("ftp.http"));
		MstUserAdmin user = userAdminDao.findByEmail(request.getUsername());
		if (user == null || user.getStatus().equals(MstUserAdmin.DELETE)) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}

		// encode password
		String password = CommonUtil.encryptPassword(request.getPassword(), user.getSalt(),
				Integer.parseInt(environment.getProperty(EnvironmentKey.SHA256_LOOPNUMBER_KEY.getKey())));

		// compare password and password data
		if (password.equals(user.getPassword())) {
			if (CommonUtil.isEmpty(user.getAccessToken())) {
				String token = UUID.randomUUID().toString();
				user.setAccessToken(token);
				bean.setAccessToken(token);
			} else {
				bean.setAccessToken(user.getAccessToken());
			}

			userAdminDao.save(user);

			bean.setUser(user);

		} else {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			bean.setAccessToken(StringUtils.EMPTY);
			return;
		}
		response.setData(bean);

	}

	/**
	 * API 180
	 * 
	 * @author ThoMC
	 * @param request
	 */

	public void logout(RequestBean request, ResponseBean response) throws Exception {
		MstUserAdmin mstUser = checkTokenInSessionAdmin();

		if (mstUser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		} else {
			mstUser.setAccessToken(null);
			response.setStatus(AppHttpStatus.SUCCESS);
			userAdminDao.save(mstUser);
			return;
		}
	}

	private void sendEmail(String fromAddr, String subject, String contentSend) throws Exception {
		// get app param for email
		String userName = environment.getProperty("mail.user");
		String cc_em_pwd = environment.getProperty("mail.pwd");
		String cc_em_host = environment.getProperty("mail.host");
		String cc_em_port = environment.getProperty("mail.port");
		String cc_em_auth = environment.getProperty("mail.auth");
		String cc_em_ssl = environment.getProperty("mail.ssl ");

		try {
			CommonUtil.sendEMail(userName, cc_em_pwd, fromAddr, userName, cc_em_host, cc_em_port, subject,
					contentSend.toString(), cc_em_auth, cc_em_ssl);
		} catch (Exception e) {
			logger.error("SendEmail Error:", e);
			throw e;
		}
	}

	public void dashboard(ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		DashboardResponseBean bean = new DashboardResponseBean();

		List<String> statusProductList = new LinkedList<String>();
		// statusProductList.add(TblProduct.STATUS_BUYING);
		// statusProductList.add(TblProduct.STATUS_COMPLETED);
		statusProductList.add(TblProduct.STATUS_PUBLISHED);
		bean.setNumberProduct(productRepo.countByStatus(statusProductList));
		bean.setMembership(profileRepo.countByStatus(MstUserAdmin.ACTIVE));

		bean.setWaitingDeposit(productOrderRepo.countByStatus(TblProductOrder.STATUS_1_WAIT_BUYER_PAY));
		bean.setWaitingDelivery(productOrderRepo.countByStatus(TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER));
		bean.setPendingReceipt(productOrderRepo.countByStatus(TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE));

		// Date currentTime = getCurrentTime();

		// Date alertDeposit = DateUtils.getDayBefore(currentTime, Integer
		// .parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DEPOSIT.getName()).getParamValue()));

		// Date alertDelivery = DateUtils.getDayBefore(currentTime, Integer
		// .parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DELIVERY.getName()).getParamValue()));
		//
		// Date alertReceipt = DateUtils.getDayBefore(currentTime, Integer
		// .parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_RECEIVED.getName()).getParamValue()));
		bean.setAlertWaitingDeposit(productOrderRepo.countByAlertStatusAndStatus(
				TblProductOrder.STATUS_1_WAIT_BUYER_PAY, TblProductOrder.STATUS_1_WAIT_BUYER_PAY));
		bean.setAlertWaitingDelivery(productOrderRepo.countByAlertStatusAndStatus(
				TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER, TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER));
		bean.setAlertPendingReceipt(productOrderRepo.countByAlertStatusAndStatus(
				TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE, TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE));

		bean.setTransferRequest(paymentRepo.countByStatus(TblRequestPayment.STATUS_NOT_PAID));
		bean.setNewInquiries(questionRepo.count());

		bean.setNewViolationReported(reportRepo.countReport());

		response.setData(bean);
	}

	public void leaveMember(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		MstUserApp updateUser = userDao.findOne(id);
		if (updateUser == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
		} else {
			// User is buying: leave denied
			if (productOrderRepo.countProductBuying(updateUser.getId())>0) {
				response.setStatus(AppHttpStatus.USER_BUYING);
			} else {
				updateUser.setStatus(MstUserApp.LEAVE);

				updateUser.setLeavedAt(getCurrentTime());
				updateUser.setUpdatedAt(updateUser.getLeavedAt());
				userDao.save(updateUser);
				List<TblProduct> publicList = productRepo.findByStatusAndCreatedBy(TblProduct.STATUS_PUBLISHED,
						updateUser.getId());

				List<TblReportViolation> reportList = reportRepo.findByCreatedUser_Id(updateUser.getId());
				reportList.forEach(report -> {
					report.setStatus(2);
				});
				reportRepo.save(reportList);
				for (TblProduct t : publicList) {
					t.setStatus(TblProduct.STATUS_DRAFT);
					List<TblReportViolation> reportByProductList = reportRepo
							.findByProduct_IdOrderByCreatedAtDesc(t.getId());
					reportByProductList.forEach(report -> {
						report.setStatus(2);
					});
					reportRepo.save(reportByProductList);

				}
				// Update product to draft
				if (!publicList.isEmpty())
					productRepo.save(publicList);
				response.setStatus(AppHttpStatus.SUCCESS);
			}
		}
	}

	public void getMember(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		MstUserApp user = profileRepo.findOne(id);
		if (user == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		}
		MstDataType dt = null;

		if (user.getPrefectureId() != null)
			dt = dataTypeRepo.findOne(user.getPrefectureId());

		if (dt != null)
			user.setPrefectureName(dt.getName());
		// Total revenue
		// BigDecimal totalRevenue =
		// productOrderRepo.totalRevenueByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER,
		// id);
		//
//		BigDecimal totalProfit = productOrderRepo.totalProfitByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER, id);
		Date currentTime = getCurrentTime();
		int date = Integer
				.parseInt(configRepo.findOneByParamName(ConfigParam.EXPIRED_REQUEST_PAYMENT.getName()).getParamValue());
		Date expiredDate = DateUtils.getDayBefore(currentTime, date);
		// Total Request payment
		BigDecimal totalRequestPayment = paymentRepo.sumBuyStatusAndUser(Constants.REQUEST_PAYMENT_STATUS_NOT_PAID, id);
		if (totalRequestPayment == null)
			totalRequestPayment = BigDecimal.ZERO;
		// Total need to request
		BigDecimal totalNeedToRequest = productOrderRepo
				.totalNeedRequestByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER, id, expiredDate);
		if (totalNeedToRequest == null)
			totalNeedToRequest = BigDecimal.ZERO;
		// Total Request accepted
		BigDecimal totalPaidPayment = paymentRepo.sumBuyStatusAndUser(Constants.REQUEST_PAYMENT_STATUS_PAID, id);
		if (totalPaidPayment == null)
			totalPaidPayment = BigDecimal.ZERO;
		// Total exprired
		Date alertDeposit = DateUtils.getDayBefore(currentTime, Integer
				.parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DEPOSIT.getName()).getParamValue()));
		BigDecimal totalExpired = productOrderRepo.totalExpriedByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER,
				id, alertDeposit);
		if (totalExpired == null)
			totalExpired = BigDecimal.ZERO;
//		user.setTotalRevenue(totalProfit == null ? BigDecimal.ZERO : totalProfit);
		user.setTotalExpired(totalExpired == null ? BigDecimal.ZERO : totalExpired);
		user.setTotalNeedToRequest(
				totalNeedToRequest == null ? BigDecimal.ZERO : totalNeedToRequest.subtract(totalRequestPayment));
		user.setTotalPaidPayment(totalPaidPayment == null ? BigDecimal.ZERO : totalPaidPayment);
		user.setTotalRequestPayment(totalRequestPayment == null ? BigDecimal.ZERO : totalRequestPayment);

		response.setData(user);
	}

	public void searchMember(int page, int size, String sortBy, String sortType, Long id, String username, String email,
			BigDecimal revenueFrom, BigDecimal revenueTo, Long productFrom, Long productTo, ResponseBean response)
			throws UnsupportedEncodingException {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);
		if (!"id".equalsIgnoreCase(sortBy) && !"total_revenue".equalsIgnoreCase(sortBy)
				&& !"total_product".equalsIgnoreCase(sortBy)) {
			order = order.ignoreCase();
		}
		username = URLDecoder.decode(username, "UTF-8");
		username = "%" + username.toUpperCase() + "%";
		email = "%" + email + "%";
		Page<MstUserApp> resultPage = profileRepo.findUserByParams(id, username, email, revenueFrom, revenueTo,
				productFrom, productTo, new PageRequest(page, size, new Sort(order)));
		response.setData(resultPage);
	}
}
