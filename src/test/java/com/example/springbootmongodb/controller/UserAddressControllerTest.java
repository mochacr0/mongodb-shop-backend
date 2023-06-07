package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.util.Assert;

import static com.example.springbootmongodb.controller.ControllerTestConstants.DEFAULT_PASSWORD;

@Slf4j
class UserAddressControllerTest extends AbstractControllerTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateUserAddressMethodTest {
        private User user;

        @BeforeEach
        void setUp() throws Exception {
            user = createUser(getRandomUsername(), getRandomEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
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
            userAddress.setName(getRandomUsername());
            userAddress.setPhoneNumber(getRandomString());
            userAddress.setProvince(getRandomString());
            userAddress.setDistrict(getRandomString());
            userAddress.setWard(getRandomString());
            userAddress.setStreetAddress(getRandomString());
            UserAddress createdUserAddress = performPost("/users/addresses/create", UserAddress.class, userAddress);
            Assertions.assertNotNull(createdUserAddress);
            Assertions.assertEquals(user.getId(), createdUserAddress.getUserId());
        }
    }

}
