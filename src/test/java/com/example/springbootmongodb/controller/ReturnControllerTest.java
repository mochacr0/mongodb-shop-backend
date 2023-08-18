package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReturnControllerTest extends AbstractControllerTest {
//    private User user;
//    private Order order;
//    private OrderReturn orderReturn;
//    private UserAddress userAddress;
//
//    @BeforeAll
//    void setUp() throws Exception {
//        userLogin();
//        user = performGet(USERS_GET_CURRENT_USER_ROUTE, User.class);
//        userAddress = performGet(USERS_GET_ADDRESS_BY_ID_ROUTE, UserAddress.class, user.getDefaultAddressId());
//    }
//
//    private void createOrder(String paymentMethod) throws Exception {
//        GHTKCalculateFeeResponse calculateFeeResponse = performGet(SHIPMENT_CALCULATE_DELIVERY_FEE_ROUTE, GHTKCalculateFeeResponse.class);
//        OrderRequest orderRequest = OrderRequest
//                .builder()
//                .orderItems(createOrderItemRequests())
//                .deliveryFee(calculateFeeResponse.getFee().getFee())
//                .shopAddress(calculateFeeResponse.getShopAddress())
//                .userAddress(userAddress)
//                .paymentMethod(paymentMethod)
//                .build();
//        order = performPost(ORDER_CREATE_ORDER_ROUTE, Order.class,  orderRequest);
//    }
//
//    private void adminAcceptOrder() {
//
//    }
//
//    private List<OrderItemRequest> createOrderItemRequests() {
//        OrderItemRequest itemRequest1 = OrderItemRequest
//                .builder()
//                .productItemId("64dccad4b5a1344511c9f762")
//                .quantity(1)
//                .build();
//        OrderItemRequest itemRequest2 = OrderItemRequest
//                .builder()
//                .productItemId("64d5e1997602e726bcebc929")
//                .quantity(2)
//                .build();
//        OrderItemRequest itemRequest3 = OrderItemRequest
//                .builder()
//                .productItemId("64d5e1997602e726bcebc92a")
//                .quantity(3)
//                .build();
//        return Arrays.asList(itemRequest1, itemRequest2, itemRequest3);
//    }
}
