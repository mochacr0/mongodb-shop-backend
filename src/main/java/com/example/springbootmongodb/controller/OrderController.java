package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.mapper.OrderMapper;
import com.example.springbootmongodb.common.data.payment.momo.MomoCaptureWalletResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoRefundResponse;
import com.example.springbootmongodb.service.OrderService;
import com.example.springbootmongodb.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @PostMapping(value = ORDER_CREATE_ORDER_ROUTE)
    @Operation(summary = "Tạo đơn hàng mới")
    Order create(@RequestBody OrderRequest request) {
       return orderMapper.fromEntity(orderService.create(request));
    }

    @GetMapping(value = ORDER_GET_ORDER_BY_ID_ROUTE)
    @Operation(summary = "Tìm đơn hàng theo id")
    Order getById(@PathVariable String orderId) {
        return orderMapper.fromEntity(orderService.findById(orderId));
    }

    @PostMapping(value = ORDER_INITIATE_PAYMENT_ROUTE)
    @Operation(summary = "Khởi tạo giao dịch thanh toán")
    MomoCaptureWalletResponse initiatePayment(@PathVariable String orderId, HttpServletRequest httpServletRequest) {
        return paymentService.initiatePayment(orderId, httpServletRequest);
    }

    @GetMapping(value = ORDER_IPN_REQUEST_CALLBACK_ROUTE)
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

    @PostMapping(value = ORDER_REQUEST_ORDER_REFUND_ROUTE)
    @Operation(summary = "Hoàn tiền")
    MomoRefundResponse refund(@PathVariable String orderId) {
        return paymentService.refund(orderId);
    }
}
