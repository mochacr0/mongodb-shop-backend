package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKOrderRequest {
    @JsonProperty("id")
    private String orderId;
    @JsonProperty("pick_name")
    private String pickName;
    @JsonProperty("pick_money")
    private int pickMoney;
    @JsonProperty("pick_address_id")
    private String pickAddressId;
    @JsonProperty("pick_address")
    private String pickAddress;
    @JsonProperty("pick_province")
    private String pickProvince;
    @JsonProperty("pick_district")
    private String pickDistrict;
    @JsonProperty("pick_ward")
    private String pickWard;
    @JsonProperty("pick_street")
    private String pickStreet;
    @JsonProperty("pick_tel")
    private String pickPhoneNumber;
    @JsonProperty("pick_email")
    private String pickEmail;
    private String name;
    private String address;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String hamlet;
    @JsonProperty("tel")
    private String phoneNumber;
    private String note;
    private String email;
    @JsonProperty("use_return_address")
    private int isReturnAddressUsed;
    @JsonProperty("return_name")
    private String returnName;
    @JsonProperty("return_address")
    private String returnAddress;
    @JsonProperty("return_province")
    private String returnProvince;
    @JsonProperty("return_district")
    private String returnDistrict;
    @JsonProperty("return_ward")
    private String returnWard;
    @JsonProperty("return_street")
    private String returnStreet;
    @JsonProperty("return_tel")
    private String returnPhoneNumber;
    @JsonProperty("return_email")
    private String returnEmail;
    @JsonProperty("is_freeship")
    private int isFreeShip;
    @JsonProperty("weight_option")
    private String weightOption;
    @JsonProperty("total_weight")
    private double totalWeight;
    @JsonProperty("pick_work_shift")
    private int pickWorkShift;
    @JsonProperty("deliver_work_ship")
    private int deliverWorkShift;
    private int value;
    private int opm;
    @JsonProperty("pick_option")
    private String pickOption;
    @JsonProperty("actual_transfer_method")
    private String actualTransferMethod;
    private String transport;
    @JsonProperty("deliver_option")
    private String deliverOption;
    @JsonProperty("pick_session")
    private String pickSession;
    private List<Integer> tags = new ArrayList<>();

}
