package com.dsoft.m2u.controller;


import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsoft.m2u.api.request.DeviceRequest;
import com.dsoft.m2u.api.request.MobileOrderRequest;
import com.dsoft.m2u.api.request.UserRegisterRequest;
import com.dsoft.m2u.api.request.VNPayHistoryRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.common.CommonFunctions;
import com.dsoft.m2u.common.ScreenMessageConstants;
import com.dsoft.m2u.service.CategoryService;
import com.dsoft.m2u.service.FacilityService;
import com.dsoft.m2u.service.FirebaseLogService;
import com.dsoft.m2u.service.MessageService;
import com.dsoft.m2u.service.OrderService;
import com.dsoft.m2u.service.PromotionService;
import com.dsoft.m2u.service.TemplateService;
import com.dsoft.m2u.service.UserService;
import com.dsoft.m2u.service.VNPayService;

import io.swagger.annotations.ApiOperation;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */

@RestController
@RequestMapping(RestURL.ROOT)
public class MobileController extends BaseController {

	private static final Logger logger = LogManager.getLogger(MobileController.class);
	
	@Autowired
	private VNPayService vnPayService;
	
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FacilityService facilityService;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private OrderService orderServece;

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private PromotionService promotionService;
	
	@Autowired
	private FirebaseLogService firebaseLogService;
	
	@ApiOperation(value = "Get all categories", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_CATEGORY, method = RequestMethod.GET)
	public BaseResponse getAllCategories(@RequestParam(required = true) String facilityId,
			@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		logger.info("============= MobileController.getAllCategories START ================");
		return categoryService.getAllCategoryForMobile(facilityId, pageNo, pageSize);
	}

