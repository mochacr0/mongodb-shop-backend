package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.ReturnMapper;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.OrderReturnEntity;
import com.example.springbootmongodb.model.OrderStatus;
import com.example.springbootmongodb.repository.ReturnRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.springbootmongodb.common.validator.ConstraintValidator.validateFields;
import static com.example.springbootmongodb.config.ReturnPolicies.MAX_DAYS_WAITING_TO_ACCEPT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnServiceImpl extends DataBaseService<OrderReturnEntity> implements ReturnService {
    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;
    private final OrderService orderService;

    @Override
    public MongoRepository<OrderReturnEntity, String> getRepository() {
        return returnRepository;
    }
    @Override
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
        OrderReturnEntity orderReturn = returnMapper.toEntity(request);
        validateExistingReturn(order, orderReturn.getOffer());
        if (request.getRefundAmount() > order.getPayment().getAmount()) {
            throw new InvalidDataException("Refund amount can not be higher than ordered amount");
        }
        orderReturn.setOrder(order);
        orderReturn.setItems(processReturnItemRequests(request.getItems(), order));
        LocalDateTime now = LocalDateTime.now();
        orderReturn.setExpiredAt(now.plusDays(MAX_DAYS_WAITING_TO_ACCEPT));
        ReturnStatus returnRequestedStatus = ReturnStatus
                .builder()
                .state(ReturnState.REQUESTED)
                .createdAt(now)
                .build();
        orderReturn.getStatusHistory().add(returnRequestedStatus);
        orderReturn = super.insert(orderReturn);
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

    private void validateExistingReturn(OrderEntity order, ReturnOffer offer) {
        if (offer == ReturnOffer.REFUND && StringUtils.isNotEmpty(order.getOrderRefund().getId())) {
            throw new InvalidDataException("You have already requested a refund for this order");

        }
        if (offer == ReturnOffer.RETURN_REFUND && StringUtils.isNotEmpty(order.getOrderReturn().getId())) {
            throw new InvalidDataException("You have already requested a return/refund for this order");
        }
    }

    private List<ReturnItem> processReturnItemRequests(List<ReturnItemRequest> returnItemRequests, OrderEntity order) {
        List<ReturnItem> returnItems = new ArrayList<>();
        Map<String, OrderItem> orderItemMap = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItemMap.put(orderItem.getProductItemId(), orderItem);
        }
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
            returnItems.add(ReturnItem
                    .builder()
                            .productItemId(orderItem.getProductItemId())
                            .productName(orderItem.getProductName())
                            .imageUrl(orderItem.getImageUrl())
                            .weight(orderItem.getWeight())
                            .price(orderItem.getPrice())
                            .quantity(returnItemRequest.getQuantity())
                    .build());
            orderItem.setRefundRequested(true);
        }
        return returnItems;
    }
}
