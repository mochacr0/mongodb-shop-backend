package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.momo.MomoCaptureWalletResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoQueryPaymentStatusResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoRefundResponse;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.Payment;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    Payment create(String paymentMethod, long amount);
    Payment initiatePayment(OrderEntity order, Payment payment, HttpServletRequest httpServletRequest);
    void processIpnRequest(MomoIpnCallbackResponse request, HttpServletRequest httpServletRequest);
    Payment refund(Payment payment, long requestedAmount);
    Payment queryPaymentStatus(String orderId, Payment payment);
}
