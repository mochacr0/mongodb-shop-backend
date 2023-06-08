package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class UserControllerTest extends AbstractControllerTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FindUsersMethodTest {
        private User user;
        @BeforeAll
        void setUp() throws Exception {
            user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(user.getId());
            login(user.getName(), DEFAULT_PASSWORD);
        }

        @AfterAll
        void tearDown() throws Exception {
            if (user != null) {
                deleteUser(user.getId());
            }
        }

        @Test
        void testFindUsersWithInvalidPageNumber() throws Exception {
            performGet(USERS_GET_USERS_ROUTE + "?page={page}", NEGATIVE_INT_VALUE)
                    .andExpect(status().isBadRequest());
            String stringPageSize = "string";
            performGet(USERS_GET_USERS_ROUTE + "?page={page}", STRING_VALUE)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsersWithInvalidPageSize() throws Exception {
            performGet(USERS_GET_USERS_ROUTE + "?pageSize={pageSize}", ZERO_INT_VALUE)
                    .andExpect(status().isBadRequest());
            performGet(USERS_GET_USERS_ROUTE + "?pageSize={pageSize}", NEGATIVE_INT_VALUE)
                    .andExpect(status().isBadRequest());
            performGet(USERS_GET_USERS_ROUTE + "?pageSize={pageSize}", STRING_VALUE)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsersWithInvalidSort() throws Exception {
            performGet(USERS_GET_USERS_ROUTE +"?sortDirection={sortDirection}", INVALID_SORT_DIRECTION)
                    .andExpect(status().isBadRequest());
//            performGet(USERS_GET_USERS_ROUTE +"?sortProperty={sortProperty}", INVALID_SORT_PROPERTY)
//                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsers() throws Exception {
            List<RegisterUserRequest> registerRequests = new ArrayList<>();
            int totalUsers = 10;
            for (int i = 0; i < totalUsers; i++) {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName("usertest" + i);
                request.setEmail("usertest" + i + "@gmail.com");
                request.setPassword("Password");
                request.setConfirmPassword("Password");
                registerRequests.add(request);
                createUser(request);
            }
            int currentPage = 0;
            int pageSize = 3;
            List<User> savedUsers = new ArrayList<>();
            PageData<User> data;
            do {
                data = performGetWithReferencedType(USERS_GET_USERS_ROUTE + "?page={page}&pageSize={pageSize}&sortDirection={sortDirection}&sortProperty={sortProperty}",
                        new TypeReference<>(){},
                        currentPage, pageSize, "desc", "createdAt");
                savedUsers.addAll(data.getData().stream().toList());
                if (data.hasNext()) {
                    currentPage++;
                }
            } while (data.hasNext());
            savedUsers = savedUsers.subList(0, totalUsers);
            for (User savedUser : savedUsers) {
                deleteUser(savedUser.getId());
            }
            savedUsers.sort(new UserComparator<>());
//            registerRequests.sort(new RegisterUserRequestComparator<>());
            boolean areListsTheSame = true;
            for (int i = 0; i < registerRequests.size(); i++) {
                if (!registerRequests.get(i).getName().equals(savedUsers.get(i).getName())
                        || !registerRequests.get(i).getEmail().equals(savedUsers.get(i).getEmail())) {
                    areListsTheSame = false;
                    log.info("----------------" + registerRequests.get(i));
                    log.info("----------------" + savedUsers.get(i));
                    break;
                }
            }
            Assertions.assertTrue(areListsTheSame, "The expected list and the actual list are not equal");
        }
    }

    @Nested
    class RegisterUserMethodTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class RegisterUserWithExistedNameAndEmail {
            private User defaultUser;
            @BeforeEach
            void setUp() throws Exception {
                defaultUser = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
                activateUser(defaultUser.getId());
            }
            @AfterEach
            void tearDown() throws Exception {
                if (defaultUser != null) {
                    login(defaultUser.getName(), DEFAULT_PASSWORD);
                    deleteUser(defaultUser.getId());
                }
            }
            @Test
            void testRegisterUserWithExistedName() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName(defaultUser.getName());
                request.setEmail("duplicatednameuser@gmail.com");
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(DEFAULT_PASSWORD);
                performPost(USERS_REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithExistedEmail() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName("duplicatedUserName");
                request.setEmail(defaultUser.getEmail());
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(DEFAULT_PASSWORD);
                performPost(USERS_REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        class RegisterUserWithMissingRequireFieldTest {
            private RegisterUserRequest request;
            @BeforeEach
            void setUp() {
                request = new RegisterUserRequest();
                request.setName(generateUsername());
                request.setEmail(generateEmail());
                request.setMatchedPasswords(DEFAULT_PASSWORD);
            }
            @Test
            void testRegisterUserWithMissingNameField () throws Exception {
                request.setName(null);
                performPost(USERS_REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingEmailField () throws Exception {
                request.setEmail(null);
                performPost(USERS_REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingPasswordField () throws Exception {
                request.setPassword(null);
                performPost(USERS_REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingConfirmPasswordField () throws Exception {
                request.setConfirmPassword(null);
                performPost(USERS_REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
        }

        @Test
        void testRegisterUserWithInvalidFormatEmail() throws Exception {
            String invalidFormatEmail = "nguyentrihai.com";
            RegisterUserRequest request = new RegisterUserRequest();
            request.setName(generateUsername());
            request.setEmail(invalidFormatEmail);
            request.setMatchedPasswords(generatePassword());
            performPost(USERS_REGISTER_USER_ROUTE, request)
                    .andExpect(status().isBadRequest());
        }

        @Nested
        class RegisterUserWithInvalidPasswordTest {
            RegisterUserRequest request;
            @BeforeEach
            void setUp() {
                request = new RegisterUserRequest();
                request.setName(generateUsername());
                request.setEmail(generateEmail());
            }
            @Test
            void testRegisterUserWithUnmatchedPasswords() throws Exception {
                String unmatchedConfirmPassword = "Not" + DEFAULT_PASSWORD;
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(unmatchedConfirmPassword);
                performPost(USERS_REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithPasswordViolations() throws Exception {
                String noUpperCaseLetterPassword = "string";
                request.setMatchedPasswords(noUpperCaseLetterPassword);
                performPost(USERS_REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
                String underMinimumLengthPassword = "1";
                request.setMatchedPasswords(underMinimumLengthPassword);
                performPost(USERS_REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
        }

        @Test
        void testRegisterUserWithValidBody() throws Exception {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setName(generateUsername());
            request.setEmail(generateEmail());
            request.setPassword(DEFAULT_PASSWORD);
            request.setConfirmPassword(DEFAULT_PASSWORD);
            User user = readResponse(performPost(USERS_ROUTE + "/register", request).andExpect(status().isOk()), User.class);
            activateUser(user.getId());
            login(user.getName(), DEFAULT_PASSWORD);
            deleteUser(user.getId());
        }
    }

    @Test
    void testDeleteUser() throws Exception {
        User createdUser = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        Assertions.assertNotNull(createdUser);
        activateUser(createdUser.getId());
        login(createdUser.getName(), DEFAULT_PASSWORD);
        performDelete(USERS_DELETE_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isOk());
        performGet(USERS_GET_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isNotFound());
    }

//    @Test
    void testActivateUserCredentialsByUserId() throws Exception {
        String password = generatePassword();
        User user = createUser(generateUsername(), generateEmail(), password, password);
        //note: case invalid uuid
        performPostWithEmptyBody(USERS_GET_USER_BY_ID_ROUTE + "/activate", user.getId().toString()).andExpect(status().isOk());
        login(user.getName(), password);
        deleteUser(user.getId());
    }

//    @Test
    void testDeleteUnverifiedUsers() throws Exception {
        List<User> createdUsers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setName("usertest" + i);
            request.setEmail("usertest" + i + "@gmail.com");
            request.setPassword("Password");
            request.setConfirmPassword("Password");
            createdUsers.add(createUser(request));
        }
        performDelete(USERS_ROUTE + "/test-delete").andExpect(status().isOk());
        for (User createdUser : createdUsers) {
            performGet(USERS_GET_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isNotFound());
        }
    }

    public class UserComparator<T extends User> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
    }

    public class RegisterUserRequestComparator<T extends RegisterUserRequest> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
 }
