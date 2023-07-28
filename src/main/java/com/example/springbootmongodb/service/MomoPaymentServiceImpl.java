package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.momo.*;
import com.example.springbootmongodb.common.utils.UrlUtils;
import com.example.springbootmongodb.config.MomoCredentials;
import com.example.springbootmongodb.exception.*;
import com.example.springbootmongodb.model.OrderEntity;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
                .isPaid(false)
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
        if (order.getPayment().getMethod() != PaymentMethod.MOMO) {
            throw new InvalidDataException(UNSUPPORTED_PAYMENT_METHOD_ERROR_MESSAGE);
        }
        if (order.getPayment().isPaid()) {
            throw new InvalidDataException("Order has been paid");
        }
        String requestId = UUID.randomUUID().toString();
        String requestBody = buildCaptureWalletRequestBody(order, requestId, httpServletRequest);
        HttpRequest initiateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_CAPTURE_WALLET_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(initiateRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new UnavailableServiceException("Momo Service Unavailable");
        }
        MomoCaptureWalletResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), MomoCaptureWalletResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new UnavailableServiceException(response.getMessage());
        }
        order.getPayment().setRequestId(requestId);
        orderService.save(order);
        return response;
    }

    private String buildCaptureWalletRequestBody(OrderEntity order, String requestId, HttpServletRequest httpRequest) {
        Payment payment = order.getPayment();
        String baseUrl = UrlUtils.getBaseUrl(httpRequest);
        String ipnUrl = baseUrl + ORDER_IPN_REQUEST_CALLBACK_ROUTE;
        String redirectUrl = ipnUrl;
//        String valueToDigest = "accessKey=" + momoCredentials.getAccessKey() +
//                "&amount=" + payment.getAmount() +
//                "&extraData=" + DEFAULT_EXTRA_DATA +
//                "&ipnUrl=" + ipnUrl +
//                "&orderId=" + order.getId() +
//                "&orderInfo=" + DEFAULT_EXTRA_DATA +
//                "&partnerCode=" + momoCredentials.getPartnerCode() +
//                "&redirectUrl=" + redirectUrl +
//                "&requestId=" + requestId +
//                "&requestType=" + MomoRequestType.CAPTURE_WALLET.getValue();
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

    @Override
    public void processIpnRequest(MomoIpnCallbackResponse response, HttpServletRequest httpServletRequest) {
        log.info("Performing MomoPaymentService processIpnRequest");
        log.info(response.toString());
        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
            return;
        }
        OrderEntity order;
        try {
            order = orderService.findById(response.getOrderId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        //validate signature
        Payment payment = order.getPayment();
        payment.setPaid(true);
        payment.setTransId(response.getTransId());
        order.setPayment(payment);
        orderService.save(order);
    }

    @Override
    public MomoRefundResponse refund(String orderId) {
        log.info("Performing PaymentService refund");
        //TODO: validate signature
        OrderEntity order;
        try {
            order = orderService.findById(orderId);
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        Payment payment = order.getPayment();
        String requestBody = buildRefundRequest(payment);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_REFUND_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new UnavailableServiceException("Momo Service Unavailable");
        }
        MomoRefundResponse response;
        try {
             response = objectMapper.readValue(httpResponse.body(), MomoRefundResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        return response;
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
        String rawSignature =  "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + MomoRequestType.CAPTURE_WALLET.getValue();
        log.info("-----------------------------: " + rawSignature);
        return rawSignature;
    }
}
