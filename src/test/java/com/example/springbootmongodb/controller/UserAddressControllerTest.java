package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
class UserAddressControllerTest extends AbstractControllerTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateUserAddressMethodTest {
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
        void testCreateAddressWithValidBody() throws Exception {
            UserAddress userAddress = new UserAddress();
            userAddress.setName(generateUsername());
            userAddress.setPhoneNumber(generateRandomString());
            userAddress.setProvince(generateRandomString());
            userAddress.setDistrict(generateRandomString());
            userAddress.setWard(generateRandomString());
            userAddress.setStreetAddress(generateRandomString());
            UserAddress createdUserAddress = performPost("/users/addresses/create", UserAddress.class, userAddress);
            Assertions.assertNotNull(createdUserAddress);
            Assertions.assertEquals(user.getId(), createdUserAddress.getUserId());
        }
    }

}
