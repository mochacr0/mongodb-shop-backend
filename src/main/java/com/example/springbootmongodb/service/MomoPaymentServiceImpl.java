package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.momo.*;
import com.example.springbootmongodb.config.MomoCredentials;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MomoPaymentServiceImpl implements PaymentService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MomoCredentials momoCredentials;

    @Override
    public MomoCaptureWalletResponse createRequest() {
        String requestBody = buildCaptureWalletRequestBody();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.create))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            log.info(httpResponse.toString());
            MomoCaptureWalletResponse response;
            response = objectMapper.readValue(httpResponse.body(), MomoCaptureWalletResponse.class);
            if (httpResponse.statusCode() >= 500) {
                throw new InternalErrorException(response.getMessage());
            }
            return response;
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
    }

    @Override
    public void processIpnRequest(MomoIpnCallbackResponse request) {
        log.info("Performing MomoPaymentService processIpnRequest");
        log.info(request.toString());
    }

    private String buildCaptureWalletRequestBody() {
        long amount = 10000;
        String ipnUrl = "http://localhost:5000/test/momo/callback";
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String redirectUrl = "http://localhost:5000/test/momo/callback";
        String extraData = "Thanh toán qua ví Momo";
        String orderInfo = "Thanh toán qua ví Momo";
        String valueToDigest = "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + MomoRequestType.CAPTURE_WALLET.getValue();
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoCaptureWalletRequest captureWalletRequest = MomoCaptureWalletRequest
                .builder()
                .partnerCode(momoCredentials.getPartnerCode())
                .requestType(MomoRequestType.CAPTURE_WALLET.getValue())
                .ipnUrl(ipnUrl)
                .redirectUrl(redirectUrl)
                .requestId(requestId)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .amount(amount)
                .autoCapture(true)
                .extraData(extraData)
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


}