	@ApiOperation(value = "Get all faciliries", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_FACILITY, method = RequestMethod.GET)
	public BaseResponse getAllFacilities(@RequestParam(required = false) Integer pageNo,
			@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) String location) {
		logger.info("============= MobileController.getAllFacilities START ================");
		return facilityService.getAllFacilitiesForMoble(pageNo, pageSize, location);
	}

	@ApiOperation(value = "Get all templates", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_TEMPLATE, method = RequestMethod.GET)
	public BaseResponse getAllTemplates(@RequestParam(required = false) Integer pageNo,
			@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) String categoryId,
			@RequestParam(required = false) String facilityId) {
		logger.info("============= MobileController.getAllTemplates START ================");
		return templateService.getAllTemplateForMoble(pageNo, pageSize, categoryId, facilityId);
	}

	@ApiOperation(value = "Mobile create order", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_ORDER, method = RequestMethod.POST)
	public BaseResponse createOrder(@Valid @RequestBody MobileOrderRequest request) {
		logger.info("============= MobileController.createOrder START ================");
		return orderServece.createOrderForMobile(request);
	}

	@ApiOperation(value = "Mobile get start time and end time from date", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_TIME, method = RequestMethod.GET)
	public BaseResponse getTimeByDate(@Valid @RequestParam String date,@RequestParam String facilityId) {
		logger.info("============= MobileController.getTimeByDate START ================");
		return new BaseResponse(ScreenMessageConstants.SUCCESS, orderServece.getStartAndEndTimeByDate(date,facilityId));
	}

	@ApiOperation(value = "Create history", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_VNPAY_HISTORY, method = RequestMethod.POST)
	public BaseResponse createHistory(@Valid @RequestBody VNPayHistoryRequest request) {
		logger.info("============= MobileController.createHistory START ================");
		return vnPayService.createHistory(request);
	}

	@ApiOperation(value = "Get all messages by category", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_GET_MESSAGE, method = RequestMethod.GET)
	public BaseResponse getMessageByCategoryId(@RequestParam(required = false) List<String> categoryIds) {
		logger.info("============= MobileController.getMessageByCategoryId START ================");
		return messageService.getMessageByCategoryId(categoryIds);
	}
	
	@ApiOperation(value = "Get all orders based on fire base id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_ORDER_BY_FIREBASE_ID, method = RequestMethod.GET)
	public BaseResponse getAllOrderByFireBaseId(@PathVariable("fireBaseId") String fireBaseId,
											@RequestParam(required = false) List<String> statuses,
											@RequestParam(defaultValue = "0", required = false) Integer pageNo,
											@RequestParam(defaultValue = "10", required = false) Integer pageSize) {
		logger.info("============= MobileController.getAllOrderByFireBaseId START ================");
		return orderServece.getAllOrderByFireBaseIdForMoble(fireBaseId, statuses,pageNo, pageSize);
	}

	@ApiOperation(value = "Get orders by order id and fire base id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO
			+ RestURL.MOBILE_ORDER_BY_ORDER_ID_AND_FIREBASE_ID, method = RequestMethod.GET)
	public BaseResponse getAllOrderByOrderIdAndFireBaseId(@PathVariable("fireBaseId") String fireBaseId,
													  @PathVariable("orderId") String orderId) {
		logger.info("============= MobileController.getAllOrderByOrderIdAndFireBaseId START ================");
		return orderServece.getOrderByOrderIdAndFireBaseId(fireBaseId, orderId);
	}

	@ApiOperation(value = "Get all promotion", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_PROMOTIONS, method = RequestMethod.GET)
	public BaseResponse getAll(@RequestParam(defaultValue = "0",required = false) Integer pageNo,
			@RequestParam(defaultValue = "10",required = false) Integer pageSize, @RequestParam(required = false) String text,
			@RequestParam(required = false) boolean all) {
		logger.info("============= PromotionController.getAll START ================");
		return promotionService.getAll(pageNo, pageSize, text, all);
	}
	
	@ApiOperation(value = "Set favourite template for user", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_FAVOURITE_TEMPLATE_ID, method = RequestMethod.POST)
	public BaseResponse setFavouriteTemplate(@PathVariable("fireBaseId") String fireBaseId,
			  								 @PathVariable("templateId") String templateId) {
		logger.info("============= TemplateController.setFavouriteTemplate START ================");
		return templateService.setFavouriteTemplate(fireBaseId, templateId);
	}
	
	@ApiOperation(value = "Get all templates", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_USER_FAVOURITE, method = RequestMethod.GET)
	public BaseResponse getTemplateFavorite(@PathVariable("fireBaseId") String fireBaseId,  @RequestParam(defaultValue = "0", required = false) Integer pageNo,
			@RequestParam(defaultValue = "10",required = false) Integer pageSize, @RequestParam(required = false) String categoryId,
			@RequestParam(required = false) String facilityId) {
		logger.info("============= MobileController.getTemplateFavorite START ================");
		return templateService.getTemplateFavorite(pageNo, pageSize, categoryId, facilityId, fireBaseId);
	}
	
	@ApiOperation(value = "Get user detail by user id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_USER_DETAILS, method = RequestMethod.GET)
	public BaseResponse getInfomationById(@PathVariable("fireBaseId") String fireBaseId) {
		logger.info("========== MobileController.getById START ==========");
		logger.info("request: " + CommonFunctions.convertToJSONString(fireBaseId));
		return userService.getById(fireBaseId);
	}
			
	@ApiOperation(value = "Create new user account with firebase ID", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_USER_REGISTER, method = RequestMethod.POST)
	public BaseResponse useRegister(@Valid @RequestBody UserRegisterRequest user) {
		logger.info("============ MobileController.useRegister START ===========");
		return userService.registerUserAccount(user);
	}
	
	@ApiOperation(value = "Get user detail by user id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_USER_NOTIFICATIONS, method = RequestMethod.GET)
	public BaseResponse getNotificationsById(@PathVariable("fireBaseId") String fireBaseId
			, @RequestParam(defaultValue = "0", required = false) Integer pageNo,
			@RequestParam(defaultValue = "10", required = false) Integer pageSize) {
		logger.info("========== MobileController.getNotificationsById START ==========");
		return firebaseLogService.getNotificationsByUserId(pageNo, pageSize, fireBaseId);
	}
	
	@ApiOperation(value = "Create new device with firebase ID", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_DEVICE, method = RequestMethod.POST)
	public BaseResponse insertDevice(@Valid @RequestBody DeviceRequest deviceRequest) {
		logger.info("============ UserController.useRegister START ===========");
		return userService.insertDevice(deviceRequest);
	}
	
	@ApiOperation(value = "Check message contain bad word", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_MESSAGE, method = RequestMethod.POST)
	public BaseResponse checkBadWord(@RequestParam(required = true) String message) {
		logger.info("============= MobileController.checkBadWord START ================");
		return new BaseResponse(ScreenMessageConstants.SUCCESS, messageService.checkMessageIsValid(message));
	}
	
//	@ApiOperation(value = "Mobile create new message", response = BaseResponse.class)
//	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.MOBILE_MESSAGE, method = RequestMethod.POST)
//	public MessageValidationDTO createMessage(@RequestParam(required = true) String message) {
//		logger.info("============= MobileController.createMessage START ================");
//		return messageService.checkMessageIsValid(message);
//	}
}
