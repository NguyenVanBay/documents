package com.mulodo.fiveneed.service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.bean.response.ProductDetailBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.DateUtils;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstCategory;
import com.mulodo.fiveneed.entity.MstDataType;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblChat;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.entity.TblProductImage;
import com.mulodo.fiveneed.entity.TblProductOrder;
import com.mulodo.fiveneed.entity.TblProductWish;
import com.mulodo.fiveneed.entity.TblReportViolation;
import com.mulodo.fiveneed.entity.TblTodo;
import com.mulodo.fiveneed.repository.CategoryRepository;
import com.mulodo.fiveneed.repository.ChatRepository;
import com.mulodo.fiveneed.repository.DataTypeRepository;
import com.mulodo.fiveneed.repository.ProductImageRepository;
import com.mulodo.fiveneed.repository.ProductOrderRepository;
import com.mulodo.fiveneed.repository.ProductRepository;
import com.mulodo.fiveneed.repository.ProductWishRepository;
import com.mulodo.fiveneed.repository.ReportViolationRepository;
import com.mulodo.fiveneed.repository.TodoRepository;

@Service("ProductService")
@Transactional(rollbackFor = Exception.class)
public class ProductService extends BaseService {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private ProductImageRepository productImageRepo;

	@Autowired
	private ChatRepository chatRepo;

	@Autowired
	private ReportViolationRepository reportRepo;

	@Autowired
	private DataTypeRepository dataRepo;

	@Autowired
	ProductOrderRepository productOrderRepo;

	@Autowired
	ChatRepository chatRepository;

	@Autowired
	ProductWishRepository productWishRepo;

	@Autowired
	TodoRepository todoRepo;

	@Autowired
	private CategoryRepository cateRepo;

	public void searchProduct(int page, int size, String sortBy, String sortType, long id, String name,
			String createdAtFrom, String createdAtTo, long createdBy, List<String> statusList,
			List<Integer> categoryList, ResponseBean response) throws Exception {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);

		if (!"id".equalsIgnoreCase(sortBy) && !"price".equalsIgnoreCase(sortBy)) {
			order = order.ignoreCase();
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
		int categorySize = 0;
		if (statusList.size() == 1 && statusList.get(0).equals("")) {
			statusSize = 0;
		} else
			statusSize = statusList.size();

		List<Integer> childCategoryList = new Vector<>();

		if (!CommonUtil.isEmpty(categoryList)) {
			for (Integer catId : categoryList) {
				List<Integer> childList = cateRepo.findCategoryChildIdByParent("-" + catId + "-%");
				if (!CommonUtil.isEmpty(childList))
					childCategoryList.addAll(childList);
			}
			childCategoryList.addAll(categoryList);
		}

		if (CommonUtil.isEmpty(childCategoryList)) {
			childCategoryList = new Vector<>();
			childCategoryList.add(0);
			categorySize = 0;
		} else {
			categorySize = childCategoryList.size();
		}

		name = URLDecoder.decode(name, "UTF-8");

		name = "%" + name.toUpperCase(Locale.JAPAN) + "%";
		Page<TblProduct> resultPage = productRepo.searchProduct(id, name, createdAtFromDate, createdAtToDate, createdBy,
				statusList, statusSize, childCategoryList, categorySize, new PageRequest(page, size, new Sort(order)));
		List<Integer> cateIdList = new Vector<>();
		resultPage.forEach(product -> {
			if (product.getCategoryId() != null)
				cateIdList.add(product.getCategoryId());
		});

		List<MstCategory> catList = cateRepo.findByIdIn(cateIdList);
		catList.add(new MstCategory());

		resultPage.forEach(product -> {
			if (product.getCategoryId() != null)
				product.setCategory(catList.stream().filter(p -> product.getCategoryId().equals(p.getId())).findFirst()
						.orElse(null));
			else {
				product.setCategory(new MstCategory());
			}
		});

		response.setData(resultPage);
	}

