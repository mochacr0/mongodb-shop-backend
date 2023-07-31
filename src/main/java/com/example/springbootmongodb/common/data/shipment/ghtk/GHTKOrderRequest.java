package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKOrderRequest {
    @JsonAlias("id")
    private String orderId;
    @JsonAlias("pick_name")
    private String pickName;
    @JsonAlias("pick_money")
    private int pickMoney;
    @JsonAlias("pick_address_id")
    private String pickAddressId;
    @JsonAlias("pick_address")
    private String pickAddress;
    @JsonAlias("pick_province")
    private String pickProvince;
    @JsonAlias("pick_district")
    private String pickDistrict;
    @JsonAlias("pick_ward")
    private String pickWard;
    @JsonAlias("pick_street")
    private String pickStreet;
    @JsonAlias("pick_tel")
    private String pickPhoneNumber;
    @JsonAlias("pick_email")
    private String pickEmail;
    private String name;
    private String address;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String hamlet;
    private String tel;
    private String note;
    private String email;
    @JsonAlias("use_return_address")
    private int isReturnAddressUsed;
    @JsonAlias("return_name")
    private String returnName;
    @JsonAlias("return_address")
    private String returnAddress;
    @JsonAlias("return_province")
    private String returnProvince;
    @JsonAlias("return_district")
    private String returnDistrict;
    @JsonAlias("return_ward")
    private String returnWard;
    @JsonAlias("return_street")
    private String returnStreet;
    @JsonAlias("return_tel")
    private String returnPhoneNumber;
    @JsonAlias("return_email")
    private String returnEmail;
    @JsonAlias("is_freeship")
    private int isFreeShip;
    @JsonAlias("weight_option")
    private String weightOption;
    @JsonAlias("total_weight")
    private double totalWeight;
    @JsonAlias("pick_work_shift")
    private int pickWorkShift;
    @JsonAlias("deliver_work_ship")
    private int deliverWorkShift;
    @JsonAlias("label_id")
    private String labelId;
    @JsonAlias("pick_date")
    private String pickDate;
    @JsonAlias("deliver_date")
    private String deliverDate;
    private String expired;
    private int value;
    private int opm;
    @JsonAlias("pick_option")
    private String pickOption;
    @JsonAlias("actual_transfer_method")
    private String actualTransferMethod;
    private String transport;
    @JsonAlias("deliver_option")
    private String deliverOption;
    @JsonAlias("pick_session")
    private String pickSession;
    private List<Integer> tags = new ArrayList<>();

}
