package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.mapper.OrderMapper;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.service.OrderService;
import com.example.springbootmongodb.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @PostMapping(value = ORDER_CREATE_ORDER_ROUTE)
    @Operation(summary = "Tạo đơn hàng mới",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    Order create(@RequestBody OrderRequest request) {
       return orderMapper.fromEntity(orderService.create(request));
    }

    @GetMapping(value = ORDER_GET_ORDER_BY_ID_ROUTE)
    @Operation(summary = "Tìm đơn hàng theo id",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    Order getById(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.findById(orderId));
    }

    @PostMapping(value = ORDER_INITIATE_PAYMENT_ROUTE)
    @Operation(summary = "Khởi tạo giao dịch thanh toán",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    RedirectView initiatePayment(@PathVariable String orderId, HttpServletRequest httpServletRequest) {
        String payUrl = orderService.initiatePayment(orderId, httpServletRequest);
        return new RedirectView(payUrl);
    }

    @GetMapping(value = ORDER_IPN_REQUEST_CALLBACK_ROUTE)
    @Operation(summary = "Momo IPN callback (Không dùng API này)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void momoCallback(@RequestParam("partnerCode") String partnerCode,
                      @RequestParam("orderId") String orderId,
                      @RequestParam("requestId") String requestId,
                      @RequestParam("amount") long amount,
                      @RequestParam("orderInfo") String orderInfo,
                      @RequestParam("orderType") String orderType,
                      @RequestParam("transId") String transId,
                      @RequestParam("resultCode") int resultCode,
                      @RequestParam("message") String message,
                      @RequestParam("payType") String payType,
                      @RequestParam("extraData") String extraData,
                      @RequestParam("signature") String signature,
                      HttpServletRequest httpServletRequest) {
        MomoIpnCallbackResponse request = MomoIpnCallbackResponse.builder()
                .partnerCode(partnerCode)
                .orderId(orderId)
                .requestId(requestId)
                .amount(amount)
                .orderInfo(orderInfo)
                .orderType(orderType)
                .transId(transId)
                .resultCode(resultCode)
                .message(message)
                .payType(payType)
                .extraData(extraData)
                .signature(signature)
                .build();
        paymentService.processIpnRequest(request, httpServletRequest);
    }

//    @PostMapping(value = ORDER_REQUEST_ORDER_REFUND_ROUTE)
//    @Operation(summary = "Hoàn tiền",
//            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
//    Payment refund(@PathVariable String orderId) {
//        return paymentService.refund(orderId);
//    }

    @PostMapping(value = ORDER_CANCEL_ORDER_ROUTE)
    @Operation(summary = "Hủy đơn hàng",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    Order cancel(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.cancel(orderId));
    }

    @PostMapping(value = ORDER_ACCEPT_ORDER_ROUTE)
    @Operation(summary = "Chấp nhận đơn hàng",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    Order accept(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.accept(orderId));
    }

    @PostMapping(value = ORDER_PLACE_SHIPMENT_ORDER_ROUTE)
    @Operation(summary = "Đặt đơn vận chuyển",
            security = {@SecurityRequirement(name = SWAGGER_SECURITY_SCHEME_BEARER_AUTH)})
    Order placeShipmentOrder(@PathVariable String orderId,
                             @RequestBody ShipmentRequest request) {
        return orderMapper.fromEntity(orderService.placeShipmentOrder(orderId, request));
    }

    @PostMapping(value = ORDER_UPDATE_SHIPMENT_STATUS_CALLBACK_ROUTE)
    @Operation(summary = "Callback update trạng thái đơn hàng của GHTK")
    Order ghtkUpdateStatusCallback(@RequestBody GHTKUpdateStatusRequest request) {
        return orderMapper.fromEntity(orderService.processShipmentStatusUpdateRequest(request));
    }

    @PostMapping(ORDER_CONFIRM_ORDER_DELIVERED_ROUTE)
    @Operation(summary = "Xác nhận đã nhận hàng")
    Order confirmDelivered(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.confirmDelivered(orderId));
    }
}
