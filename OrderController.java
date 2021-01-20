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

import com.dsoft.m2u.api.dto.OrderUpdateDTO;
import com.dsoft.m2u.api.request.OrderRequest;
import com.dsoft.m2u.api.request.OrderUpdateRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.common.CommonFunctions;
import com.dsoft.m2u.service.OrderService;
import com.dsoft.m2u.utils.MapperUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
public class OrderController extends BaseController {

	private static final Logger logger = LogManager.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Get all orders", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_ORDER, method = RequestMethod.GET)
	public BaseResponse getAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) String searchText,
			@RequestParam(required = false) String date, @RequestParam(required = false) List<String> facilityIds,
			@RequestParam(required = false) List<String> templateIds,
			@RequestParam(required = false) List<String> categoryIds, @RequestParam(required = false) String deviceId,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> types,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {
		logger.info("============= OrderController.getAll ================");
		return orderService.getAll(pageNo, pageSize, searchText, facilityIds, templateIds, categoryIds, date, deviceId,
				status, types, fromDate, toDate);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Insert a orders", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_ORDER, method = RequestMethod.POST)
	public BaseResponse insert(@Valid @RequestBody OrderRequest request) {
		logger.info("============= OrderController.insert START ================");

		return orderService.insert(request);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Get order by id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_ORDER_ID, method = RequestMethod.GET)
	public BaseResponse getById(@PathVariable String id) {
		logger.info("============= OrderController.getById START ================");
		logger.info("request: " + CommonFunctions.convertToJSONString(id));
		return orderService.getById(id);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Update a order", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_ORDER_ID, method = RequestMethod.PUT)
	public BaseResponse update(@Valid @PathVariable String id, @Valid @RequestBody OrderUpdateRequest request) {
		logger.info("============= OrderController.update START ================");
		OrderUpdateDTO dto = MapperUtil.mapper(request, OrderUpdateDTO.class);
		return orderService.update(dto, id);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Delete a order", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_ORDER_ID, method = RequestMethod.DELETE)
	public BaseResponse delete(@Valid @PathVariable String id) {
		logger.info("============= OrderController.delete START ================");
		return orderService.delete(id);
	}	
}