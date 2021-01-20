package com.dsoft.m2u.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.dsoft.m2u.common.CommonFunctions;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    //private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);
	private static final Logger logger = LogManager.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // This is invoked when user tries to access a secured REST resource without
        // supplying any credentials
        // We should just send a 403 Forbidden response because there is no 'login
        // page' to redirect to
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        // NO LOGIN

    	logger.warn("Unauthorized error. RequestURI - {}", httpServletRequest.getRequestURI());
        logger.warn("Unauthorized error. Message - {}", e.getMessage());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(403);
        ResponseEntity<?> loginError = ResponseEntity.status(403).body("Can not access this resource!");
        httpServletResponse.getWriter().write(CommonFunctions.convertToJSONString(loginError));
    }
}
