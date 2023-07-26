package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.momo.MomoCaptureWalletResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoRefundResponse;
import com.example.springbootmongodb.model.Payment;
import com.example.springbootmongodb.model.PaymentEntity;

public interface PaymentService {
    Payment create(String paymentMethod, long amount);
    MomoCaptureWalletResponse initiatePayment(String orderId, String paymentMethod, long amount);
    void processIpnRequest(MomoIpnCallbackResponse request);
    MomoRefundResponse refund(PaymentEntity payment);
}
