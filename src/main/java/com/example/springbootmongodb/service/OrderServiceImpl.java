package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ProductItemMapper;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.common.data.mapper.UserAddressMapper;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.PaymentStatus;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKPickOption;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKWorkShiftOption;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.OrderRepository;
import com.example.springbootmongodb.security.Authority;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.springbootmongodb.config.OrderPolicies.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends DataBaseService<OrderEntity> implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final UserAddressService userAddressService;
    private final ProductItemService productItemService;
    private final UserService userService;
    private final ShopAddressService shopAddressService;
    private final ShopAddressMapper shopAddressMapper;
    private final UserAddressMapper userAddressMapper;
    private final ShipmentService shipmentService;
    private final CartService cartService;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final ProductItemMapper productItemMapper;

    @Override
    public MongoRepository<OrderEntity, String> getRepository() {
        return this.orderRepository;
    }

    @Override
    @Transactional
    public OrderEntity create(OrderRequest request) {
        log.info("Performing OrderService create");

        ///validate user authority
        UserEntity user;
        try {
            user = userService.findCurrentUser();
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        if (request.getShopAddress() == null) {
            throw new InvalidDataException("Shop address should be specified");
        }

        //validate shop address
        ShopAddress existingShopAddress;
        try {
            existingShopAddress = shopAddressMapper.fromEntity(shopAddressService.findById(request.getShopAddress().getId()));
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        if (!existingShopAddress.equals(request.getShopAddress())) {
            throw new InvalidDataException("Our shop address has updated, affecting the delivery fee. " +
                    "Please review your order for accurate costs");
        }

        //validate user address
        UserAddress existingUserAddress;
        try {
            existingUserAddress = userAddressMapper.fromEntity(userAddressService.findById(request.getUserAddress().getId()));
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        if (!existingUserAddress.equals(request.getUserAddress())) {
            throw new InvalidDataException("Your delivery address has been updated. Please verify your new address before proceeding with your order.");
        }

        //validate items
        if (CollectionUtils.isEmpty(request.getOrderItems())) {
            throw new InvalidDataException("There is no items to place order");
        }
        long subTotal = 0;
        List<String> productItemIds = new ArrayList<>();
        List<OrderItem> orderItems = processOrderItemRequests(request.getOrderItems());
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (OrderItemRequest itemRequest : request.getOrderItems()) {
//            if (itemRequest.getQuantity() <= 0) {
//                throw new InvalidDataException("Order item quantity should be positive");
//            }
//            ProductItemEntity productItem;
//            try {
//                productItem = productItemService.findById(itemRequest.getProductItemId());
//            } catch (ItemNotFoundException exception) {
//                throw new UnprocessableContentException(exception.getMessage());
//            }
//            if (productItem.getQuantity() < itemRequest.getQuantity()) {
//                throw new InvalidDataException(String.format("Product item with id [%s] is out of stock", productItem.getId()));
//            }
//            productItemIds.add(itemRequest.getProductItemId());
//            OrderItem orderItem = OrderItem
//                    .builder()
//                    .productItemId(productItem.getId())
//                    .productName(productItem.getProduct().getName())
//                    .price(productItem.getPrice())
//                    .weight(productItem.getWeight())
//                    .variationDescription(productItem.getVariationDescription())
//                    .quantity(itemRequest.getQuantity())
//                    .build();
//            orderItems.add(orderItem);
//            subTotal += orderItem.getQuantity() * orderItem.getPrice();
//        }
        for (OrderItem orderItem : orderItems) {
            productItemIds.add(orderItem.getProductItemId());
            subTotal += orderItem.getQuantity() * orderItem.getPrice();
        }
        OrderEntity order = OrderEntity
                .builder()
                .user(user)
//                .userAddress(existingUserAddress)
                .subTotal(subTotal)
                .orderItems(orderItems)
                .build();
        LocalDateTime expiredAt = null;
        LocalDateTime now = LocalDateTime.now();
        OrderStatus iniStatus = OrderStatus.builder().createdAt(now).build();
        Payment payment = paymentService.create(request.getPaymentMethod(), subTotal);
        if (payment.getMethod() == PaymentMethod.MOMO) {
            iniStatus.setState(OrderState.UNPAID);
            expiredAt = now.plusMinutes(MAX_MINUTES_WAITING_TO_INITIATE_PAYMENT);
        }
        else if (payment.getMethod() == PaymentMethod.CASH){
            iniStatus.setState(OrderState.WAITING_TO_ACCEPT);
            expiredAt = now.plusDays(MAX_DAYS_WAITING_TO_PREPARING);
        }
        Shipment shipment = Shipment
                .builder()
                .userAddress(existingUserAddress)
                .shopAddress(existingShopAddress)
                .build();
        order.setStatusHistory(Collections.singletonList(iniStatus));
        order.setPayment(payment);
        order.setExpiredAt(expiredAt);
        order.setShipment(shipment);
        order = super.insert(order);
        cartService.bulkRemoveItems(productItemIds);
        return order;
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
        SecurityUser user = getCurrentUser();
        OrderEntity order = findById(id);
        validateOrderState(order, OrderState.UNPAID, OrderState.WAITING_TO_ACCEPT, OrderState.PREPARING, OrderState.READY_TO_SHIP, OrderState.IN_CANCEL);
        if (user.getAuthority().equals(Authority.USER)) {
            return processUserCancelRequest(order);
        }
        else if (user.getAuthority().equals(Authority.ADMIN)) {
            return processAdminCancelRequest(order);
        }
        else {
            throw new AuthenticationServiceException("You aren't authorized to perform this operation!");
        }
    }

    private OrderEntity processUserCancelRequest(OrderEntity order) {
        OrderState currentState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        OrderStatus canceledStatus = OrderStatus
                .builder()
                .state(OrderState.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();
        switch (currentState) {
            case UNPAID -> {
                orderRepository.rollbackOrderItemQuantities(order.getOrderItems());
                order.getStatusHistory().add(canceledStatus);
                order.setExpiredAt(null);
            }
            case WAITING_TO_ACCEPT -> {
                orderRepository.rollbackOrderItemQuantities(order.getOrderItems());
                order.getStatusHistory().add(canceledStatus);
                if (order.getPayment().getMethod() == PaymentMethod.MOMO) {
                    Payment refundedPayment = paymentService.refund(order.getPayment());
                    order.setPayment(refundedPayment);
                }
                order.setExpiredAt(null);
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
        return save(order);
     }

    private OrderEntity processAdminCancelRequest(OrderEntity order) {
        OrderState currentOrderState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        Shipment orderShipment = order.getShipment();
        ShipmentState currentShipmentState = orderShipment.getStatusHistory().get(orderShipment.getStatusHistory().size() - 1).getState();
        switch (currentOrderState) {
            case UNPAID -> {}
            case WAITING_TO_ACCEPT, PREPARING -> {
                refundInCancel(order);
            }
            case READY_TO_SHIP -> {
                refundInCancel(order);
                //cancel shipment
                //FAILED_TO_PICK is already one of the ending states of shipment so don't need to cancel
                if (currentShipmentState != ShipmentState.FAILED_TO_PICKUP) {
                    Shipment canceledShipment = shipmentService.cancel(order.getId(), order.getShipment());
                    order.setShipment(canceledShipment);
                }
            }
            case IN_CANCEL -> {
                refundInCancel(order);
                //case IN_CANCEL and shipment order has been placed
                if (StringUtils.isNotEmpty(order.getShipment().getId())) {
                    Shipment canceledShipment = shipmentService.cancel(order.getId(), order.getShipment());
                    order.setShipment(canceledShipment);
                }
            }
            case FAILED_TO_DELIVER -> {
                if (currentShipmentState != ShipmentState.RETURNED) {
                    throw new InvalidDataException("Order hasn't been returned yet");
                }
            }
            default -> throw new InvalidDataException(String.format("Cannot perform this action. Order is %s", currentOrderState.getMessage()));
        }
        orderRepository.rollbackOrderItemQuantities(order.getOrderItems());
        OrderStatus canceledStatus = OrderStatus
                .builder()
                .state(OrderState.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(canceledStatus);
        order.setExpiredAt(null);
        return save(order);
    }

    private void refundInCancel(OrderEntity order) {
        if (order.getPayment().getMethod() == PaymentMethod.MOMO) {
            Payment refundedPayment = paymentService.refund(order.getPayment());
            order.setPayment(refundedPayment);
            order.setExpiredAt(null);
        }
    }

    @Override
    public void cancelExpiredOrders() {
        log.info("Performing OrderService cancelExpiredOrders");
        orderRepository.cancelExpiredOrders();
    }

    @Override
    public String initiatePayment(String id, HttpServletRequest httpServletRequest) {
        log.info("Performing OrderService initiatePayment");
        OrderEntity order = findById(id);
        validateOrderState(order, OrderState.UNPAID);
        Payment initiatedPayment = paymentService.initiatePayment(order, order.getPayment(), httpServletRequest);
        order.setPayment(initiatedPayment);
        order.setExpiredAt(LocalDateTime.now().plusMinutes(ORDER_MOMO_TRANSACTION_EXPIRY_TIME_IN_MINUTE)); //Momo default transaction timeout: 1 hour and 40 minutes
        save(order);
        return initiatedPayment.getPayUrl();
    }

    @Override
    public OrderEntity accept(String id) {
        log.info("Performing OrderService accept");
        OrderEntity order = findById(id);
        validateOrderState(order, OrderState.WAITING_TO_ACCEPT);
        Payment orderPayment = order.getPayment();
        if (orderPayment.getMethod() == PaymentMethod.MOMO) {
            validatePaymentStatus(orderPayment.getStatus(), PaymentStatus.PAID);
        }
        LocalDateTime now = LocalDateTime.now();
        OrderStatus preparingStatus = OrderStatus
                .builder()
                .state(OrderState.PREPARING)
                .createdAt(now)
                .build();
        order.getStatusHistory().add(preparingStatus);
        order.setExpiredAt(now.plusDays(MAX_DAYS_PREPARING_TO_READY));
        return save(order);
    }

    @Override
    public OrderEntity placeShipmentOrder(String id, ShipmentRequest shipmentRequest) {
        log.info("Performing OrderService placeShipmentOrder");
        OrderEntity order = findById(id);
        validateOrderState(order, OrderState.PREPARING);
        Shipment placedShipment = shipmentService.place(order, shipmentRequest);
        order.setShipment(placedShipment);
        OrderStatus readyToShipStatus = OrderStatus
                .builder()
                .state(OrderState.READY_TO_SHIP)
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(readyToShipStatus);
        if (shipmentRequest.getPickOption().equals(GHTKPickOption.POST.getValue())) {
            String[] splitTime = placedShipment.getEstimatedPickTime().split(" ");
            String dayPart = splitTime[0];
            GHTKWorkShiftOption pickWorkShiftOption = GHTKWorkShiftOption.parseFromDayPart(dayPart);
            String estimatedPickTimeString = splitTime[1];
            LocalDateTime expiredAt = LocalDateTime.parse(String.format("%sT%s", estimatedPickTimeString, pickWorkShiftOption.getEndTime()));
            order.setExpiredAt(expiredAt);
        }
        return save(order);
    }

    @Override
    @Transactional
    public OrderEntity processShipmentStatusUpdateRequest(GHTKUpdateStatusRequest request) {
        log.info("Performing OrderService processShipmentStatusUpdateRequest");
        OrderEntity order;
        try {
            order = findById(request.getPartnerId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateUnexpectedOrderStates(order, OrderState.WAITING_TO_ACCEPT, OrderState.PREPARING, OrderState.CANCELED, OrderState.COMPLETED);
        Shipment orderShipment = order.getShipment();
        ShipmentState updateState = ShipmentState.parseFromInt(request.getStatusId());
        ShipmentStatus updatedStatus = ShipmentStatus
                .builder()
                .state(updateState)
                .description(request.getReason())
                .build();
        orderShipment.getStatusHistory().add(updatedStatus);
        order.setShipment(orderShipment);
        LocalDateTime now =  LocalDateTime.now();
        switch (updateState) {
            case ACCEPTED -> order.setExpiredAt(null);
            case PICKING_UP -> {}
            case PICKUP_DELAYED -> {
                //update expiredAt to new pick up date time
            }
            case FAILED_TO_PICKUP, RETURNED -> {
                //cancel order
                save(order);
                return cancel(order.getId());
            }
            case PICKED_UP -> {
                OrderStatus orderPickedUpStatus = OrderStatus
                        .builder()
                        .state(OrderState.PICKED_UP)
                        .createdAt(now)
                        .description("Order has been handed over to the shipping carrier")
                        .build();
                order.getStatusHistory().add(orderPickedUpStatus);
                order.setExpiredAt(null);
            }
            case DELIVERING -> {
                OrderStatus orderDeliveringStatus = OrderStatus
                        .builder()
                        .state(OrderState.DELIVERING)
                        .createdAt(now)
                        .description("Delivering")
                        .build();
                order.getStatusHistory().add(orderDeliveringStatus);
            }
            case DELIVERY_DELAYED -> {}
            case FAILED_TO_DELIVER -> {
                OrderStatus orderFailedToDeliverStatus = OrderStatus
                        .builder()
                        .state(OrderState.FAILED_TO_DELIVER)
                        .createdAt(now)
                        .description("Failed to deliver")
                        .build();
                order.getStatusHistory().add(orderFailedToDeliverStatus);
            }
            case DELIVERED -> {
                OrderStatus orderWaitingToConfirmState = OrderStatus
                        .builder()
                        .state(OrderState.TO_CONFIRM_RECEIVE)
                        .createdAt(now)
                        .description("Waiting for user confirmation")
                        .build();
                order.getStatusHistory().add(orderWaitingToConfirmState);
                order.setCompletedAt(now.plusDays(MAX_DAYS_FOR_RETURN_REFUND));
            }
            case RETURNING -> {}
            default -> throw new InvalidDataException("Unhandled states");
        }
        return save(order);
    }

    @Override
    public void markCompletedOrders() {
        log.info("Performing OrderService markCompletedOrders");

    }

    @Override
    public OrderEntity confirmDelivered(String id) {
        log.info("Performing UserService confirm");
        OrderEntity order;
        try {
            order = findById(id);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateOrderState(order, OrderState.TO_CONFIRM_RECEIVE);
        OrderStatus completedStatus = OrderStatus
                .builder()
                .state(OrderState.COMPLETED)
                .description("Order received")
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(completedStatus);
        return save(order);
    }


    private List<OrderItem> processOrderItemRequests(List<OrderItemRequest> orderItemRequests) {
        List<CompletableFuture<OrderItem>> processFutures = orderItemRequests.stream().map(orderItemRequest -> {
            return CompletableFuture.supplyAsync(() -> {
                if (orderItemRequest.getQuantity() <= 0) {
                    throw new InvalidDataException("Order item quantity should be positive");
                }
                ProductItemEntity productItem;
                try {
                    productItem = productItemService.findById(orderItemRequest.getProductItemId());
                } catch (ItemNotFoundException exception) {
                    throw new UnprocessableContentException(exception.getMessage());
                }
                if (productItem.getQuantity() < orderItemRequest.getQuantity()) {
                    throw new InvalidDataException(String.format("Product item with id [%s] is out of stock", productItem.getId()));
                }
                productItem.setQuantity(productItem.getQuantity() - orderItemRequest.getQuantity());
                productItemService.save(productItem);
                return OrderItem
                        .builder()
                        .productItemId(productItem.getId())
                        .productName(String.format("%s %s", productItem.getProduct().getName(), productItem.getVariationDescription()))
                        .imageUrl(productItem.getImageUrl())
                        .price(productItem.getPrice())
                        .weight(productItem.getWeight())
                        .variationDescription(productItem.getVariationDescription())
                        .quantity(orderItemRequest.getQuantity())
                        .build();
            }, taskExecutor);
        }).collect(Collectors.toList());
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(processFutures.toArray(new CompletableFuture[processFutures.size()]));
        combinedFuture.join();
        return processFutures.stream().map(CompletableFuture::join).toList();
    }
}
