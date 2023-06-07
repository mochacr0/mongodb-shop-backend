package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.ChangePasswordRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.springbootmongodb.controller.ControllerConstants.AUTH_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.USERS_GET_USER_BY_ID_ROUTE;
import static com.example.springbootmongodb.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {
    private final String BAD_CREDENTIALS_EXCEPTION_MESSAGE = "Authentication failed: The username or password you entered is incorrect. Please try again";
    private final String LOCKED_EXCEPTION_MESSAGE = "Authentication failed: Username was locked due to security policy";
    @Autowired
    private UserService userService;


//    @Test
//    void testGetUserPasswordPolicy() throws Exception {
//        performGet(AUTH_ROUTE + "/passwordPolicy").andExpect(status().isOk());
//    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class LoginMethodTest {
        private final String INVALID_PASSWORD = "Not" + DEFAULT_PASSWORD;
        private User user;

        @BeforeEach
        void setUp() throws Exception {
            user = createUser(getRandomUsername(), getRandomEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            performPostWithEmptyBody(USERS_GET_USER_BY_ID_ROUTE + "/activate", user.getId().toString());
        }

        @AfterEach
        void tearDown() throws Exception {
            if (user != null) {
                deleteUser(user.getId());
            }
        }

        @Test
        void testLoginWithValidCredentials() throws Exception {
            performLogin(user.getName(), DEFAULT_PASSWORD).andExpect(status().isOk());
        }

        @Test
        void testLoginWithMissingUsername() throws Exception {
            performLogin(null, DEFAULT_PASSWORD).andExpect(status().isUnauthorized());
            performLogin("", DEFAULT_PASSWORD).andExpect(status().isUnauthorized());
        }

        @Test
        void testLoginWithMissingPassword() throws Exception {
            performLogin(user.getName(), null).andExpect(status().isUnauthorized());
            performLogin(user.getName(), "").andExpect(status().isUnauthorized());
        }

        @Test
        void testLoginWithFailedLoginLock() throws Exception {
            //failed maximum times, should return bad credentials
            for (int i = 0; i < maxFailedLoginAttempts; i++) {
                performLogin(user.getName(), INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            }
            //exceeded maximum allowed attempts, should return locked
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(LOCKED_EXCEPTION_MESSAGE)));
            //Wait until the expiration time has passed
            Thread.sleep(failedLoginLockExpirationMillis);
            //Now login should return ok
            performLogin(user.getName(), DEFAULT_PASSWORD).andExpect(status().isOk());
        }

        @Test
        void testLoginFailMultipleTimesButNotWithinTheSameInterval() throws Exception{
            //test login fail multiple times but not within the same interval
            //first failed login
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            //Wait until the interval time has passed
            Thread.sleep(failedLoginIntervalMillis);
            //then try to fail maximum times, should return bad credentials
            for (int i = 0; i < maxFailedLoginAttempts; i++) {
                performLogin(user.getName(), INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            }
            //should return locked
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(LOCKED_EXCEPTION_MESSAGE)));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ChangePasswordTest {
        private User user;
        private ChangePasswordRequest changePasswordRequest;
        private final String NEW_PASSWORD = "new" + DEFAULT_PASSWORD;

        @BeforeEach
        void setUp() throws Exception {
            user = createUser(getRandomUsername(), getRandomEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            performPostWithEmptyBody(USERS_GET_USER_BY_ID_ROUTE + "/activate", user.getId().toString());
            login(user.getName(),DEFAULT_PASSWORD);
            changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setCurrentPassword(DEFAULT_PASSWORD);
            changePasswordRequest.setNewPassword(NEW_PASSWORD);
            changePasswordRequest.setConfirmPassword(NEW_PASSWORD);
        }

        @AfterEach
        void tearDown() throws Exception {
            if (user != null) {
                deleteUser(user.getId());
            }
        }
        @Test
        void testChangePasswordWithInvalidCurrentPassword() throws Exception {
            //current password is empty
            changePasswordRequest.setCurrentPassword(null);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            changePasswordRequest.setCurrentPassword("");
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            //current password is not matched
            changePasswordRequest.setCurrentPassword("not" + DEFAULT_PASSWORD);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
        }

        @Test
        void testChangePasswordWithInvalidNewPassword() throws Exception {
            //new password is empty
            changePasswordRequest.setNewPassword(null);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            changePasswordRequest.setNewPassword("");
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            //current password and new password are the same
            changePasswordRequest.setNewPassword(DEFAULT_PASSWORD);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
        }

        @Test
        void testChangePasswordWithInvalidConfirmPassword() throws Exception {
            //confirm password is empty
            changePasswordRequest.setConfirmPassword(null);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            changePasswordRequest.setConfirmPassword("");
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
            //new password and confirm password are not matched
            changePasswordRequest.setConfirmPassword("Not" + NEW_PASSWORD);
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isBadRequest());
        }

        @Test
        void testChangePasswordWithValidBody() throws Exception {
            performPost(AUTH_ROUTE + "/changePassword",changePasswordRequest).andExpect(status().isOk());
            performLogin(user.getName(), NEW_PASSWORD).andExpect(status().isOk());
        }

    }
}
