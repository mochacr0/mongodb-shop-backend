package com.example.springbootmongodb.controller;

public class ControllerConstants {
    /**
     * Generics constants.
     */
    public static final String ID_PARAM = "id";
    public static final String PAGE_NUMBER_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "pageSize";
    public static final String SORT_ORDER_PARAM = "sortOrder";
    public static final String SORT_PROPERTY_PARAM = "sortProperty";
    public static final String TEXT_SEARCH_PARAM = "textSearch";

    public static final int PAGE_NUMBER_DEFAULT_VALUE = 0;
    public static final String PAGE_NUMBER_DEFAULT_STRING_VALUE = "0";
    public static final int PAGE_SIZE_DEFAULT_VALUE = 10;
    public static final String PAGE_SIZE_DEFAULT_STRING_VALUE = "10";
    public static final String SORT_DIRECTION_DEFAULT_VALUE = "desc";
    public static final String SORT_PROPERTY_DEFAULT_VALUE = "createdAt";
    public static final String USER_ID_PARAM = "userId";
    public static final String PAGE_NUMBER_DESCRIPTION = "Sequence number of pages starting from 0";
    public static final String PAGE_SIZE_DESCRIPTION = "Maximum amount of entities in one page";
    public static final String SORT_ORDER_DESCRIPTION = "Sorting direction";
    public static final String SORT_ORDER_EXAMPLE = "asc (ascending) or desc (descending)";
    public static final String SORT_PROPERTY_DESCRIPTION = "Entity property to sort by";

    //Auth controller constants
    public static final String AUTH_ROUTE = "/auth";
    public static final String AUTH_LOGIN_ENDPOINT = AUTH_ROUTE + "/login";
    public static final String AUTH_REFRESH_TOKEN_ROUTE = AUTH_ROUTE + "/refreshToken";
    public static final String AUTH_ACTIVATE_EMAIL_ROUTE = AUTH_ROUTE + "/activate";
    public static final String AUTH_RESEND_ACTIVATION_TOKEN_ROUTE = AUTH_ROUTE + "/activate/resend";
    public static final String AUTH_GET_USER_PASSWORD_POLICY_ROUTE = AUTH_ROUTE + "/passwordPolicy";
    public static final String AUTH_CHANGE_PASSWORD_ROUTE = AUTH_ROUTE + "/changePassword";
    public static final String AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE = AUTH_ROUTE + "/requestPasswordReset";
    public static final String AUTH_RESET_PASSWORD_ROUTE = AUTH_ROUTE + "/resetPassword";
    public static final String OAUTH2_AUTHENTICATION_SUCCESS_REDIRECT = "/?accessToken=";
    public static final String OAUTH2_AUTHENTICATION_FAILURE_REDIRECT = "/oauth2?error=";


    //Users controller constants
    public static final String USERS_ROUTE = "/users";
    public static final String USERS_REGISTER_USER_ROUTE = USERS_ROUTE + "/register";
    public static final String USERS_UPDATE_USER_ROUTE = USERS_ROUTE + "/update";

    public static final String USERS_GET_USERS_ROUTE = USERS_ROUTE;
    public static final String USERS_BY_USER_ID_ROUTE = USERS_ROUTE + "/{userId}";

    public static final String USERS_GET_CURRENT_USER_ROUTE = USERS_ROUTE + "/current";
    public static final String USERS_GET_USER_BY_ID_ROUTE = USERS_BY_USER_ID_ROUTE;
    public static final String USERS_DELETE_USER_BY_ID_ROUTE = USERS_BY_USER_ID_ROUTE;
    public static final String USERS_ACTIVATE_USER_CREDENTIALS_ROUTE = USERS_BY_USER_ID_ROUTE + "/activate";
    public static final String USERS_ADDRESSES_ROUTE = USERS_ROUTE + "/addresses";
    public static final String USERS_GET_CURRENT_USER_ADDRESSES_ROUTE = USERS_ADDRESSES_ROUTE;
    public static final String USERS_CREATE_ADDRESSES_ROUTE = USERS_ADDRESSES_ROUTE + "/create";
    public static final String USERS_UPDATE_ADDRESSES_ROUTE = USERS_ADDRESSES_ROUTE + "/update";
    public static final String USERS_GET_ADDRESS_BY_ID_ROUTE = USERS_ADDRESSES_ROUTE + "/{addressId}";
    public static final String USERS_DELETE_ADDRESSES_ROUTE = USERS_GET_ADDRESS_BY_ID_ROUTE;

    //Swagger security schemes
    public static final String SWAGGER_SECURITY_SCHEME_BEARER_AUTH = "Bearer Auth";

}
