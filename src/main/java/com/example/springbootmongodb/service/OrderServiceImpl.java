package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.OrderItemRequest;
import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.OrderRepository;
import com.example.springbootmongodb.security.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.springbootmongodb.config.OrderPolicies.MAX_DAYS_IN_CANCEL_TO_CANCELED;

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
        OrderEntity order = OrderEntity
                .builder()
                .user(user)
                .userAddress(userAddress)
                .subTotal(subTotal)
                .orderItems(orderItems)
                .build();
        OrderStatus iniStatus = OrderStatus.builder().createdAt(LocalDateTime.now()).build();
        Payment payment = paymentService.create(request.getPaymentMethod(), subTotal);
        if (payment.getMethod() == PaymentMethod.MOMO) {
            iniStatus.setState(OrderState.UNPAID);
        }
        else if (payment.getMethod() == PaymentMethod.CASH){
            iniStatus.setState(OrderState.WAITING_TO_ACCEPT);
        }
        order.setStatusHistory(Collections.singletonList(iniStatus));
        order.setPayment(payment);
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

    @Override
    @Transactional
    public OrderEntity cancel(String id) {
        log.info("Performing OrderService cancel");
        OrderEntity order = findById(id);
        SecurityUser user = getCurrentUser();
        if (user.getAuthority().equals(Authority.USER)) {
            processUserCancelRequest(order);
        }
        else if (user.getAuthority().equals(Authority.ADMIN)) {
            processAdminCancelRequest(order);
        }
        return save(order);
    }

    private void processUserCancelRequest(OrderEntity order) {
        OrderState currentState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        OrderStatus canceledStatus = OrderStatus
                .builder()
                .state(OrderState.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();
        switch (currentState) {
            case UNPAID -> order.getStatusHistory().add(canceledStatus);
            case WAITING_TO_ACCEPT -> {
                order.getStatusHistory().add(canceledStatus);
                if (order.getPayment().getMethod() == PaymentMethod.MOMO) {
                    paymentService.refund(order.getId());
                }
            }
            case PREPARING, READY_TO_SHIP -> {
                OrderStatus inCancelStatus = OrderStatus
                        .builder()
                        .state(OrderState.IN_CANCEL)
                        .createdAt(LocalDateTime.now())
                        .build();
                order.getStatusHistory().add(inCancelStatus);
                LocalDateTime expiredAt = LocalDateTime.now().plusDays(MAX_DAYS_IN_CANCEL_TO_CANCELED);
                order.setExpiredAt(expiredAt);
            }
            default -> throw new InvalidDataException(String.format("Cannot perform this action. Order is %s", currentState.getMessage()));
        }
     }

    private void processAdminCancelRequest(OrderEntity order) {
        OrderState currentState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        OrderStatus canceledStatus = OrderStatus
                .builder()
                .state(OrderState.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(canceledStatus);
        switch (currentState) {
            case UNPAID -> {}
            case WAITING_TO_ACCEPT, PREPARING -> {
                if (order.getPayment().getMethod() == PaymentMethod.MOMO) {
                    paymentService.refund(order.getId());
                }
            }
            case READY_TO_SHIP -> {
                //TODO: cancel shipment
            }
            default -> throw new InvalidDataException(String.format("Cannot perform this action. Order is %s", currentState.getMessage()));
        }
    }

    @Override
    public void cancelExpiredOrders() {
        log.info("Performing OrderService cancelExpiredOrders");
        orderRepository.cancelExpiredOrders();
    }
}
