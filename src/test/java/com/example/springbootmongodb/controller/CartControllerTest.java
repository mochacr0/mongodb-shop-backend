package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.service.CartServiceImpl.MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE;
import static com.example.springbootmongodb.service.CartServiceImpl.NON_POSITIVE_QUANTITY_ERROR_MESSAGE;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerTest extends AbstractControllerTest {
    private User user;
    private Cart cart;
    private ProductItem productItem;

    private static final String NON_EXISTENT_PRODUCT_ITEM_ID = "64995649fb56f04d1953a877";

    @BeforeAll
    void setUp() throws Exception {
        user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        activateUser(user.getId());
        login(user.getName(), DEFAULT_PASSWORD);
        cart = performGet(CART_GET_CURRENT_CART_ROUTE, Cart.class);
        Product product = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
        productItem = product.getItemMap().get("0");
    }

    @AfterAll
    void tearDown() throws Exception {
        if (user != null && StringUtils.isNotEmpty(user.getId())) {
            User existingUser = performGet(USERS_GET_USER_BY_ID_ROUTE, User.class, user.getId());
            if (existingUser != null) {
                deleteUser(user.getId());
            }
        }
    }

    @AfterEach
    void reset() throws Exception {
        updateCart();
        performPut(CART_REMOVE_ITEMS_ROUTE, cart.getItems().stream().map(cartItem -> cartItem.getProductItem().getId()).toList());
    }

    @Test
    @Order(1)
    void testCartExists() {
        Assertions.assertNotNull(cart);
        Assertions.assertEquals(cart.getUserId(), user.getId());
        Assertions.assertTrue(CollectionUtils.isEmpty(cart.getItems()));
    }

    @Test
    void testAddItemWithNonExistentProductItemId() throws Exception {
        UpdateCartItemRequest request = createUpdateCartItemRequest(NON_EXISTENT_PRODUCT_ITEM_ID, 100);
        performPost(CART_ADD_ITEM_ROUTE, request).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testAddItemWithInvalidQuantity() throws Exception {
        UpdateCartItemRequest request = createUpdateCartItemRequest(productItem.getId(), 0);
        performPost(CART_ADD_ITEM_ROUTE, request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(NON_POSITIVE_QUANTITY_ERROR_MESSAGE)));
        request.setQuantity(-1);
        performPost(CART_ADD_ITEM_ROUTE, request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(NON_POSITIVE_QUANTITY_ERROR_MESSAGE)));
        request.setQuantity(productItem.getQuantity() + 1);
        performPost(CART_ADD_ITEM_ROUTE, request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE)));
    }

    @Test
    void testAddItemWithValidBody() throws Exception {
        UpdateCartItemRequest request = createUpdateCartItemRequest(productItem.getId(), 20);
        performPost(CART_ADD_ITEM_ROUTE, request).andExpect(status().isOk());
        updateCart();
        CartItem addedItem = cart.getItems().get(0);
        Assertions.assertEquals(request.getProductItemId(), addedItem.getProductItem().getId());
        Assertions.assertEquals(request.getQuantity(), addedItem.getQuantity());
    }

    @Test
    void testAddDuplicatedItem() throws Exception {
        UpdateCartItemRequest request = createUpdateCartItemRequest(productItem.getId(), 20);
        performPost(CART_ADD_ITEM_ROUTE, request).andExpect(status().isOk());
        updateCart();
        CartItem addedItem = cart.getItems().get(0);
        Assertions.assertEquals(request.getProductItemId(), addedItem.getProductItem().getId());
        Assertions.assertEquals(request.getQuantity(), addedItem.getQuantity());
        performPost(CART_ADD_ITEM_ROUTE, request).andExpect(status().isOk());
        updateCart();
        addedItem = cart.getItems().get(0);
        Assertions.assertEquals(request.getProductItemId(), addedItem.getProductItem().getId());
        Assertions.assertEquals(request.getQuantity() * 2, addedItem.getQuantity());
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateItemMethodTest {
        @BeforeEach
        void setUp() throws Exception {
            performPost(CART_ADD_ITEM_ROUTE, createUpdateCartItemRequest(productItem.getId(), 10));
            updateCart();
            Assertions.assertTrue(CollectionUtils.isNotEmpty(cart.getItems()));
            Assertions.assertEquals(cart.getItems().get(0).getProductItem().getId(), productItem.getId());
        }

        @Test
        void testUpdateCartItemWithNonExistentProductItemId() throws Exception {
            performPut(CART_UPDATE_ITEM_ROUTE, new UpdateCartItemRequest(NON_EXISTENT_PRODUCT_ITEM_ID, 10))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        void testUpdateCartItemWithInvalidQuantity() throws Exception {
            UpdateCartItemRequest request = new UpdateCartItemRequest(productItem.getId(), -1);
            performPut(CART_UPDATE_ITEM_ROUTE, request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(NON_POSITIVE_QUANTITY_ERROR_MESSAGE)));
            request.setQuantity(0);
            performPut(CART_UPDATE_ITEM_ROUTE, request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(NON_POSITIVE_QUANTITY_ERROR_MESSAGE)));
            request.setQuantity(productItem.getQuantity() + 1);
            performPut(CART_UPDATE_ITEM_ROUTE, request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(MAX_QUANTITY_EXCEEDED_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateNonExistentCartItem() throws Exception {
            performPut(CART_REMOVE_ITEMS_ROUTE, Collections.singletonList(productItem.getId()));
            performPut(CART_UPDATE_ITEM_ROUTE, new UpdateCartItemRequest(productItem.getId(), 10));
            updateCart();
            Assertions.assertFalse(isCartItemExists(productItem.getId()));
        }

        @Test
        void testUpdateCartItemWithValidBody() throws Exception {
            UpdateCartItemRequest request = new UpdateCartItemRequest(productItem.getId(), 100);
            CartItem cartItem = performPut(CART_UPDATE_ITEM_ROUTE, CartItem.class, request);
            Assertions.assertEquals(request.getQuantity(), cartItem.getQuantity());
        }
    }

    @Test
    void testRemoveSingleCartItem() throws Exception {
        cart = performPut(CART_REMOVE_ITEMS_ROUTE, Cart.class, Collections.singletonList(productItem.getId()));
        Assertions.assertFalse(isCartItemExists(productItem.getId()));
    }

    @Test
    void testRemoveMultipleCartItems() throws Exception {
        Product product1 = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
        ProductItem productItem1 = product1.getItemMap().get("0");
        Product product2 = performPost(PRODUCT_CREATE_PRODUCT_ROUTE, Product.class, createProductRequestSample());
        ProductItem productItem2 = product2.getItemMap().get("0");
        performPost(CART_ADD_ITEM_ROUTE, new UpdateCartItemRequest(productItem.getId(), 10));
        performPost(CART_ADD_ITEM_ROUTE, new UpdateCartItemRequest(productItem1.getId(), 20));
        performPost(CART_ADD_ITEM_ROUTE, new UpdateCartItemRequest(productItem2.getId(), 30));
        updateCart();
        Assertions.assertEquals(3, cart.getItems().size());
        cart = performPut(CART_REMOVE_ITEMS_ROUTE, Cart.class, Collections.singletonList(productItem1.getId()));
        Assertions.assertEquals(2, cart.getItems().size());
        Assertions.assertFalse(isCartItemExists(productItem1.getId()));
        cart = performPut(CART_REMOVE_ITEMS_ROUTE, Cart.class, Arrays.asList(productItem.getId(), productItem2.getId()));
        Assertions.assertTrue(CollectionUtils.isEmpty(cart.getItems()));
    }

    private UpdateCartItemRequest createUpdateCartItemRequest(String productItemId, int quantity) {
        return UpdateCartItemRequest
                .builder()
                .productItemId(productItemId)
                .quantity(quantity)
                .build();
    }

    private void updateCart() throws Exception {
        this.cart = performGet(CART_GET_CURRENT_CART_ROUTE, Cart.class);
    }

    private boolean isCartItemExists(String productItemId) throws Exception {
        updateCart();
        long count = cart.getItems().stream().filter(item -> item.getProductItem().getId().equals(productItemId)).count();
        return count != 0;
    }

    private int findCartItemIndex(String productItemId) throws Exception {
        updateCart();
        int index = -1;
        for (int i = 0; i < cart.getItems().size(); i++) {
            if (cart.getItems().get(i).getProductItem().getId().equals(productItemId)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
