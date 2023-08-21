package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.data.mapper.OrderMapper;
import com.example.springbootmongodb.common.data.mapper.ReviewMapper;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.common.data.mapper.UserAddressMapper;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKPickOption;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKWorkShiftOption;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.OrderStatus;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    //TEST
    private final UserAddressService addressService;
    private final UserService userService;
    private final UserAddressMapper addressMapper;
    private final ShopAddressService shopAddressService;
    private final ShopAddressMapper shopAddressMapper;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping(value = REVIEW_GET_REVIEW_BY_ID_ROUTE)
    @Operation(summary = "Truy xuất review theo id")
    Review findById(@PathVariable String reviewId) {
        return reviewMapper.fromEntity(reviewService.findById(reviewId));
    }

    @PostMapping(value = REVIEW_POST_REVIEW_ROUTE)
    @Operation(summary = "Đánh giá sản phẩm")
    Review post(@RequestBody ReviewRequest request) {
        return reviewMapper.fromEntity(reviewService.create(request));
    }

    @PutMapping(value = REVIEW_UPDATE_REVIEW_ROUTE)
    @Operation(summary = "Chỉnh sửa đánh giá")
    Review edit(@PathVariable String reviewId,
            @RequestBody ReviewRequest request) {
        return reviewMapper.fromEntity(reviewService.edit(reviewId, request));
    }

    @PostMapping("/iniData")
    Order iniData(@RequestBody List<String> productItemIds) {
        UserEntity user = userService.findByName("user00");
        UserAddress userAddress = addressMapper.fromEntity(addressService.findById(user.getDefaultAddressId()));
        ShopAddress shopAddress = shopAddressMapper.fromEntity(shopAddressService.findDefaultAddress());
        List<OrderItemRequest> orderItemRequests = productItemIds.stream().map(itemId -> OrderItemRequest.builder().productItemId(itemId).quantity(1).build()).toList();
        OrderRequest orderRequest = OrderRequest
                .builder()
                .userAddress(userAddress)
                .shopAddress(shopAddress)
                .deliveryFee(45000)
                .paymentMethod(PaymentMethod.CASH.name().toLowerCase())
                .orderItems(orderItemRequests)
                .build();
        OrderEntity order = orderService.create(orderRequest);
        order = orderService.accept(order.getId());
        ShipmentRequest shipmentRequest = ShipmentRequest
                .builder()
                .pickOption(GHTKPickOption.POST.getValue())
                .pickWorkShipOption(GHTKWorkShiftOption.AFTERNOON.name().toLowerCase())
                .deliverWorkShipOption(GHTKWorkShiftOption.AFTERNOON.name().toLowerCase())
                .build();
        orderService.placeShipmentOrder(order.getId(), shipmentRequest);

        //place shipment manual due to unavailable ghtk service
//        OrderStatus readyToShipStatus = OrderStatus
//                .builder()
//                .state(OrderState.READY_TO_SHIP)
//                .createdAt(LocalDateTime.now())
//                .build();
//        order.getStatusHistory().add(readyToShipStatus);
//        order.setCurrentStatus(readyToShipStatus);
//        orderService.save(order);

        GHTKUpdateStatusRequest request = GHTKUpdateStatusRequest
                .builder()
                .shipmentId(order.getShipment().getId())
                .partnerId(order.getId())
                .weight(0.2f)
                .fee(45000)
                .returnPartPackage(0)
                .statusId(Integer.valueOf(ShipmentState.ACCEPTED.getCode()))
                .build();
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKING_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKED_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERING.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERED.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        return orderMapper.fromEntity(orderService.confirmDelivered(order.getId()));
    }
}
