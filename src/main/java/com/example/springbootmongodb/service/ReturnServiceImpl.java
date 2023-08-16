package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ReturnMapper;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKPickOption;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKWorkShiftOption;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.ReturnRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.springbootmongodb.common.validator.ConstraintValidator.validateFields;
import static com.example.springbootmongodb.config.ReturnPolicies.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnServiceImpl extends DataBaseService<OrderReturnEntity> implements ReturnService {
    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;
    private final OrderService orderService;
    private final ShipmentService shipmentService;

    @Override
    public MongoRepository<OrderReturnEntity, String> getRepository() {
        return returnRepository;
    }

    @Override
    @Transactional
    public OrderReturnEntity create(ReturnRequest request) {
        log.info("Performing ReturnService create");
        validateFields(request);
        if (CollectionUtils.isEmpty(request.getItems())) {
            throw new InvalidDataException("Choose one or more items to return/refund");
        }
        OrderEntity order;
        try {
            order = orderService.findById(request.getOrderId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateOrderState(order, OrderState.TO_CONFIRM_RECEIVE);
        if (order.getPayment().getMethod() == PaymentMethod.CASH) {
                throw new InvalidDataException("Refund/return requests are currently supported only for orders paid online");
        }
        OrderReturnEntity orderReturn = returnMapper.toEntity(request);
        validateExistingReturn(order, orderReturn.getOffer());
        orderReturn.setOrder(order);
        ProcessReturnItemsResult processReturnItemsResult = processReturnItemRequests(request.getItems(), order);
        orderReturn.setItems((processReturnItemsResult.getReturnItems()));
        orderReturn.setRefundAmount(processReturnItemsResult.getRefundAmount());
        LocalDateTime now = LocalDateTime.now();
        orderReturn.setExpiredAt(now.plusDays(MAX_DAYS_WAITING_TO_ACCEPT));
        ReturnStatus returnRequestedStatus = ReturnStatus
                .builder()
                .state(ReturnState.REQUESTED)
                .createdAt(now)
                .build();
        orderReturn.getStatusHistory().add(returnRequestedStatus);
        orderReturn.setCurrentStatus(returnRequestedStatus);
        orderReturn = super.insert(orderReturn);
        ShipmentEntity returnShipment = shipmentService.initiate(orderReturn.getId(), order.getShipment().getDeliverAddress(), order.getShipment().getPickUpAddress());
        orderReturn.setShipment(returnShipment);
        orderReturn = save(orderReturn);
        order.setOrderReturn(orderReturn);
        OrderStatus orderReturnStatus = OrderStatus
                .builder()
                .state(OrderState.TO_RETURN)
                .createdAt(now)
                .build();
        order.getStatusHistory().add(orderReturnStatus);
        order.setCompletedAt(null);
        orderService.save(order);
        return orderReturn;
    }

    @Override
    public void acceptExpiredReturnRequests() {
        log.info("Performing ReturnService acceptExpiredReturnRequests");
        returnRepository.acceptExpiredReturnRequests();
    }

    @Override
    public OrderReturnEntity confirmJudging(String returnId) {
        log.info("Performing ReturnService confirmProcessing");
        OrderReturnEntity orderReturn;
        try {
            orderReturn = findById(returnId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateReturnState(orderReturn, ReturnState.REQUESTED);
        ReturnStatus returnProcessingStatus = ReturnStatus
                .builder()
                .state(ReturnState.JUDGING)
                .createdAt(LocalDateTime.now())
                .build();
        orderReturn.setCurrentStatus(returnProcessingStatus);
        orderReturn.getStatusHistory().add(returnProcessingStatus);
        orderReturn.setExpiredAt(null);
        return save(orderReturn);
    }

    @Override
    public OrderReturnEntity findById(String returnId) {
        log.info("Performing ReturnService findById");
        if (StringUtils.isEmpty(returnId)) {
            throw new InvalidDataException("Return Id should be specified");
        }
        return returnRepository.findById(returnId).orElseThrow(() -> new ItemNotFoundException(String.format("Return with id [%s] is not found", returnId)));
    }

    @Override
    public OrderReturnEntity accept(String returnId) {
        log.info("Performing ReturnService accept");
        OrderReturnEntity orderReturn;
        try {
            orderReturn = findById(returnId);
        } catch(ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateReturnState(orderReturn, ReturnState.JUDGING);
        LocalDateTime now = LocalDateTime.now();
        if (orderReturn.getOffer() == ReturnOffer.REFUND) {
            PaymentMethod paymentMethod = orderReturn.getOrder().getPayment().getMethod();
            //case payment in cash
            //TODO: handle in-cash payment refund
            if (paymentMethod == PaymentMethod.CASH) {
                ReturnStatus refundProcessingStatus = ReturnStatus
                        .builder()
                        .state(ReturnState.REFUND_PROCESSING)
                        .createdAt(now).build();
                orderReturn.getStatusHistory().add(refundProcessingStatus);
                orderReturn = save(orderReturn);
                //TODO: send notify email

                return orderReturn;
            }

            //case payment with momo
            else if (paymentMethod == PaymentMethod.MOMO) {
                orderService.refundInReturn(orderReturn.getOrder().getId(), orderReturn.getRefundAmount());
                ReturnStatus returnRefundedStatus = ReturnStatus
                        .builder()
                        .state(ReturnState.AWAITING_USER_CONFIRMATION)
                        .createdAt(now)
                        .build();
                orderReturn.getStatusHistory().add(returnRefundedStatus);
                orderReturn.setExpiredAt(now.plusDays(MAX_DAYS_WAITING_FOR_USER_CONFIRMATION));
                orderReturn = save(orderReturn);
                //TODO: send notification email

                return orderReturn;
            }
        }
        else if (orderReturn.getOffer() == ReturnOffer.RETURN_REFUND) {
            //TODO: handle retunrn and refund
            ReturnStatus returnUserPreparingStatus = ReturnStatus
                    .builder()
                    .state(ReturnState.USER_PREPARING)
                    .createdAt(now)
                    .build();
            orderReturn.getStatusHistory().add(returnUserPreparingStatus);
            orderReturn.setExpiredAt(now.plusDays(MAX_DAYS_USER_PREPARING));
            orderReturn = save(orderReturn);
            //TODO: send notification email

        }
        return save(orderReturn);
    }

    @Override
    public OrderReturnEntity placeShipmentOrder(String returnId, ShipmentRequest shipmentRequest) {
        log.info("Performing ReturnService placeShipmentOrder");
        OrderReturnEntity orderReturn = findById(returnId);
        validateReturnState(orderReturn, ReturnState.USER_PREPARING);
        ShipmentEntity placedShipment = shipmentService.place(orderReturn.getShipment(), orderReturn.getId(), (int)orderReturn.getRefundAmount(), orderReturn.getItems(), shipmentRequest);
        orderReturn.setShipment(placedShipment);
        ReturnStatus readyToShipStatus = ReturnStatus
                .builder()
                .state(ReturnState.READY_TO_SHIP)
                .createdAt(LocalDateTime.now())
                .build();
        orderReturn.getStatusHistory().add(readyToShipStatus);
        if (shipmentRequest.getPickOption().equals(GHTKPickOption.POST.getValue())) {
            String[] splitTime = placedShipment.getEstimatedPickTime().split(" ");
            String dayPart = splitTime[0];
            GHTKWorkShiftOption pickWorkShiftOption = GHTKWorkShiftOption.parseFromDayPart(dayPart);
            String estimatedPickTimeString = splitTime[1];
            LocalDateTime expiredAt = LocalDateTime.parse(String.format("%sT%s", estimatedPickTimeString, pickWorkShiftOption.getEndTime()));
            orderReturn.setExpiredAt(expiredAt);
        }
        return save(orderReturn);
    }

    private void validateExistingReturn(OrderEntity order, ReturnOffer offer) {
        if (offer == ReturnOffer.REFUND && order.getOrderRefund() != null) {
            throw new InvalidDataException("You have already requested a refund for this order");

        }
        if (offer == ReturnOffer.RETURN_REFUND && order.getOrderReturn() != null) {
            throw new InvalidDataException("You have already requested a return/refund for this order");
        }
    }

    private ProcessReturnItemsResult processReturnItemRequests(List<ReturnItemRequest> returnItemRequests, OrderEntity order) {
        List<ReturnItem> returnItems = new ArrayList<>();
        Map<String, OrderItem> orderItemMap = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItemMap.put(orderItem.getProductItemId(), orderItem);
        }
        long totalRefundAmount = 0;
        for (ReturnItemRequest returnItemRequest : returnItemRequests) {
            OrderItem orderItem = orderItemMap.get(returnItemRequest.getProductItemId());
            if (orderItem == null) {
                throw new UnprocessableContentException(String.format("Product item with id [%s] doesn't exists in this order", returnItemRequest.getProductItemId()));
            }
            if (orderItem.isRefundRequested()) {
                throw new InvalidDataException(String.format("Product item with id [%s] is already returned/refunded", orderItem.getProductItemId()));
            }
            if (returnItemRequest.getQuantity() <= 0) {
                throw new InvalidDataException("Return quantity should be positive");
            }
            if (returnItemRequest.getQuantity() > orderItem.getQuantity()) {
                throw new InvalidDataException("Return quantity should be equal or less than ordered quantity");
            }
            ReturnItem returnItem = ReturnItem
                    .builder()
                    .productItemId(orderItem.getProductItemId())
                    .productName(orderItem.getProductName())
                    .imageUrl(orderItem.getImageUrl())
                    .weight(orderItem.getWeight())
                    .price(orderItem.getPrice())
                    .quantity(returnItemRequest.getQuantity())
                    .build();
            returnItems.add(returnItem);
            totalRefundAmount += returnItem.getQuantity() * returnItem.getPrice();
            orderItem.setRefundRequested(true);
        }
        //return delivery fee if user return all items
        if (totalRefundAmount == order.getSubTotal()) {
            totalRefundAmount = order.getTotal();
        }

        return ProcessReturnItemsResult.builder().refundAmount(totalRefundAmount).returnItems(returnItems).build();
    }

    private void validateReturnState(OrderReturnEntity orderReturn, ReturnState... expectedStates) {
        ReturnState actualState = orderReturn.getCurrentStatus().getState();
        if (Arrays.stream(expectedStates).noneMatch(expectedState -> expectedState == actualState)) {
            throw new InvalidDataException(String.format("Order is %s",
                    actualState.getMessage().toLowerCase()));
        }
    }
}
