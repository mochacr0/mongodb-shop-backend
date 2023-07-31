package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderItemRequest;
import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends DataBaseService<OrderEntity> implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final UserAddressService userAddressService;
    private final ProductItemService productItemService;
    private final UserService userService;
    @Override
    public MongoRepository<OrderEntity, String> getRepository() {
        return this.orderRepository;
    }

    @Override
    public OrderEntity create(OrderRequest request) {
        log.info("Performing OrderService create");
        //validate user
        UserEntity user;
        try {
            user = userService.findCurrentUser();
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        //validate shipping address
        UserAddressEntity userAddress;
        try {
            userAddress = userAddressService.findById(request.getUserAddressId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        //validate items
        if (CollectionUtils.isEmpty(request.getOrderItems())) {
            throw new InvalidDataException("There is no items to place order");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        long subTotal = 0;
        for (OrderItemRequest itemRequest : request.getOrderItems()) {
            if (itemRequest.getQuantity() <= 0) {
                throw new InvalidDataException("Order item quantity should be positive");
            }
            ProductItemEntity productItem;
            try {
                productItem = productItemService.findById(itemRequest.getProductItemId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException(exception.getMessage());
            }
            OrderItem orderItem = OrderItem
                    .builder()
                    .productItemId(productItem.getId())
                    .productName(productItem.getProduct().getName())
                    .price(productItem.getPrice())
                    .variationDescription(productItem.getVariationDescription())
                    .quantity(itemRequest.getQuantity())
                    .build();
            orderItems.add(orderItem);
            subTotal += orderItem.getQuantity() * orderItem.getPrice();
        }
        Payment payment = paymentService.create(request.getPaymentMethod(), subTotal);
        OrderStatus iniStatus = OrderStatus
                .builder()
                .state(OrderState.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();
        OrderEntity order = OrderEntity
                .builder()
                .user(user)
                .userAddress(userAddress)
                .subTotal(subTotal)
                .payment(payment)
                .orderItems(orderItems)
                .statusHistory(Collections.singletonList(iniStatus))
                .build();
        return super.insert(order);
    }

    @Override
    public OrderEntity findById(String id) {
        log.info("Performing OrderService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException("Order Id should be specified");
        }
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Order with id [%s] is not found", id)));
    }

    @Override
    public OrderEntity save(OrderEntity order) {
        log.info("Performing OrderService save");
        return super.save(order);
    }
}
