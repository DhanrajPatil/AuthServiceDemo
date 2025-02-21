package com.elitefolk.authservicedemo.configs;

import org.springframework.stereotype.Component;

@Component
public class AppConstants {

    public static final String TOKEN_HEADER_KEY = "auth_token";

    public static final long TOKEN_EXPIRY = 3600000;

}
