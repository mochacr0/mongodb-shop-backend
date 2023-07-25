package com.example.springbootmongodb.common.data.payment.momo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
public class MomoCaptureWalletResponse extends MomoAbstractResponse {
    private String deeplink;
    private String deeplinkMiniApp;
    private String payUrl;
    private String qrCodeUrl;
}
