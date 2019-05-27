package com.mulodo.fiveneed.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.common.util.CommonUtil;
import com.mulodo.fiveneed.common.util.StringUtils;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.constant.Constants;
import com.mulodo.fiveneed.constant.EnvironmentKey;
import com.mulodo.fiveneed.entity.MstUserAdmin;
import com.mulodo.fiveneed.repository.StaffRepository;

@Service("StaffService")
public class StaffService extends BaseService {

	@Autowired
	private StaffRepository staffRepository;

	public void findOne(long id, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		MstUserAdmin staff = staffRepository.findOne(id);
		if (staff == null) {
			response.setStatus(AppHttpStatus.FAILED_TO_GET_DATA);
			return;
		} else {
			response.setData(staff);
		}
	}

	public void create(MstUserAdmin staff, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		MstUserAdmin user = staffRepository.findByEmail(staff.getEmail());
		if (user == null && checkuser.getRole().equals("Admin")) {
			staff.setSalt(CommonUtil.randomDecimalString(Constants.SALT_LENGTH));
			staff.setPassword(CommonUtil.encryptPassword(staff.getPassword(), staff.getSalt(),
					Integer.parseInt(environment.getProperty(EnvironmentKey.SHA256_LOOPNUMBER_KEY.getKey()))));
			if (staff.getIsDeleted()) {
				staff.setStatus(MstUserAdmin.DELETE);
			} else {
				staff.setStatus(MstUserAdmin.ACTIVE);
			}
			staff.setCreatedAt(getCurrentTime());
			staff.setCreatedBy(checkuser.getId());
			staffRepository.save(staff);
		} else {
			response.setStatus(AppHttpStatus.FAILED_TO_SAVE_DATA);
			return;
		}

	}

	public void findAllStaff(long id, String username, int page, int size, String sortBy, String sortType,
			ResponseBean response) throws UnsupportedEncodingException {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		String sortByProperty = StringUtils.snakeCaseToCamelCase(sortBy);
		Sort.Order order = new Sort.Order(
				Constants.ORDER_ASC.equalsIgnoreCase(sortType) ? Direction.ASC : Direction.DESC, sortByProperty);

		username = URLDecoder.decode(username, "UTF-8");
		username = "%" + username.toUpperCase() + "%";

		Page<MstUserAdmin> resultPage = staffRepository.searchUser(id, username,
				new PageRequest(page, size, new Sort(order)));
		response.setData(resultPage);
	}

	public void update(MstUserAdmin staff, ResponseBean response) {
		MstUserAdmin checkuser = checkTokenInSessionAdmin();
		if (checkuser == null) {
			response.setStatus(AppHttpStatus.AUTH_FAILED);
			return;
		}
		MstUserAdmin user = staffRepository.findOne(staff.getId());
		MstUserAdmin userDuplicate = staffRepository.findByEmail(staff.getEmail());

		if (userDuplicate != null && user.getId().intValue() != userDuplicate.getId().intValue()) {
			response.setStatus(AppHttpStatus.ALREADY_EXISTS_USER_ID);
			return;
		}

		// Không disable tài khoản của mình
		if (user != null && user.getId().equals(checkuser.getId()) && staff.getStatus().equals(MstUserAdmin.DELETE)) {
			response.setStatus(AppHttpStatus.FAILED_TO_UPDATE_DATA);
			return;
		}

		if (user != null && checkuser.getRole().equals("Admin")) {
			if (!staff.getPassword().equals(user.getPassword())) {
				user.setPassword(CommonUtil.encryptPassword(staff.getPassword(), user.getSalt(),
						Integer.parseInt(environment.getProperty(EnvironmentKey.SHA256_LOOPNUMBER_KEY.getKey()))));
			}
			if (staff.getIsDeleted()) {
				user.setStatus(MstUserAdmin.DELETE);
			} else {
				user.setStatus(MstUserAdmin.ACTIVE);
			}
			user.setUserName(staff.getUserName());
			user.setEmail(staff.getEmail());
			user.setRole(staff.getRole());
			user.setAccessToken("");

			user.setUpdatedAt(getCurrentTime());
			user.setUpdatedBy(checkuser.getId());

			staffRepository.save(user);

		} else {
			response.setStatus(AppHttpStatus.FAILED_TO_UPDATE_DATA);
		}
	}

}
