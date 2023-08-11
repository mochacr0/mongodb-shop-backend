package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.common.data.mapper.ProductItemMapper;
import com.example.springbootmongodb.common.data.mapper.UserMapper;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.springbootmongodb.config.OrderPolicies.MAX_DAYS_WAITING_TO_PREPARING;
import static com.example.springbootmongodb.config.OrderPolicies.ORDER_MOMO_TRANSACTION_EXPIRY_TIME_IN_MINUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.ORDER_IPN_REQUEST_CALLBACK_ROUTE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MomoPaymentServiceImpl extends AbstractService implements PaymentService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MomoCredentials momoCredentials;
    private final ProductItemMapper productItemMapper;
    private final UserMapper userMapper;

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

//    @Override
//    public Payment initiatePayment(String orderId, Payment payment, HttpServletRequest httpServletRequest) {
//        validatePaymentStatus(payment.getStatus(), PaymentStatus.NEW);
//        validatePaymentMethod(payment.getMethod(), PaymentMethod.MOMO);
//        String requestId = UUID.randomUUID().toString();
//        String requestBody = buildCaptureWalletRequestBody(orderId, payment, requestId, httpServletRequest);
//        HttpRequest initiateRequest = HttpRequest
//                .newBuilder()
//                .uri(URI.create(MomoEndpoints.MOMO_CAPTURE_WALLET_ROUTE))
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//        HttpResponse<String> httpResponse;
//        try {
//            httpResponse = httpClient.send(initiateRequest, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException exception) {
//            throw new InternalErrorException(exception.getMessage());
//        }
//        if (httpResponse.statusCode() >= 500) {
//            throw new InternalErrorException(httpResponse.body());
//        }
//        MomoCaptureWalletResponse response;
//        try {
//            response = objectMapper.readValue(httpResponse.body(), MomoCaptureWalletResponse.class);
//        } catch (JsonProcessingException exception) {
//            throw new InternalErrorException(exception.getMessage());
//        }
//        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
//            throw new UnprocessableContentException(response.getMessage());
//        }
//        payment.setStatus(PaymentStatus.INITIATED);
//        payment.setDescription(response.getMessage());
//        payment.setPayUrl(response.getPayUrl());
//        log.info("----------PAY URL: " + response.getPayUrl());
//        log.info("----------SEND REQUEST ID: " + requestId);
//        log.info("----------ORDER ID: " + orderId);
//        log.info("----------AMOUNT: " + payment.getAmount());
//        log.info("----------RECEIVE REQUEST ID: " + response.getRequestId());
//        return payment;
//    }

    @Override
    public Payment initiatePayment(OrderEntity order, Payment payment, HttpServletRequest httpServletRequest) {
        validatePaymentStatus(payment.getStatus(), PaymentStatus.NEW);
        validatePaymentMethod(payment.getMethod(), PaymentMethod.MOMO);
        String requestId = UUID.randomUUID().toString();
        String requestBody = buildPayWithMethodBody(order, payment, requestId, httpServletRequest);
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
            throw new InternalErrorException(httpResponse.body());
        }
        MomoPayWithMethodResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), MomoPayWithMethodResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
            throw new UnprocessableContentException(response.getMessage());
        }
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setDescription(response.getMessage());
        payment.setPayUrl(response.getShortLink());
        log.info("-----------PAY URL: " + payment.getPayUrl());
        return payment;
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
            case 99, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1030 -> processFailedTrans(order, response);
        }
        order.getPayment().setDescription(response.getMessage());
        orderService.save(order);
    }

    private void processSuccessfulTrans(OrderEntity order, MomoIpnCallbackResponse response) {
        Payment orderPayment = order.getPayment();
        orderPayment.setStatus(PaymentStatus.PAID);
        orderPayment.setTransId(response.getTransId());
        LocalDateTime now = LocalDateTime.now();
        OrderStatus newOrderStatus = OrderStatus
                .builder()
                .state(OrderState.WAITING_TO_ACCEPT)
                .createdAt(now)
                .build();
        order.getStatusHistory().add(newOrderStatus);
        order.setExpiredAt(now.plusDays(MAX_DAYS_WAITING_TO_PREPARING));
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
        order.setExpiredAt(null);
    }


    @Override
    public Payment refund(Payment payment) {
        log.info("Performing PaymentService refund");
        //TODO: validate signature
        validatePaymentStatus(payment.getStatus(), PaymentStatus.PAID);
        validatePaymentMethod(payment.getMethod(), PaymentMethod.MOMO);
        String requestBody = buildRefundRequestBody(payment);
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
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setDescription(response.getMessage());
        return payment;
    }

    @Override
    public Payment queryPaymentStatus(String orderId, Payment payment) {
        log.info("Performing PaymentService queryPaymentStatus");
        validatePaymentMethod(payment.getMethod(), PaymentMethod.MOMO);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_QUERY_PAYMENT_STATUS_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryStatusRequest(orderId)))
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
        payment.setTransId(response.getTransId());
        payment.setDescription(response.getMessage());
        return payment;
    }

    private String buildCaptureWalletRequestBody(String orderId, Payment payment, String requestId, HttpServletRequest httpRequest) {
        String baseUrl = UrlUtils.getBaseUrl(httpRequest);
        String ipnUrl = baseUrl + ORDER_IPN_REQUEST_CALLBACK_ROUTE;
        String redirectUrl = ipnUrl;
        String valueToDigest = buildCaptureWalletRawSignature(payment.getAmount(), ipnUrl, orderId, DEFAULT_EXTRA_DATA, DEFAULT_EXTRA_DATA, redirectUrl, requestId);
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoCaptureWalletRequest captureWalletRequest = MomoCaptureWalletRequest
                .builder()
                .partnerCode(momoCredentials.getPartnerCode())
                .requestType(MomoRequestType.CAPTURE_WALLET.getValue())
                .ipnUrl(ipnUrl)
                .redirectUrl(ipnUrl)
                .requestId(requestId)
                .orderId(orderId)
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

    private String buildRefundRequestBody(Payment payment) {
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

    private String buildPayWithMethodBody(OrderEntity order, Payment payment, String requestId, HttpServletRequest httpRequest) {
        String baseUrl = UrlUtils.getBaseUrl(httpRequest);
        String ipnUrl = baseUrl + ORDER_IPN_REQUEST_CALLBACK_ROUTE;
        String redirectUrl = ipnUrl;
        String valueToDigest = buildPayWithMethodRawSignature(payment.getAmount(), ipnUrl, order.getId(), DEFAULT_EXTRA_DATA, DEFAULT_EXTRA_DATA, redirectUrl, requestId);
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoPayWithMethodRequest payWithMethodRequest = MomoPayWithMethodRequest
                .builder()
                .partnerCode(momoCredentials.getPartnerCode())
                .partnerName("Shop")
                .requestId(requestId)
                .amount(payment.getAmount())
                .orderId(order.getId())
                .orderInfo(DEFAULT_EXTRA_DATA)
                .redirectUrl(ipnUrl)
                .ipnUrl(ipnUrl)
                .requestType(MomoRequestType.PAY_WITH_METHOD.getValue())
                .extraData(DEFAULT_EXTRA_DATA)
                .items(order.getOrderItems().stream().map(productItemMapper::fromOrderItemToMomoItem).toList())
                .userInfo(userMapper.fromEntityToMomoUserInfo(order.getUser()))
                .orderExpireTimeInMinute(ORDER_MOMO_TRANSACTION_EXPIRY_TIME_IN_MINUTE)
                .autoCapture(true)
                .lang(MomoResponseLanguage.VI.getValue())
                .signature(signedSignature)
                .build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(payWithMethodRequest);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Serializing failed");
        }
        return requestBody;
    }

    private String buildPayWithMethodRawSignature(long amount, String ipnUrl, String orderId, String extraData, String orderInfo, String redirectUrl, String requestId) {
        return "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + MomoRequestType.PAY_WITH_METHOD.getValue();
    }

    private void validateOrder(OrderEntity order, PaymentStatus expectedPaymentStatus, OrderState... expectedOrderStates) {
        validateOrderState(order, expectedOrderStates);
        Payment orderPayment = order.getPayment();
        validatePaymentMethod(orderPayment.getMethod(), PaymentMethod.MOMO);
        validatePaymentStatus(orderPayment.getStatus(), expectedPaymentStatus);
    }

}
