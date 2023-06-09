package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.service.UserAddressServiceImpl.DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE;
import static com.example.springbootmongodb.service.UserAddressServiceImpl.MISMATCHED_USER_IDS_MESSAGE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class UserAddressControllerTest extends AbstractControllerTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateUserAddressMethodTest {
        private User user;
        private final int maxAddressesCount = 5;
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
        void testCreateAddressWithValidBody() throws Exception {
            UserAddress createdUserAddress = performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, createUserAddressData());
            Assertions.assertNotNull(createdUserAddress);
            Assertions.assertEquals(user.getId(), createdUserAddress.getUserId());
            performDelete(USERS_DELETE_ADDRESS_BY_ID_ROUTE, createdUserAddress.getId());
        }

        @Test
        void testCreateValidNumberOfAddresses() throws Exception {
            List<UserAddress> userAddresses = new ArrayList<>();
            for (int i = 0; i < maxAddressesCount; i++) {
                userAddresses.add(performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, createUserAddressData()));
            }
            ArrayList<UserAddress> createdUserAddresses = performGetWithReferencedType(USERS_GET_CURRENT_USER_ADDRESSES_ROUTE, new TypeReference<>(){});
            Assertions.assertFalse(createdUserAddresses.isEmpty());
            Assertions.assertEquals(maxAddressesCount, createdUserAddresses.size());
            userAddresses.sort(new AddressComparator<>());
            createdUserAddresses.sort(new AddressComparator<>());
            for (int i = 0; i < maxAddressesCount; i++) {
                Assertions.assertEquals(userAddresses.get(i).getName(), createdUserAddresses.get(i).getName());
            }
        }

        @Test
        void testCreateAddressWithMaxLimitExceeded() throws Exception {
            for (int i = 0; i < maxAddressesCount; i++) {
                performPost(USERS_CREATE_ADDRESSES_ROUTE, createUserAddressData()).andExpect(status().isOk());
            }
            UserAddress additionalAddress = createUserAddressData();
            performPost(USERS_CREATE_ADDRESSES_ROUTE, additionalAddress).andExpect(status().isBadRequest());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateUserAddressTest {
        private User user;
        private UserAddress userAddress;
        @BeforeEach
        void setUp() throws Exception {
            user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(user.getId());
            login(user.getName(), DEFAULT_PASSWORD);
            userAddress = performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, createUserAddressData());
        }

        @AfterEach
        void tearDown() throws Exception {
            if (user != null) {
                deleteUser(user.getId());
            }
        }

        @Test
        void testUpdateAddressWithInvalidId() throws Exception {
            UserAddress userAddress = new UserAddress();
            userAddress.setName(generateRandomString());
            //missing address id
            String missingId = null;
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, missingId).andExpect(status().isNotFound());
            missingId = "";
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, missingId).andExpect(status().isNotFound());
            String invalidAddressId = "64805c5bdb4a3449c81a9bed";
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, invalidAddressId).andExpect(status().isNotFound());
        }

        @Test
        void testUpdateAddressWithMismatchedIds() throws Exception {
            User newUser = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(newUser.getId());
            login(newUser.getName(), DEFAULT_PASSWORD);
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, userAddress.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is(MISMATCHED_USER_IDS_MESSAGE)));
            deleteUser(newUser.getId());
        }

        @Test
        void testSetAddressDefault() throws Exception {
            userAddress.setDefault(true);
            userAddress = performPut(USERS_UPDATE_ADDRESSES_ROUTE, UserAddress.class, userAddress, userAddress.getId());
            user = performGet(USERS_GET_USER_BY_ID_ROUTE, User.class, user.getId());
            Assertions.assertEquals(user.getDefaultAddressId(), userAddress.getId());
        }

        @Test
        void testUnsetDefaultAddress() throws Exception {
            userAddress.setDefault(true);
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, userAddress.getId()).andExpect(status().isOk());
            userAddress.setDefault(false);
            performPut(USERS_UPDATE_ADDRESSES_ROUTE, userAddress, userAddress.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE)));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteUserAddressTest {
        private User user;
        @BeforeEach
        void setUp() throws Exception {
            user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            activateUser(user.getId());
            login(user.getName(), DEFAULT_PASSWORD);
        }

        @AfterEach
        void tearDown() throws Exception {
            user = performGet(USERS_GET_USER_BY_ID_ROUTE, User.class, user.getId());
            if (user != null && StringUtils.isNotEmpty(user.getId())) {
                deleteUser(user.getId());
            }
        }

        @Test
        void testDeleteUserAddressWithValidArgs() throws Exception {
            UserAddress userAddress = createUserAddressData();
            userAddress = performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, userAddress);
            performDelete(USERS_DELETE_ADDRESS_BY_ID_ROUTE, userAddress.getId()).andExpect(status().isOk());
            performGet(USERS_GET_ADDRESS_BY_ID_ROUTE, userAddress.getId()).andExpect(status().isNotFound());
        }

        @Test
        void testDeleteUserAndTheirAddresses() throws Exception {
            UserAddress userAddress = createUserAddressData();
            userAddress = performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, userAddress);
            performDelete(USERS_DELETE_USER_BY_ID_ROUTE, user.getId());
            performGet(USERS_GET_ADDRESS_BY_ID_ROUTE, userAddress.getId()).andExpect(status().isNotFound());
        }

        @Test
        void testDeleteDefaultAddress() throws Exception {
            UserAddress userAddress = createUserAddressData();
            userAddress.setDefault(true);
            userAddress = performPost(USERS_CREATE_ADDRESSES_ROUTE, UserAddress.class, userAddress);
            performDelete(USERS_DELETE_ADDRESS_BY_ID_ROUTE, userAddress.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", Matchers.is(DEFAULT_ADDRESS_CHANGE_REQUIRED_MESSAGE)));
        }

    }

    public static class AddressComparator<T extends UserAddress> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private UserAddress createUserAddressData() {
        UserAddress userAddress = new UserAddress();
        userAddress.setName(generateRandomString());
        return userAddress;
    }
}
