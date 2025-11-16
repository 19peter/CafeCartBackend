package com.peters.cafecart.Constants;

import java.util.List;

public class Constants {

    public static final String API_V1 = "/api/v1";
    public static final String CURRENT_API = API_V1;


    
    public static final String CUSTOMER_AUTH_LOGIN = CURRENT_API + "/auth/login/customer";
    public static final String CUSTOMER_AUTH_REGISTER = CURRENT_API + "/auth/register/customer";
    public static final String CUSTOMER_AUTH_REFRESH_TOKEN = CURRENT_API + "/auth/refresh-token/customer";

    public static final String VENDOR_AUTH_LOGIN = CURRENT_API + "/auth/login/vendor";
    public static final String VENDOR_AUTH_REGISTER = CURRENT_API + "/auth/register/vendor";
    public static final String VENDOR_AUTH_REFRESH_TOKEN = CURRENT_API + "/auth/refresh-token/vendor";

    public static final String SHOP_AUTH_LOGIN = CURRENT_API + "/auth/login/vendor-shop";
    public static final String SHOP_AUTH_REGISTER = CURRENT_API + "/auth/register/vendor-shop";
    public static final String SHOP_AUTH_REFRESH_TOKEN = CURRENT_API + "/auth/refresh-token/vendor-shop";

    public static final List<String> ALLOWED_PATHS = List.of(
            CUSTOMER_AUTH_LOGIN,
            CUSTOMER_AUTH_REGISTER,
            CUSTOMER_AUTH_REFRESH_TOKEN,
            VENDOR_AUTH_LOGIN,
            VENDOR_AUTH_REGISTER,
            VENDOR_AUTH_REFRESH_TOKEN,
            SHOP_AUTH_LOGIN,
            SHOP_AUTH_REGISTER,
            SHOP_AUTH_REFRESH_TOKEN);
}
