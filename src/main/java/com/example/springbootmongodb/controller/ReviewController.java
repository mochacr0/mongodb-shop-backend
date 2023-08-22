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
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Truy xuất đánh giá theo id")
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

    @DeleteMapping(value = REVIEW_DELETE_REVIEW_ROUTE)
    @Operation(summary = "Ẩn đánh giá")
    void disable(@PathVariable String reviewId) {
        reviewService.disable(reviewId);
    }

    @GetMapping(value = REVIEW_GET_REVIEWS_ROUTE)
    @Operation(summary = "Phân trang đánh giá sản phẩm")
    PageData<Review> getReviews(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                                @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                                @Parameter(description = PAGE_SIZE_DESCRIPTION)
                                @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                                @Parameter(description = SORT_ORDER_DESCRIPTION,
                                        examples = {@ExampleObject(name = "asc (Ascending)", value = "asc"),
                                                @ExampleObject(name = "desc (Descending)", value = "desc")})
                                @RequestParam(required = false) String sortDirection,
                                @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                                @RequestParam(required = false) String sortProperty,
                                @Parameter(description = "Id của sản phẩm")
                                @RequestParam(required = false) String productId) {
        return reviewService.findReviews(ReviewPageParameter
                .builder()
                .page(page)
                .pageSize(pageSize)
                .sortDirection(sortDirection)
                .sortProperty(sortProperty)
                .productId(productId)
                .build()
        );
    }

    @PostMapping(value = REVIEW_RESPONSE_REVIEW_ROUTE)
    @Operation(summary = "Phản hồi đánh giá của khách hàng")
    Review response(@PathVariable String reviewId,
                    @RequestBody ShopResponseRequest request) {
        return reviewMapper.fromEntity(reviewService.createResponse(reviewId, request));
    }

    @PutMapping(value = REVIEW_EDIT_RESPONSE_ROUTE)
    @Operation(summary = "Chỉnh sửa phản hồi")
    Review editResponse(@PathVariable String reviewId,
                        @RequestBody ShopResponseRequest request) {
        return reviewMapper.fromEntity(reviewService.editResponse(reviewId, request));
    }

    @DeleteMapping(value = REVIEW_DELETE_RESPONSE_ROUTE)
    @Operation(summary = "Xóa phản hồi")
    Review deleteResponse(@PathVariable String reviewId) {
        return reviewMapper.fromEntity(reviewService.deleteResponse(reviewId));
    }

    @PostMapping("/iniData")
    @Operation(summary = "Tạo data đơn hàng đã hoàn thành và chờ review (Test only)")
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