	public void getProductDetail(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		ProductDetailBean bean = new ProductDetailBean();

		TblProduct p = productRepo.findOne(id);
		if (p == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		}

		p.setStrTags("");
		if (p.getArrtags() != null && p.getArrtags().length > 0) {
			for (String tagId : p.getArrtags()) {
				p.setStrTags(p.getStrTags() + dataRepo.findOne(Integer.parseInt(tagId)).getName() + ",");
			}
			p.setStrTags(p.getStrTags().substring(0, p.getStrTags().length() - 1));
		}

		bean.setProduct(p);
		List<TblProductImage> imageList = productImageRepo.findByProductIdOrderByImageOrderAsc(id);
		bean.setProductImageList(imageList);

		MstDataType prefecture = null;
		if (p.getPrefectureId() != null)
			prefecture = dataRepo.findOne(p.getPrefectureId());
		if (prefecture != null)
			p.setPrefecture(prefecture.getName());

		// Join with datatype
		List<Integer> dataTypeIdList = new LinkedList<>();
		dataTypeIdList.add(p.getDeliverDayId());
		dataTypeIdList.add(p.getPrefectureId());
		dataTypeIdList.add(p.getProductStatusId());
		dataTypeIdList.add(p.getShipFeeTypeId());
		dataTypeIdList.add(p.getShipMethodId());
		dataTypeIdList.add(p.getStoreTypeId());
		dataTypeIdList.add(p.getVolumnId());
		dataTypeIdList.removeIf((Integer val) -> val == null);

		List<MstDataType> dataList = dataRepo.findByIdInOrderByOrderNoAsc(dataTypeIdList);

		dataList.forEach(data -> {
			if (Objects.equals(data.getId(), p.getDeliverDayId())) {
				p.setDeliverDay(data.getName());
			}
			if (Objects.equals(data.getId(), p.getProductStatusId())) {
				p.setProductStatus(data.getName());
			}
			if (Objects.equals(data.getId(), p.getShipFeeTypeId())) {
				p.setShipFeeType(data.getName());
			}

			if (Objects.equals(data.getId(), p.getShipMethodId())) {
				p.setShipMethod(data.getName());
			}
			if (Objects.equals(data.getId(), p.getStoreTypeId())) {
				p.setStoreType(data.getName());
			}
			if (Objects.equals(data.getId(), p.getVolumnId())) {
				p.setVolumn(data.getName());
			}

		});

		// Comment list
		List<TblChat> listChat = chatRepo.findByProduct_IdOrderByCreatedAtDesc(p.getId());

		bean.setChatList(listChat);

		// Report Violation List
		List<TblReportViolation> listReport = reportRepo.findByProduct_IdOrderByCreatedAtDesc(p.getId());

		bean.setReportList(listReport);

		response.setData(bean);
	}

	// danhloc
	// Cancel Product
	public void updateStatusProduct(long id, TblProduct product, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		TblProduct p = productRepo.findOne(id);
		if (p == null || !p.getStatus().equalsIgnoreCase(TblProduct.STATUS_PUBLISHED)) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		} else {
			p.setStatus(TblProduct.STATUS_VIOLATION);
			p.setViolationTitle(product.getViolationTitle());
			p.setViolationDetail(product.getViolationDetail());
			productRepo.save(p);

			List<TblChat> chatList = chatRepository.findByProduct_IdOrderByCreatedAtDesc(p.getId());
			List<TblProductWish> wishList = productWishRepo.findByProduct_Id(p.getId());
			List<TblTodo> todoList = todoRepo.findByProduct_Id(p.getId());

			List<TblTodo> todoDeleteList = todoList.stream()
					.filter(todo -> todo.getReceiveUserId().compareTo(p.getCreatedBy()) != 0)
					.collect(Collectors.toList());
			chatRepository.delete(chatList);
			productWishRepo.delete(wishList);
			todoRepo.delete(todoDeleteList);

			MstUserApp user = userDao.findOne(p.getCreatedBy());

			Long totalProduct = productRepo.countByCreatedByAndProductType(user.getId(), 0);
			if (totalProduct != null)
				user.setTotalProduct(totalProduct);
			BigDecimal totalRevenue = productOrderRepo
					.totalRevenueByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER, user.getId());

			BigDecimal totalProfit = productOrderRepo.totalProfitByUser(TblProductOrder.STATUS_4_WAIT_ADMIN_PAY_SELLER,
					user.getId());
			if (totalRevenue != null)
				user.setTotalRevenue(totalRevenue);
			if (totalProfit != null)
				user.setTotalProfit(totalProfit);
			userDao.save(user);

			if (TblProduct.STATUS_CANCEL.equalsIgnoreCase(p.getStatus())
					|| TblProduct.STATUS_DRAFT.equalsIgnoreCase(p.getStatus())
					|| TblProduct.STATUS_VIOLATION.equalsIgnoreCase(p.getStatus())) {
				List<TblReportViolation> reportList = reportRepo.findByProduct_IdOrderByCreatedAtDesc(p.getId());
				reportList.forEach(report -> {
					report.setStatus(2);
				});
				reportRepo.save(reportList);
				// あなたの販売商品は販売規則に違反され、商品違反停止になりました。
				// 違反停止商品は下書きボックスに移動しました。ご確認ください。。
				TblTodo notication = new TblTodo();
				notication.setType(TblTodo.NOTI_TYPE_ADMIN_CANCEL_PRODUCT);

				notication.setTodoContent(TblTodo.NOTI_CONTENT_TYPE_2_ADMIN_CANCEL_PRODUCT);
				// notication.setTodoType(TblTodo.NOTI_TYPE_2_ADMIN_CANCEL_PRODUCT);
				notication.setImageUrl(p.getProductImageUrl());
				notication.setProductName(p.getName());
				notication.setProductId(id);
				notication.setProduct(p);
				notication.setReceiveUserId(p.getCreatedBy());
				notication.setReceiveUser(userDao.findOne(p.getCreatedBy()));
				notication.setIsRead(false);
				notication.setCreatedAt(getCurrentTime());
				todoRepo.save(notication);
			}
		}
	}
}
