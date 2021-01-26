package com.dsoft.m2u.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dsoft.m2u.api.request.TemplateRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.common.CommonFunctions;
import com.dsoft.m2u.service.TemplateService;

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
public class TemplateController extends BaseController {

	private static final Logger logger = LogManager.getLogger(TemplateController.class);

	@Autowired
	private TemplateService templateService;

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Get all template method", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE, method = RequestMethod.GET)
	public BaseResponse getAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) String search,
		   	@RequestParam(required = false) List<String> categoryIds, @RequestParam(required = false) String dateFrom,
							   @RequestParam(required = false) String dateTo) {
		logger.info("============= TemplateController.getAll START ================");
		return templateService.getAll(pageNo, pageSize, search, categoryIds, dateFrom, dateTo);
	}
	
	@ApiImplicitParams({
	@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Get all template without paging method", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE_NO_PAGING, method = RequestMethod.GET)
	public BaseResponse getAllWithoutPaging() {
		logger.info("============= TemplateController.getAllWithoutPaging START ================");
		return templateService.getAll();
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Insert a template method", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE, method = RequestMethod.POST)
	public BaseResponse insert(@Valid @RequestPart("templateModel") TemplateRequest templateModel, @RequestPart("file") MultipartFile file) {
		logger.info("============= TemplateController.insert START ================");
		return templateService.insert(templateModel, file);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Get template by id", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE_ID, method = RequestMethod.GET)
	public BaseResponse getById(@PathVariable String id) {
		logger.info("============= TemplateController.getById START ================");
		logger.info("request: " + CommonFunctions.convertToJSONString(id));
		return templateService.getById(id);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Update a template", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE_ID, method = RequestMethod.PUT)
	public BaseResponse update(@Valid @PathVariable String id, @RequestPart("templateModel") TemplateRequest templateUpdateModel,
			@RequestPart(required = false, name = "file") MultipartFile file) {
		logger.info("============= TemplateController.update START ================");
		return templateService.update(templateUpdateModel, file, id);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Delete a template", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE_ID, method = RequestMethod.DELETE)
	public BaseResponse delete(@Valid @PathVariable String id) {
		logger.info("============= TemplateController.delete START ================");
		return templateService.delete(id);
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@ApiOperation(value = "Update a template with thumbnail", response = BaseResponse.class)
	@RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO + RestURL.ADMIN_TEMPLATE_THUMBNAIL, method = RequestMethod.PUT)
	public BaseResponse addThumbnail(@Valid @PathVariable String id, @RequestParam MultipartFile file) {
	logger.info("============= TemplateController.update START ================");
	
	return templateService.addThumbnail(id, file);
}
}