package com.mulodo.fiveneed.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.constant.ConfigParam;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.entity.TblTodo;
import com.mulodo.fiveneed.repository.ConfigRepository;
import com.mulodo.fiveneed.repository.ProductOrderRepository;
import com.mulodo.fiveneed.repository.ProductRepository;
import com.mulodo.fiveneed.repository.TodoRepository;
import com.mulodo.fiveneed.repository.UserRepository;

@Service("ScheduleService")
@Transactional(rollbackFor = Exception.class)
public class ScheduleService extends BaseService {
	@Autowired
	protected UserRepository userRepo;

	@Autowired
	protected ProductRepository productRepo;

	@Autowired
	protected ProductOrderRepository productOrderRepo;

	@Autowired
	TodoRepository todoRepo;

	@Autowired
	ConfigRepository configRepo;


	@Scheduled(fixedRate = 86400000)
	public void runUpdateAlertOrderDeposit() {
		Date currentTime = getCurrentTime();
		logger.info("runUpdateAlertOrderDeposit :: " + Calendar.getInstance().getTime());
		Date alertDeposit = DateUtils.getDayBefore(currentTime, Integer
				.parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DEPOSIT.getName()).getParamValue()));
		List<TblProductOrder> expiredDepositOrder = productOrderRepo.findExpiredDepositOrder(alertDeposit);
		for (TblProductOrder o : expiredDepositOrder) {
			if (!o.getStatus().equals(TblProductOrder.STATUS_0_CANCEL)) {
				o.setAlertStatus(TblProductOrder.STATUS_1_WAIT_BUYER_PAY);
				o.setUpdatedAt(currentTime);
				productOrderRepo.save(o);

				TblTodo todo = new TblTodo();
				todo.setType(TblTodo.TODO_TYPE_ALERT_DEPOSIT);
				// todo.setTodoType(TblTodo.TODO_TYPE_6_ALERT_DEPOSIT);
				todo.setCreatedAt(o.getUpdatedAt());
				todo.setCreatedBy(null);
				todo.setFromUserId(null);
				todo.setFromUserName("");
				todo.setProductOrderId(o.getId());
				todo.setProduct(o.getProduct());
				todo.setImageUrl(o.getProductImageUrl());
				todo.setIsRead(false);
				todo.setProductId(o.getProductId());
				todo.setProductName(o.getProductName());
				todo.setProduct(o.getProduct());
				todo.setReceiveUserId(o.getUserSellId());
				todo.setReceiveUser(userDao.findOne(o.getUserSellId()));
				todo.setTodoContent(TblTodo.TODO_CONTENT_6_ALERT_DEPOSIT);

				todoRepo.save(todo);
			}

		}
	}

	@Scheduled(fixedRate = 86400000)
	public void runUpdateAlertDeliver() {
		Date currentTime = getCurrentTime();
		logger.info("runUpdateAlertDeliver :: " + Calendar.getInstance().getTime());
		Date alertDelivery = DateUtils.getDayBefore(currentTime, Integer
				.parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DELIVERY.getName()).getParamValue()));

		List<TblProductOrder> expiredDepositOrder = productOrderRepo.findExpiredDelivery(alertDelivery);
		for (TblProductOrder o : expiredDepositOrder) {
			if (!o.getStatus().equals(TblProductOrder.STATUS_0_CANCEL)) {
				o.setAlertStatus(TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER);
				o.setUpdatedAt(currentTime);
				productOrderRepo.save(o);
			}
		}
	}

	@Scheduled(fixedRate = 86400000)
	public void runUpdateAlertReceive() {
		Date currentTime = getCurrentTime();
		logger.info("runUpdateAlertReceive :: " + Calendar.getInstance().getTime());

		Date alertReceipt = DateUtils.getDayBefore(currentTime, Integer
				.parseInt(configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_RECEIVED.getName()).getParamValue()));

		List<TblProductOrder> expiredDepositOrder = productOrderRepo.findExpiredReceive(alertReceipt);
		for (TblProductOrder o : expiredDepositOrder) {
			if (!o.getStatus().equals(TblProductOrder.STATUS_0_CANCEL)) {
				o.setAlertStatus(TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE);
				o.setUpdatedAt(currentTime);
				productOrderRepo.save(o);
			}
		}
	}

	@Scheduled(fixedRate = 86400000)
	public void runUpdateLeavedMember() {
		Date currentTime = getCurrentTime();
		Date leavedDate = DateUtils.getDayBefore(currentTime, 180);

		logger.info("runUpdateLeavedMember :: " + Calendar.getInstance().getTime());
		List<MstUserApp> userUpdate = userRepo.findAllUserLeaved(leavedDate);
		if (!CollectionUtils.isEmpty(userUpdate)) {
			for (MstUserApp user : userUpdate) {
				user.setStatus(MstUserApp.DELETE);
			}
			userRepo.save(userUpdate);
		}

	}

	//
//	@Scheduled(initialDelay = 1000, fixedRate = 300000)
//	public void runUpdateProduct() {
//		Date currentTime = getCurrentTime();
//		Date fromDate = DateUtils.getDayBefore(currentTime, 100);
//		logger.info("runUpdateProduct :: " + Calendar.getInstance().getTime());
//		List<Long> userUpdate = productRepo.findAllUserIdUploadInThisDay(fromDate, currentTime);
//		for (Long userId : userUpdate) {
//			MstUserApp user = userRepo.findOne(userId);
//
//			Long totalProduct = productRepo.countByCreatedByAndProductType(userId, 0);
//			if (totalProduct != null)
//				user.setTotalProduct(totalProduct);
//			userRepo.save(user);
//		}
//	}
//
//	@Scheduled(initialDelay = 15000, fixedRate = 300000)
//	public void runUpdateRevenue() {
//		Date currentTime = getCurrentTime();
//		Date fromDate = DateUtils.getDayBefore(currentTime, 100);
//		logger.info("runUpdateRevenue :: " + Calendar.getInstance().getTime());
//		List<Long> userUpdate = productOrderRepo.findAllUserIdRevenueInThisDay(fromDate, currentTime);
//		for (Long userId : userUpdate) {
//			MstUserApp user = userRepo.findOne(userId);
//
//			BigDecimal totalRevenue = productOrderRepo
//					.totalRevenueByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER, userId);
//
//			BigDecimal totalProfit = productOrderRepo.totalProfitByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER,
//					userId);
//
//			if (totalRevenue != null)
//				user.setTotalRevenue(totalRevenue);
//			if (totalProfit != null)
//				user.setTotalProfit(totalProfit);
//			userRepo.save(user);
//		}
//	}
	
}
