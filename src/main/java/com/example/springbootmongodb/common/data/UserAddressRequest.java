package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAddressRequest {
    @Schema(title = "name", description = "Receiver name", example = "Nguyen Van A")
    private String name;
    @Schema(title = "phoneNumber", description = "Receiver phone number", example = "0123456789")
    private String phoneNumber;
    @Schema(title = "province", description = "Delivery province", example = "0123456789")
    private String province;
    @Schema(title = "district", description = "Delivery district", example = "0123456789")
    private String district;
    @Schema(title = "ward", description = "Delivery ward", example = "0123456789")
    private String ward;
    @Schema(title = "streetAddress", description = "Delivery street ", example = "0123456789")
    private String streetAddress;
    @Schema(title = "isDefault", description = "If this email address is the default for the user, this boolean value will be true", example = "false")
    private boolean isDefault;
}
