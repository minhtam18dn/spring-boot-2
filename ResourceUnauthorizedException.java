package com.dsoft.m2u.exception;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
public class ResourceUnauthorizedException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceUnauthorizedException(String message) {
        super(message);
    }
	
}