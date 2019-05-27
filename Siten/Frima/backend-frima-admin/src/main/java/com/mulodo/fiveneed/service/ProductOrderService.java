package com.mulodo.fiveneed.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.bean.response.ProductDetailBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstDataType;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.entity.TblTodo;
import com.mulodo.fiveneed.repository.ConfigRepository;
import com.mulodo.fiveneed.repository.DataTypeRepository;
import com.mulodo.fiveneed.repository.ProductOrderRepository;
import com.mulodo.fiveneed.repository.ProductRepository;
import com.mulodo.fiveneed.repository.TodoRepository;

@Service("ProductOrderService")
@Transactional(rollbackFor = Exception.class)
public class ProductOrderService extends BaseService {

	@Autowired
	private ProductService productService;

	@Autowired
	TodoRepository todoRepo;

	@Autowired
	ProductRepository productRepo;

	@Autowired
	ConfigRepository configRepo;

	@Autowired
	DataTypeRepository dataTypeRepo;

	@Autowired
	private ProductOrderRepository productOrderRepo;

	public void search(int page, int size, String sortBy, String sortType, long id, long productId, String productName,
			long userSellId, String userSellName, long userBuyId, String userBuyName, List<Integer> statusList,
			List<Integer> statusAlertList, ResponseBean response) throws UnsupportedEncodingException {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);
		if (!"id".equalsIgnoreCase(sortBy) && !"product.price".equalsIgnoreCase(sortBy)) {
			order=order.ignoreCase();
		}
		userSellName = URLDecoder.decode(userSellName, "UTF-8");
		userBuyName = URLDecoder.decode(userBuyName, "UTF-8");
		productName = URLDecoder.decode(productName, "UTF-8");
		userSellName = "%" + userSellName.toUpperCase().trim() + "%";
		userBuyName = "%" + userBuyName.toUpperCase().trim() + "%";
		productName = "%" + productName.toUpperCase().trim() + "%";
		int statusSize = 0;
		int statusAlertSize = 0;
		if (CommonUtil.isEmpty(statusList)) {
			statusList = new Vector<>();
			statusList.add(-1);
			statusSize = 0;
		} else
			statusSize = statusList.size();

		if (CommonUtil.isEmpty(statusAlertList)) {
			statusAlertList = new Vector<>();
			statusAlertList.add(-1);
			statusAlertSize = 0;
		} else
			statusAlertSize = statusAlertList.size();

		// Date currentTime = getCurrentTime();
		// Date alertDeposit = null;
		// Date alertDelivery = null;
		// Date alertReceipt = null;
		//
		// if
		// (statusAlertList.contains(TblProductOrder.STATUS_1_WAIT_BUYER_PAY)) {
		// alertDeposit = DateUtils.getDayBefore(currentTime, Integer.parseInt(
		// configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DEPOSIT.getName()).getParamValue()));
		// }
		// if
		// (statusAlertList.contains(TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER))
		// {
		// alertDelivery = DateUtils.getDayBefore(currentTime, Integer.parseInt(
		// configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_DELIVERY.getName()).getParamValue()));
		// }
		//
		// if
		// (statusAlertList.contains(TblProductOrder.STATUS_3_WAIT_BUYER_RECEIVE))
		// {
		// alertReceipt = DateUtils.getDayBefore(currentTime, Integer.parseInt(
		// configRepo.findOneByParamName(ConfigParam.ALERT_WAITING_RECEIVED.getName()).getParamValue()));
		//
		// }

		Page<TblProductOrder> resultPage = productOrderRepo.search(id, productId, productName, userSellId, userSellName,
				userBuyId, userBuyName, statusList, statusSize, statusAlertList, statusAlertSize,
				new PageRequest(page, size, new Sort(order)));
		System.out.println(resultPage);
		response.setData(resultPage);

	}

	public void get(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblProductOrder order = productOrderRepo.findOne(id);
		MstDataType dt = dataTypeRepo.findOne(order.getPaymentMethod());
		if (dt != null)
			order.setPaymentMethodName(dt.getName());

		ResponseBean productResponse = new ResponseBean();
		productService.getProductDetail(order.getProductId(), productResponse);
		order.setProduct(((ProductDetailBean) productResponse.getData()).getProduct());
		MstDataType prefecture = dataTypeRepo.findOne(order.getPrefectureId());
		if (prefecture != null)
			order.getProduct().setPrefecture(prefecture.getName());

		response.setData(order);
	}

	public void confirmPaymentBuyer(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblProductOrder order = productOrderRepo.findOne(id);

		if(order.getStatus()!=TblProductOrder.STATUS_1_WAIT_BUYER_PAY){
			response.setStatus(AppHttpStatus.FAILED_TO_UPDATE_DATA);
		}
		
		order.setStatus(TblProductOrder.STATUS_2_WAIT_SELLER_DELIVER);
		order.setAlertStatus(null);
		order.setUpdatedAt(getCurrentTime());
		order.setAdminId(checkuser.getId());
		order.setPaymentBuyAt(order.getUpdatedAt());
		productOrderRepo.save(order);

		TblTodo todo = new TblTodo();
		// todo.setTodoType(TblTodo.TODO_TYPE_1_DELIVER);
		todo.setType(TblTodo.TODO_TYPE_DELIVER);
		todo.setCreatedAt(order.getUpdatedAt());
		// todo.setCreatedBy(user.getId());
		// todo.setFromUserId(user.getId());
		// todo.setFromUserName(user.getUserName());
		todo.setProductOrderId(order.getId());
		todo.setImageUrl(order.getProductImageUrl());
		todo.setIsRead(false);
		todo.setProductId(order.getProductId());
		todo.setProduct(order.getProduct());
		todo.setProductName(order.getProductName());
		todo.setReceiveUserId(order.getUserSellId());
		todo.setReceiveUser(userDao.findOne(order.getUserSellId()));
		todo.setTodoContent(TblTodo.TODO_CONTENT_1_DELIVER);

		todoRepo.save(todo);

		response.setData(order);
	}

	public void unConfirmPaymentBuyer(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		
		
		
		TblProductOrder order = productOrderRepo.findOne(id);
		
		if(order.getStatus()!=TblProductOrder.STATUS_1_WAIT_BUYER_PAY){
			response.setStatus(AppHttpStatus.FAILED_TO_UPDATE_DATA);
		}
		
		order.setStatus(TblProductOrder.STATUS_0_CANCEL);
		order.setAlertStatus(null);
		order.setUpdatedAt(getCurrentTime());
		order.setAdminId(checkuser.getId());
		order.setPaymentBuyAt(null);
		order.setDeliverAt(null);
		order.setReceivedAt(null);
		order.setPaymentSellerAt(null);
		productOrderRepo.save(order);

		// Update trạng thái của sản phẩm thành buying
		TblProduct product = productRepo.findOne(order.getProductId());
		product.setStatus(TblProduct.STATUS_PUBLISHED);
		productRepo.save(product);
		response.setData(order);
	}

}
