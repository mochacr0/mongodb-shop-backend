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
    public static final String PAGE_NUMBER_DESCRIPTION = "Số trang hiện tại (mặc định là trang đầu tiên)";
    public static final String PAGE_SIZE_DESCRIPTION = "Số lượng phần tử tối đa trong mỗi trang";
    public static final String SORT_ORDER_DESCRIPTION = "Chiều sắp xếp";
    public static final String SORT_ORDER_EXAMPLE = "asc (ascending) or desc (descending)";
    public static final String SORT_PROPERTY_DESCRIPTION = "Thuộc tính sắp xếp phần tử được áp dụng";

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
    public static final String OAUTH2_AUTHENTICATION_SUCCESS_REDIRECT_PATTERN = "/?accessToken=%s&refreshToken=%s";
    public static final String OAUTH2_AUTHENTICATION_FAILURE_REDIRECT = "/oauth2?error=";


    //Users controller constants
    public static final String USERS_ROUTE = "/users";
    public static final String USERS_REGISTER_USER_ROUTE = USERS_ROUTE;
    public static final String USERS_UPDATE_USER_ROUTE = USERS_ROUTE;

    public static final String USERS_GET_USERS_ROUTE = USERS_ROUTE;
    public static final String USERS_BY_USER_ID_ROUTE = USERS_ROUTE + "/{userId}";

    public static final String USERS_GET_CURRENT_USER_ROUTE = USERS_ROUTE + "/current";
    public static final String USERS_GET_USER_BY_ID_ROUTE = USERS_BY_USER_ID_ROUTE;
    public static final String USERS_DELETE_USER_BY_ID_ROUTE = USERS_BY_USER_ID_ROUTE;
    public static final String USERS_ACTIVATE_USER_CREDENTIALS_ROUTE = USERS_BY_USER_ID_ROUTE + "/activate";
    public static final String USERS_ADDRESSES_ROUTE = USERS_ROUTE + "/addresses";
    public static final String USERS_GET_CURRENT_USER_ADDRESSES_ROUTE = USERS_ADDRESSES_ROUTE;
    public static final String USERS_CREATE_ADDRESSES_ROUTE = USERS_ADDRESSES_ROUTE;
    public static final String USERS_GET_ADDRESS_BY_ID_ROUTE = USERS_ADDRESSES_ROUTE + "/{addressId}";
    public static final String USERS_UPDATE_ADDRESS_ROUTE = USERS_GET_ADDRESS_BY_ID_ROUTE;
    public static final String USERS_DELETE_ADDRESS_BY_ID_ROUTE = USERS_GET_ADDRESS_BY_ID_ROUTE;

    //Swagger security schemes
    public static final String SWAGGER_SECURITY_SCHEME_BEARER_AUTH = "Bearer Auth";

    //Category constants
    public static final String CATEGORY_ROUTE = "/categories";
    public static final String CATEGORY_CREATE_CATEGORY_ROUTE = CATEGORY_ROUTE;
    public static final String CATEGORY_GET_CATEGORIES_ROUTE = CATEGORY_ROUTE;
    public static final String CATEGORY_GET_CATEGORY_BY_ID_ROUTE = CATEGORY_ROUTE + "/{categoryId}";
    public static final String CATEGORY_UPDATE_CATEGORY_ROUTE = CATEGORY_GET_CATEGORY_BY_ID_ROUTE;
    public static final String CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE = CATEGORY_GET_CATEGORY_BY_ID_ROUTE;
    public static final String CATEGORY_GET_DEFAULT_CATEGORY_ROUTE = CATEGORY_ROUTE + "/default";

    /**
     * Product constants
     */
    public static final String PRODUCT_ROUTE = "/products";
    public static final String PRODUCT_GET_PRODUCTS_ROUTE = PRODUCT_ROUTE;
    public static final String PRODUCT_CREATE_PRODUCT_ROUTE = PRODUCT_GET_PRODUCTS_ROUTE;
    public static final String PRODUCT_GET_PRODUCT_BY_ID_ROUTE = PRODUCT_GET_PRODUCTS_ROUTE + "/{productId}";
    public static final String PRODUCT_UPDATE_PRODUCT_ROUTE = PRODUCT_GET_PRODUCT_BY_ID_ROUTE;
    public static final String PRODUCT_SEARCH_PRODUCTS_ROUTE = PRODUCT_GET_PRODUCTS_ROUTE + "/search";
    public static final String PRODUCT_DELETE_PRODUCT_BY_ID_ROUTE = PRODUCT_GET_PRODUCT_BY_ID_ROUTE;

    /**
     * Cart constants
     */
    public static final String CART_ROUTE  = "/cart";
    public static final String CART_GET_CURRENT_CART_ROUTE = CART_ROUTE;
    public static final String CART_ADD_ITEM_ROUTE = CART_ROUTE + "/add";
    public static final String CART_UPDATE_ITEM_ROUTE = CART_ROUTE + "/update";
    public static final String CART_REMOVE_ITEMS_ROUTE = CART_ROUTE + "/remove";

    /**
     * Media constants
     */
    public static final String MEDIA_ROUTE = "/media";
    public static final String MEDIA_UPLOAD_IMAGE_ROUTE = MEDIA_ROUTE + "/image";
    public static final String MEDIA_UPLOAD_MULTIPLE_IMAGES_ROUTE = MEDIA_ROUTE + "/multiImages";

    /**
     * Order constants
     */
    public static final String ORDER_ROUTE = "/orders";
    public static final String ORDER_CREATE_ORDER_ROUTE = ORDER_ROUTE;
    public static final String ORDER_GET_ORDER_BY_ID_ROUTE = ORDER_ROUTE + "/{orderId}";
    public static final String ORDER_PAYMENT_ROUTE = ORDER_GET_ORDER_BY_ID_ROUTE + "/payment";
    public static final String ORDER_INITIATE_PAYMENT_ROUTE = ORDER_PAYMENT_ROUTE + "/initiate";
    public static final String ORDER_IPN_REQUEST_CALLBACK_ROUTE = ORDER_ROUTE + "/momo/callback";
    public static final String ORDER_REQUEST_ORDER_REFUND_ROUTE = ORDER_PAYMENT_ROUTE + "/refund";
    public static final String ORDER_CANCEL_ORDER_ROUTE = ORDER_GET_ORDER_BY_ID_ROUTE + "/cancel";
    public static final String ORDER_ACCEPT_ORDER_ROUTE = ORDER_GET_ORDER_BY_ID_ROUTE + "/accept";
    public static final String ORDER_PLACE_SHIPMENT_ORDER_ROUTE = ORDER_GET_ORDER_BY_ID_ROUTE + "/shipment";
    public static final String ORDER_UPDATE_SHIPMENT_STATUS_CALLBACK_ROUTE = ORDER_ROUTE + "/ghtk/callback";
    public static final String ORDER_CONFIRM_ORDER_DELIVERED_ROUTE = ORDER_GET_ORDER_BY_ID_ROUTE + "/confirmDelivered";

    /**
     * Shipping constants
     */
    public static final String SHIPMENT_ROUTE = "/shipments";
    public static final String SHIPMENT_GET_LV4_ADDRESSES_ROUTE = SHIPMENT_ROUTE + "/lv4Addresses";
    public static final String SHIPMENT_CALCULATE_DELIVERY_FEE_ROUTE = SHIPMENT_ROUTE + "/calculateFee";

    /**
     * Shop address constants
     */
    public static final String SHOP_ADDRESS_ROUTE = "/shopAddresses";
    public static final String SHOP_ADDRESS_CREATE_ADDRESS_ROUTE = SHOP_ADDRESS_ROUTE;
    public static final String SHOP_ADDRESS_GET_ADDRESSES_ROUTE = SHOP_ADDRESS_ROUTE;
    public static final String SHOP_ADDRESS_GET_ADDRESS_BY_ID_ROUTE = SHOP_ADDRESS_ROUTE + "/{shopAddressId}";
    public static final String SHOP_ADDRESS_UPDATE_ADDRESS_BY_ID_ROUTE = SHOP_ADDRESS_GET_ADDRESS_BY_ID_ROUTE;
    public static final String SHOP_ADDRESS_DELETE_ADDRESS_BY_ID_ROUTE = SHOP_ADDRESS_GET_ADDRESS_BY_ID_ROUTE;

    /**
     * Order returns constants
     */
    public static final String ORDER_RETURN_ROUTE = "/returns";
    public static final String ORDER_RETURN_REQUEST_RETURN_ROUTE = ORDER_RETURN_ROUTE;
    public static final String ORDER_RETURN_GET_RETURN_BY_ID_ROUTE = ORDER_RETURN_ROUTE + "/{returnId}";
    public static final String ORDER_RETURN_CONFIRM_RETURN_PROCESSING_ROUTE = ORDER_RETURN_GET_RETURN_BY_ID_ROUTE + "/judge";
    public static final String ORDER_RETURN_ACCEPT_RETURN_REQUEST_ROUTE = ORDER_RETURN_GET_RETURN_BY_ID_ROUTE + "/accept";
    public static final String ORDER_RETURN_PLACE_SHIPMENT_ORDER_ROUTE = ORDER_RETURN_GET_RETURN_BY_ID_ROUTE + "/shipment";

    /**
     * Review constants
     */
    public static final String REVIEW_ROUTE = "/reviews";
    public static final String REVIEW_POST_REVIEW_ROUTE = REVIEW_ROUTE;
    public static final String REVIEW_GET_REVIEWS_ROUTE = REVIEW_ROUTE;
    public static final String REVIEW_GET_REVIEW_BY_ID_ROUTE = REVIEW_ROUTE + "/{reviewId}";
    public static final String REVIEW_UPDATE_REVIEW_ROUTE = REVIEW_GET_REVIEW_BY_ID_ROUTE;
}
