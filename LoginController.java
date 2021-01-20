package com.dsoft.m2u.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dsoft.m2u.api.dto.UserResDTO;
import com.dsoft.m2u.api.request.LoginRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.api.response.JwtResponse;
import com.dsoft.m2u.domain.Token;
import com.dsoft.m2u.domain.User;
import com.dsoft.m2u.exception.ResourceUnauthorizedException;
import com.dsoft.m2u.security.AuthService;
import com.dsoft.m2u.security.JwtProvider;
import com.dsoft.m2u.service.TokenService;
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
public class LoginController extends BaseController{
	
	private static final Logger logger = LogManager.getLogger(LoginController.class);

    @Value("Authorization")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "Login with username and password", response = BaseResponse.class)
    @RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO +
            RestURL.LOGIN, method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("========== LoginController.authenticateUser START ===========");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Token token = jwtProvider.generateJwtToken(authentication);
            User userLogin = authService.getLoggedUser();

            token.setUser(userLogin);
            tokenService.storeToken(token);

            UserResDTO userResponse = MapperUtil.mapper(userLogin, UserResDTO.class);

            logger.info(userLogin.toString());
            return ResponseEntity.ok(new JwtResponse(token.getTokenId(), userResponse));
        } catch (Exception e) {
			logger.warn(e.getMessage());
            throw new ResourceUnauthorizedException("Username or password don't correct!");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    @ApiOperation(value = "Logout user account", response = BaseResponse.class)
    @RequestMapping(value = RestURL.VERSION_ONE_DOT_ZERO +
            RestURL.LOGOUT, method = RequestMethod.DELETE)
    public BaseResponse logout(@RequestHeader(name="Authorization") String tokenHeader) {
        logger.info("========== LoginController.logout START ==========");

        String bearer = "Bearer ";
        return tokenService.deleteToken(tokenHeader.substring(bearer.length()));
    }
}