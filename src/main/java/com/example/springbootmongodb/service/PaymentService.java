package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.payment.momo.MomoCaptureWalletResponse;
import com.example.springbootmongodb.common.data.payment.momo.MomoIpnCallbackResponse;

public interface PaymentService {
    MomoCaptureWalletResponse createRequest();
    void processIpnRequest(MomoIpnCallbackResponse request);
}
