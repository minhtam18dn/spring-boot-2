package com.dsoft.m2u.security;

import com.dsoft.m2u.domain.User;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
public interface AuthService {
    User getLoggedUser();
    boolean isLogged();
}
