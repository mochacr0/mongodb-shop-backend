package com.example.springbootmongodb.model;

public class ModelConstants {
    /**
     * Generics constants
     */
    public static final String NAME_FIELD = "name";
    public static final String CREATED_AT_FIELD = "createdAt";
    public static final String UPDATED_AT_FIELD = "updatedAt";

    /**
     * ID constants
     */
    public static final String ID_FIELD = "id";
    public static final String USER_CREDENTIALS_USER_ID_FIELD = "userId";

    /**
     * User constants
     */
    public static final String USER_COLLECTION_NAME = "users";
    public static final String USER_PASSWORD_FIELD = "password";
    public static final String USER_AUTHORITY_FIELD = "authority";
    public static final String USER_EMAIL_FIELD = "email";

    /**
     * User credentials constants
     */
//    public static final String USER_CREDENTIALS_COLLECTION = "userCredentials";
//    public static final String USER_CREDENTIALS_ACTIVATION_TOKEN_FIELD = "activationToken";
//    public static final String USER_CREDENTIALS_PASSWORD_FIELD = "password";
//    public static final String USER_CREDENTIALS_PASSWORD_RESET_TOKEN_FIELD = "passwordResetToken";
//    public static final String USER_CREDENTIALS_PASSWORD_RESET_TOKEN_EXPIRATION_MILLIS_FIELD = "passwordResetTokenExpirationMillis";
//    public static final String USER_CREDENTIALS_FAILED_LOGIN_HISTORY_TOKEN_FIELD = "failedLoginHistory";
//    public static final String USER_CREDENTIALS_IS_VERIFIED_FIELD = "isVerified";
//    public static final String USER_CREDENTIALS_IS_ENABLED_FIELD = "isEnabled";
//    public static final String USER_CREDENTIALS_ACTIVATION_TOKEN_EXPIRATION_MILLIS_FIELD = "activationTokenExpirationMillis";
//    public static final String USER_CREDENTIALS_FAILED_LOGIN_LOCK_EXPIRATION_MILLIS_FIELD = "failedLoginLockExpirationMillis";
//    public static final String USER_CREDENTIALS_FAILED_LOGIN_COUNT_FIELD = "failedLoginCount";
//    public static final String USER_CREDENTIALS_FIRST_FAILED_LOGIN_ATTEMPT_MILLIS_FIELD = "firstFailedLoginAttemptMillis";
//    public static final String USER_CREDENTIALS_ADDITIONAL_INFO_FIELD = "additionalInfo";

    /**
     * User address constants
     */
    public static final String USER_ADDRESS_COLLECTION_NAME = "addresses";


    /**
     * Category constants
     */
    public static final String CATEGORY_COLLECTION_NAME = "categories";
    public static final String CATEGORY_DEFAULT_CATEGORY_NAME = "Uncategorized";

    /**
     * Product constants
     */
    public static final String PRODUCT_COLLECTION_NAME = "products";
    public static final String PRODUCT_ITEM_COLLECTION_NAME = "items";
    public static final String PRODUCT_VARIATION_COLLECTION_NAME = "variants";
    public static final String PRODUCT_VARIATION_OPTION_COLLECTION_NAME = "options";
}
