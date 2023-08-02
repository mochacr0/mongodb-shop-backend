package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.PaymentStatus;
import com.example.springbootmongodb.common.data.payment.momo.*;
import com.example.springbootmongodb.common.utils.UrlUtils;
import com.example.springbootmongodb.config.MomoCredentials;
import com.example.springbootmongodb.exception.*;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.OrderStatus;
import com.example.springbootmongodb.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.example.springbootmongodb.controller.ControllerConstants.ORDER_IPN_REQUEST_CALLBACK_ROUTE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MomoPaymentServiceImpl implements PaymentService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MomoCredentials momoCredentials;
    @Autowired
    @Lazy
    private OrderService orderService;
    public static final String UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE = "Payment method is not supported";
    private static final String DEFAULT_EXTRA_DATA = "Thanh toán qua ví Momo";

    @Override
    public Payment create(String paymentMethod, long amount) {
        log.info("Performing PaymentService create");
        PaymentMethod method = PaymentMethod.parseFromString(paymentMethod);
        if (method == null) {
            throw new InvalidDataException(UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE);
        }
        return Payment
                .builder()
                .amount(amount)
                .method(method)
                .status(PaymentStatus.NEW)
                .build();
    }

    @Override
    @Transactional
    public MomoCaptureWalletResponse initiatePayment(String orderId, HttpServletRequest httpServletRequest) {
        OrderEntity order;
        try {
            order = orderService.findById(orderId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateOrder(order, PaymentStatus.NEW, OrderState.UNPAID);
        Payment orderPayment = order.getPayment();
        String requestId = UUID.randomUUID().toString();
        String requestBody = buildCaptureWalletRequestBody(order, requestId, httpServletRequest);
        HttpRequest initiateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_CAPTURE_WALLET_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(initiateRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new UnavailableServiceException(httpResponse.body());
        }
        MomoCaptureWalletResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), MomoCaptureWalletResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
            throw new UnprocessableContentException(response.getMessage());
        }
        orderPayment.setStatus(PaymentStatus.INITIATED);
        orderPayment.setDescription(response.getMessage());
        order.setPayment(orderPayment);
        orderService.save(order);
        log.info("----------PAY URL: " + response.getPayUrl());
        log.info("----------SEND REQUEST ID: " + requestId);
        log.info("----------ORDER ID: " + order.getId());
        log.info("----------AMOUNT: " + orderPayment.getAmount());
        log.info("----------RECEIVE REQUEST ID: " + response.getRequestId());
        return response;
    }

    @Override
    public void processIpnRequest(MomoIpnCallbackResponse response, HttpServletRequest httpServletRequest) {
        log.info("Performing MomoPaymentService processIpnRequest");
        OrderEntity order;
        try {
            order = orderService.findById(response.getOrderId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        validateOrder(order, PaymentStatus.INITIATED, OrderState.UNPAID);
        switch (response.getResultCode()) {
            case 0, 9000 -> processSuccessfulTrans(order, response);
            case 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1030 -> processFailedTrans(order, response);
        }
        order.getPayment().setDescription(response.getMessage());
        orderService.save(order);
    }

    private void processSuccessfulTrans(OrderEntity order, MomoIpnCallbackResponse response) {
        Payment orderPayment = order.getPayment();
        orderPayment.setStatus(PaymentStatus.PAID);
        orderPayment.setTransId(response.getTransId());
        OrderStatus newOrderStatus = OrderStatus
                .builder()
                .state(OrderState.WAITING_TO_ACCEPT)
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(newOrderStatus);
    }

    private void processFailedTrans(OrderEntity order, MomoIpnCallbackResponse response) {
        Payment orderPayment = order.getPayment();
        orderPayment.setStatus(PaymentStatus.FAILED);
        orderPayment.setTransId(response.getTransId());
        OrderStatus newOrderStatus = OrderStatus
                .builder()
                .state(OrderState.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();
        order.getStatusHistory().add(newOrderStatus);
    }


    @Override
    public Payment refund(String orderId) {
        log.info("Performing PaymentService refund");
        //TODO: validate signature
        OrderEntity order;
        try {
            order = orderService.findById(orderId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        Payment orderPayment = order.getPayment();
        validateOrder(order, PaymentStatus.PAID, OrderState.WAITING_TO_ACCEPT, OrderState.PREPARING, OrderState.READY_TO_SHIP, OrderState.IN_CANCEL);
        String requestBody = buildRefundRequest(orderPayment);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_REFUND_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new InternalErrorException(httpResponse.body());
        }
        MomoRefundResponse response;
        try {
             response = objectMapper.readValue(httpResponse.body(), MomoRefundResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
            throw new UnprocessableContentException(response.getMessage());
        }
        orderPayment.setStatus(PaymentStatus.REFUNDED);
        orderPayment.setDescription(response.getMessage());
        order.setPayment(orderPayment);
        orderService.save(order);
        return orderPayment;
    }

    @Override
    public Payment queryPaymentStatus(String orderId) {
        log.info("Performing PaymentService queryPaymentStatus");
        OrderEntity order;
        try {
            order = orderService.findById(orderId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        Payment orderPayment = order.getPayment();
        validatePaymentMethod(orderPayment.getMethod());
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_QUERY_PAYMENT_STATUS_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryStatusRequest(order.getId())))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new InternalErrorException(httpResponse.body());
        }
        MomoQueryPaymentStatusResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), MomoQueryPaymentStatusResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
//        if (orderPayment.getTransId().equalsIgnoreCase(response.getTransId())
//                || orderPayment.getDescription().equalsIgnoreCase(response.getMessage())) {
//            return orderPayment;
//        }
        orderPayment.setTransId(response.getTransId());
        orderPayment.setDescription(response.getMessage());
        order.setPayment(orderPayment);
        orderService.save(order);
        return orderPayment;
    }

    private String buildCaptureWalletRequestBody(OrderEntity order, String requestId, HttpServletRequest httpRequest) {
        Payment payment = order.getPayment();
        String baseUrl = UrlUtils.getBaseUrl(httpRequest);
        String ipnUrl = baseUrl + ORDER_IPN_REQUEST_CALLBACK_ROUTE;
        String redirectUrl = ipnUrl;
        String valueToDigest = buildCaptureWalletRawSignature(payment.getAmount(), ipnUrl, order.getId(), DEFAULT_EXTRA_DATA, DEFAULT_EXTRA_DATA, redirectUrl, requestId);
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoCaptureWalletRequest captureWalletRequest = MomoCaptureWalletRequest
                .builder()
                .partnerCode(momoCredentials.getPartnerCode())
                .requestType(MomoRequestType.CAPTURE_WALLET.getValue())
                .ipnUrl(ipnUrl)
                .redirectUrl(ipnUrl)
                .requestId(requestId)
                .orderId(order.getId())
                .orderInfo(DEFAULT_EXTRA_DATA)
                .amount(payment.getAmount())
                .autoCapture(true)
                .extraData(DEFAULT_EXTRA_DATA)
                .lang(MomoResponseLanguage.VI.getValue())
                .signature(signedSignature)
                .build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(captureWalletRequest);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Serializing failed");
        }
        return requestBody;
    }

    private String buildRefundRequest(Payment payment) {
        String requestId = UUID.randomUUID().toString();
        String testOrderId = UUID.randomUUID().toString();
        String valueToDigest = "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + payment.getAmount() +
                "&description=" + "Test refund" +
                "&orderId=" + testOrderId +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&requestId=" + requestId +
                "&transId=" + payment.getTransId();
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoRefundRequest refundRequest = MomoRefundRequest
                .builder()
                .orderId(testOrderId)
                .partnerCode(momoCredentials.getPartnerCode())
                .amount(payment.getAmount())
                .requestId(requestId)
                .description("Test refund")
                .lang(MomoResponseLanguage.VI.getValue())
                .transId(payment.getTransId())
                .signature(signedSignature)
                .build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(refundRequest);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Serializing failed");
        }
        return requestBody;
    }

    private String buildCaptureWalletRawSignature(long amount, String ipnUrl, String orderId, String extraData, String orderInfo, String redirectUrl, String requestId) {
        return "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + MomoRequestType.CAPTURE_WALLET.getValue();
    }

    private String buildQueryStatusRequest(String orderId) {
        String requestId = UUID.randomUUID().toString();
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(buildQueryStatusRawSignature(orderId, requestId));
        MomoQueryPaymentStatusRequest queryStatusRequest = MomoQueryPaymentStatusRequest
                .builder()
                .partnerCode(momoCredentials.getPartnerCode())
                .requestId(requestId)
                .orderId(orderId)
                .lang(MomoResponseLanguage.VI.getValue())
                .signature(signedSignature)
                .build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(queryStatusRequest);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        return requestBody;
    }

    private String buildQueryStatusRawSignature(String orderId, String requestId) {
        return "accessKey=" + momoCredentials.getAccessKey() +
                "&orderId=" + orderId +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&requestId=" + requestId;
    }

    private void validateOrder(OrderEntity order, PaymentStatus expectedPaymentStatus, OrderState... expectedOrderStates) {
        OrderState currentOrderState = order.getStatusHistory().get(order.getStatusHistory().size() - 1).getState();
        if (Arrays.stream(expectedOrderStates).noneMatch(expectedState -> expectedState == currentOrderState)) {
            throw new InvalidDataException(String.format("Order is %s",
                    currentOrderState.getMessage().toLowerCase()));
        }
        Payment orderPayment = order.getPayment();
        validatePaymentMethod(orderPayment.getMethod());
        if (orderPayment.getStatus() != expectedPaymentStatus) {
            throw new InvalidDataException(String.format("Payment is %s. It must be %s to perform this action",
                    orderPayment.getStatus().name().toLowerCase(),
                    expectedPaymentStatus.name().toLowerCase()));
        }
    }

    private void validatePaymentMethod(PaymentMethod method) {
        if (method != PaymentMethod.MOMO) {
            throw new InvalidDataException(UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE);
        }
    }

}
