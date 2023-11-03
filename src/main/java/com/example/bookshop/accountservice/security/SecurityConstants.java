package com.example.bookshop.accountservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {
    public static String JWT_KEY;
    public static String JWT_HEADER;

    public static Long JWT_EXP;

    @Value("${constant.security.JWT_KEY}")
    public void setJwtKey(String key) {
        SecurityConstants.JWT_KEY = key;
    }

    @Value("${constant.security.JWT_HEADER}")
    public void setJwtHeader(String header) {
        SecurityConstants.JWT_HEADER = header;
    }

    @Value("${constant.security.JWT_EXP}")
    public void setJwtHeader(Long expiration) {
        SecurityConstants.JWT_EXP = expiration;
    }
}
