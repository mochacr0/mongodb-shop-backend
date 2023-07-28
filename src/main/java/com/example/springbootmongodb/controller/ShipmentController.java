package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;
import com.example.springbootmongodb.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.SHIPMENT_CALCULATE_DELIVERY_FEE_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.SHIPMENT_GET_LV4_ADDRESSES_ROUTE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @GetMapping(value = SHIPMENT_GET_LV4_ADDRESSES_ROUTE)
    @Operation(summary = "Truy xuất địa chỉ cấp 4 theo tỉnh/thành, quận/huyện và phường/xã")
    GHTKLv4AddressesResponse getLv4Addresses(@RequestParam(required = false) String address,
                                             @RequestParam String province,
                                             @RequestParam String district,
                                             @RequestParam String wardStreet) {
        return shipmentService.getLv4Addresses(address, province, district, wardStreet);
    }

    @GetMapping(value = SHIPMENT_CALCULATE_DELIVERY_FEE_ROUTE)
    @Operation(summary = "Tính phí vận chuyển")
    GHTKCalculateFeeResponse calculateDeliveryFee(@RequestParam(required = false) String pickAddressId,
                                                  @RequestParam(required = false) String pickAddress,
                                                  @RequestParam String pickProvince,
                                                  @RequestParam String pickDistrict,
                                                  @RequestParam(required = false) String pickWard,
                                                  @RequestParam(required = false) String pickStreet,
                                                  @RequestParam(required = false) String address,
                                                  @RequestParam String province,
                                                  @RequestParam String district,
                                                  @RequestParam(required = false) String ward,
                                                  @RequestParam(required = false) String street,
                                                  @RequestParam String weight,
                                                  @RequestParam(required = false) String value,
                                                  @RequestParam(required = false) String transport,
                                                  @RequestParam(required = false) String deliverOption,
                                                  @RequestParam(required = false) List<Integer> tags) {
        GHTKCalculateFeeRequest request = GHTKCalculateFeeRequest.builder()
                .pickAddressId(pickAddressId)
                .pickAddress(pickAddress)
                .pickProvince(pickProvince)
                .pickDistrict(pickDistrict)
                .pickWard(pickWard)
                .pickStreet(pickStreet)
                .address(address)
                .province(province)
                .district(district)
                .ward(ward)
                .street(street)
                .weight(weight)
                .value(value)
                .transport(transport)
                .tags(tags)
                .build();
        return shipmentService.calculateFee(request);
    }
}

