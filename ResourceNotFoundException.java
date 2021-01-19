package com.dsoft.m2u.exception;

import com.dsoft.m2u.api.dto.ErrorCodeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    private static final long serialVersionUID = 7271159904629535750L;
    
	private ErrorCodeEnum errorCode;
	private Integer errorCodeNum;
	
	public ResourceNotFoundException(String message) {
		super(message);
		this.errorCode = ErrorCodeEnum.RESOURCE_NOT_FOUND;
	}
	
	public ResourceNotFoundException(String message, ErrorCodeEnum errorCode) {
		this(message);
		this.errorCode = errorCode;
	}

	public ErrorCodeEnum getErrorCode() {
		return errorCode;
	}
	
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(ErrorCodeEnum errorCode, String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
        this.errorCodeNum = errorCode.getNumCode();
    }

}
