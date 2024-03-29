package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.ChangePasswordRequest;
import com.example.springbootmongodb.common.data.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static com.example.springbootmongodb.controller.ControllerConstants.AUTH_ROUTE;
import static com.example.springbootmongodb.service.UserCredentialsServiceImpl.ACCOUNT_LOCKED_MESSAGE;
import static com.example.springbootmongodb.service.UserCredentialsServiceImpl.USERNAME_PASSWORD_INCORRECT_MESSAGE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {

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
            user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(user.getId());
            login(user.getName(), DEFAULT_PASSWORD);
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
            for (int i = 0; i < securitySettings.getMaxFailedLoginAttempts(); i++) {
                performLogin(user.getName(), INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(USERNAME_PASSWORD_INCORRECT_MESSAGE)));
            }
            //exceeded maximum allowed attempts, should return locked
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(ACCOUNT_LOCKED_MESSAGE)));
            //Wait until the expiration time has passed
            Thread.sleep(securitySettings.getFailedLoginLockExpirationMillis());
            //Now login should return ok
            performLogin(user.getName(), DEFAULT_PASSWORD).andExpect(status().isOk());
        }

        @Test
        void testLoginFailMultipleTimesButNotWithinTheSameInterval() throws Exception{
            //test login fail multiple times but not within the same interval
            //first failed login
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(USERNAME_PASSWORD_INCORRECT_MESSAGE)));
            //Wait until the interval time has passed
            Thread.sleep(securitySettings.getFailedLoginIntervalMillis());
            //then try to fail maximum times, should return bad credentials
            for (int i = 0; i < securitySettings.getMaxFailedLoginAttempts(); i++) {
                performLogin(user.getName(), INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(USERNAME_PASSWORD_INCORRECT_MESSAGE)));
            }
            //should return locked
            performLogin(user.getName(), INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(ACCOUNT_LOCKED_MESSAGE)));
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
            user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(user.getId());
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
