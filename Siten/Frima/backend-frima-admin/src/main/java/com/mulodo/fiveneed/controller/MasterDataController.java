package com.mulodo.fiveneed.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.entity.MstCategory;
import com.mulodo.fiveneed.entity.MstDataType;
import com.mulodo.fiveneed.entity.MstNgword;
import com.mulodo.fiveneed.service.MasterDataService;

@RestController
@RequestMapping("/admin")
@PropertySource(value = { "classpath:application.properties" })
public class MasterDataController extends BaseController {
	@Autowired
	MasterDataService dataService;

	@RequestMapping(value = "/category", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> getCategoryByParentId(
			@RequestParam(value = "parent_id", required = false) Integer parentId) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.getCategoryByParentId(parentId, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	
	
	@RequestMapping(value = "/treecategory", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> getTreeCategory() {
		ResponseBean response = new ResponseBean();
		try {
			dataService.getTreeCategory( response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> createTreeCategory(@RequestBody MstCategory  Category) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.createTreeCategory(Category, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> updateTreeCategory(@RequestBody MstCategory  Category) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.updateTreeCategory(Category, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	@RequestMapping(value = "/treecategory/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> TreeCategory(@PathVariable("id") Integer id) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.treeCategoryDetail(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseBean> deleteTreeCategory(@PathVariable("id") Integer id) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.deleteTreeCategory(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory/moveUp", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> moveUpTreeCategory(@RequestBody MstCategory  Category) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.moveUpTreeCategory(Category, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory/moveDown", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> moveDownTreeCategory(@RequestBody MstCategory  Category) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.moveDownTreeCategory(Category, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/treecategory/move", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> moveTreeCategory(@RequestParam(value = "CateId", required = true, defaultValue = "0") int CateId,
			@RequestParam(value = "newParentCatId", required = true, defaultValue = "0") int newParentCatId,
			@RequestParam(value = "newPositionInNewParent", required = true, defaultValue = "0") int newPositionInNewParent,
			@RequestParam(value = "child", required = true, defaultValue = "0") List<Integer>  child) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.moveTree(CateId,newParentCatId,newPositionInNewParent,child,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/ngword/count", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> countNgword() {
		ResponseBean response = new ResponseBean();
		try {
			dataService.countWord(response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/ngword", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> findNgword(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.findNgword(page, size, sortBy, sortType, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/ngword/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseBean> deleteNgword(@PathVariable("id") Long id) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.deleteNgword(id, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/ngword", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> createNgword(@RequestBody MstNgword ngword) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.createNgword(ngword, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/ngword/delete", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> deleteNgword(@RequestBody List<Long> ngwordIdList) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.deleteNgword(ngwordIdList, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/bank", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> listBank(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.listBank(page,size,sortBy,sortType,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/bank", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> addBank(@RequestBody MstDataType typeData ) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.addBank(typeData,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	@RequestMapping(value = "/bank/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> detailBank(@PathVariable(value="id") int id) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.detailBank(id,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/bank", method = RequestMethod.PUT)
	public ResponseEntity<ResponseBean> putBank(@RequestBody MstDataType typeData ) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.putBank(typeData,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/deletebank", method = RequestMethod.POST)
	public ResponseEntity<ResponseBean> deleteBank(@RequestBody List<MstDataType> listMstDataType ) {
		
		ArrayList<String> listId = new ArrayList<>();

		for (MstDataType dataType : listMstDataType) {
			listId.add(dataType.getId()+ "");
		}

		ResponseBean response = new ResponseBean();
		try {
			dataService.deleteBank(listId,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
	
	@RequestMapping(value = "/question", method = RequestMethod.GET)
	public ResponseEntity<ResponseBean> listQuestion(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "100") int size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
			@RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType) {
		ResponseBean response = new ResponseBean();
		try {
			dataService.listQuestion(page,size,sortBy,sortType,response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}
}




