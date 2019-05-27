package com.mulodo.fiveneed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.mulodo.fiveneed.bean.RequestBean;
import com.mulodo.fiveneed.bean.ResponseBean;
import com.mulodo.fiveneed.bean.request.LoginRequestBean;
import com.mulodo.fiveneed.constant.AppHttpStatus;
import com.mulodo.fiveneed.service.UserService;

@RestController
@RequestMapping("/admin/user")
@PropertySource(value = { "classpath:application.properties" })
public class UserController extends BaseController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<?> test() {
		ResponseBean response = new ResponseBean();
		try {
			response.setStatus(AppHttpStatus.SUCCESS);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	/**
	 * @author tan
	 * @param request
	 * @return
	 */

	/**
	 * API 220
	 *
	 * @param request
	 * @return
	 */
	// @RequestMapping(value = "/RegistSystem", method = RequestMethod.POST)
	// public ResponseEntity<?> registSystem(
	// @RequestBody RegistSystemRequestBean request) {
	// ResponseBean response = new ResponseBean();
	// try {
	// userService.registSystem(request, response);
	// return response(response);
	// } catch (Exception e) {
	// response.setStatus(AppHttpStatus.INTERNAL_SERVER_ERROR);
	// return response(response);
	// }
	// }

	/**
	 * API A001
	 *
	 * @author ThoMC
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody LoginRequestBean request) {
		ResponseBean response = new ResponseBean();
		try {
			userService.login(request, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public ResponseEntity<?> dashboard() {
		ResponseBean response = new ResponseBean();
		try {
			userService.dashboard(response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

	/**
	 * API 020
	 *
	 * @author Danhloc
	 * @param request
	 * @return
	 */
	// @RequestMapping(value = "/ResetPassword", method = RequestMethod.POST)
	// public ResponseEntity<?> resetPassword(
	// @RequestBody ResetPasswordRequestBean request) {
	// ResponseBean response = new ResponseBean();
	// try {
	// userService.resetPassword(request, response);
	// return response(response);
	// } catch (Exception e) {
	// logger.error("ResetPassword", e);
	// response.setStatus(AppHttpStatus.INTERNAL_SERVER_ERROR);
	// return response(response);
	// }
	// }

	/**
	 * API 030
	 *
	 * @author Danloc
	 * @param request
	 * @return
	 */
	// @RequestMapping(value = "/ChangePassword", method = RequestMethod.POST)
	// public ResponseEntity<?> changePassword(
	// @RequestBody ChangePasswordRequestBean request) {
	// ChangePasswordResponseBean response = new ChangePasswordResponseBean();
	//
	// try {
	// userService.changePassword(request, response);
	// return response(response);
	// } catch (Exception e) {
	// logger.error("ChangePassword", e);
	// response.setStatus(AppHttpStatus.INTERNAL_SERVER_ERROR);
	// return response(response);
	// }
	//
	// }

	/**
	 * Send mail set password
	 *
	 * @author thomc
	 * @param request
	 * @return
	 */
	// @RequestMapping(value = "/SendMail", method = RequestMethod.POST)
	// public ResponseEntity<?> sendMail(@RequestBody RequestBean request) {
	// ResponseBean response = new ResponseBean();
	// try {
	// userService.sendMail(request, response);
	// return response(response);
	// } catch (Exception e) {
	// logger.error("sendMail", e);
	// response.setStatus(AppHttpStatus.INTERNAL_SERVER_ERROR);
	// return response(response);
	// }
	// }

	/**
	 * API 180 LogOut
	 *
	 * @author THOMC
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<?> logOut(@RequestBody RequestBean request) {
		ResponseBean response = new ResponseBean();
		try {
			userService.logout(request, response);
			return response(response);
		} catch (Exception e) {
			return responseError(response, e);
		}
	}

}
