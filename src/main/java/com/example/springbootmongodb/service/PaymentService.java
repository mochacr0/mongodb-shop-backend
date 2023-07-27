package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.momo.MomoCaptureWalletResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoRefundResponse;
import com.example.springbootmongodb.model.Payment;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    Payment create(String paymentMethod, long amount);
    MomoCaptureWalletResponse initiatePayment(String orderId, HttpServletRequest httpServletRequest);
    void processIpnRequest(MomoIpnCallbackResponse request, HttpServletRequest httpServletRequest);
    MomoRefundResponse refund(String orderId);
}
