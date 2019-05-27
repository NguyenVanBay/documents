package com.mulodo.fiveneed.controller;

import java.util.LinkedList;
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
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.entity.MstUserApp;
import com.mulodo.fiveneed.entity.TblProduct;
import com.mulodo.fiveneed.service.ProductService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class ProductController extends BaseController {
	@Autowired
	ProductService productService;

	@RequestMapping(value = "/product", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> searchProducts(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
			@RequestParam(value = "id", required = false, defaultValue = "0") long id,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "created_at_from", required = false, defaultValue = "0") String createdAtFrom,
			@RequestParam(value = "created_at_to", required = false, defaultValue = "0") String createdAtTo,
			@RequestParam(value = "created_by", required = false, defaultValue = "0") int createdBy,
			@RequestParam(value = "status_list", required = false, defaultValue = "") String statusListStr,
			@RequestParam(value = "category_list", required = false, defaultValue = "") String categoryListStr) {
		ResponseBean response = new ResponseBean();
		try {
			List<String> statusList = CommonUtil.arrayToVector(statusListStr.split(Constants.SEPERATE_CHARACTER));
			List<String> categoryStrList = CommonUtil
					.arrayToVector(categoryListStr.split(Constants.SEPERATE_CHARACTER));
			List<Integer> categoryList = new LinkedList<>();
			for (String str : categoryStrList) {
				try {
					if (!CommonUtil.isEmpty(str))
						categoryList.add(Integer.parseInt(str));
				} catch (Exception e) {
					logger.error("idCategory:", e);
				}

			}
			productService.searchProduct(page, size, sortBy, sortType, id, name, createdAtFrom, createdAtTo, createdBy,
					statusList, categoryList, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/product/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> getProductDetail(@PathVariable("id") int id) {
		ResponseBean response = new ResponseBean();
		try {
			productService.getProductDetail(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	/**
	 * API D2
	 * 
	 * @author danhloc
	 * 
	 */

	@RequestMapping(value = "/product/cancel_product/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBean> updateStatusProduct(@PathVariable("id") int id,
			@RequestBody TblProduct product) {
		ResponseBean response = new ResponseBean();
		try {
			productService.updateStatusProduct(id, product, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}

	}
}
