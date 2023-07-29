package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.config.GHTKCredentials;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ShopAddressEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.example.springbootmongodb.common.data.shipment.ghtk.GHTKEndpoints.GHTK_CALCULATE_DELIVERY_FEE_ROUTE;
import static com.example.springbootmongodb.common.data.shipment.ghtk.GHTKEndpoints.GHTK_GET_LV4_ADDRESSES_ROUTE;

@Service
@Slf4j
@RequiredArgsConstructor
public class GHTKShipmentServiceImpl extends AbstractService implements ShipmentService {
    private final HttpClient httpClient;
    private final GHTKCredentials ghtkCredentials;
    private final ObjectMapper objectMapper;
    private final ShopAddressService shopAddressService;
    private final String TOKEN_HEADER_NAME = "Token";

    @Override
    public GHTKLv4AddressesResponse getLv4Addresses(String address, String province, String district, String wardStreet) {
        log.info("Performing ShipmentService getLv4Addresses");
        String uri = UriComponentsBuilder.fromUriString(GHTK_GET_LV4_ADDRESSES_ROUTE)
                .queryParam("province", province)
                .queryParam("district", district)
                .queryParam("ward_street", wardStreet)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header(TOKEN_HEADER_NAME, ghtkCredentials.getApiToken())
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        GHTKLv4AddressesResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), GHTKLv4AddressesResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (StringUtils.isNotEmpty(response.getMessage())) {
            throw new InvalidDataException(response.getMessage());
        }
        return response;
    }

//    private void validateGetLv4Addresses(String province, String district, String wardStreet) {
//        if (StringUtils.isEmpty(province)) {
//            throw new InvalidDataException("Province is required");
//        }
//        if (StringUtils.isEmpty(district)) {
//            throw new InvalidDataException("District is required");
//        }
//        if (StringUtils.isEmpty(wardStreet)) {
//            throw new InvalidDataException("Ward street is required");
//        }
//    }

    @Override
    public GHTKCalculateFeeResponse calculateFee(GHTKCalculateFeeRequest request) {
        log.info("Performing ShipmentService calculateFee");
        if (StringUtils.isNotEmpty(request.getPickAddressId())) {
            ShopAddressEntity shopAddress;
            try {
                shopAddress = shopAddressService.findById(request.getPickAddressId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException(exception.getMessage());
            }
            request.setPickProvince(shopAddress.getProvince());
            request.setPickDistrict(shopAddress.getDistrict());
            request.setPickWard(shopAddress.getWard());
            request.setPickStreet(shopAddress.getStreetAddress());
        }
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(buildCalculateFeeUri(request)))
                .header(TOKEN_HEADER_NAME, ghtkCredentials.getApiToken())
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        GHTKCalculateFeeResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), GHTKCalculateFeeResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (StringUtils.isNotEmpty(response.getMessage())) {
            throw new InvalidDataException(response.getMessage());
        }
        return response;
    }

    private String buildCalculateFeeUri(GHTKCalculateFeeRequest request) {
        return UriComponentsBuilder.fromUriString(GHTK_CALCULATE_DELIVERY_FEE_ROUTE)
                .queryParam("pick_address_id", request.getPickAddressId())
                .queryParam("pick_address", request.getPickAddress())
                .queryParam("pick_province", request.getPickProvince())
                .queryParam("pick_district", request.getPickDistrict())
                .queryParam("pick_ward", request.getPickWard())
                .queryParam("pick_street", request.getPickStreet())
                .queryParam("address", request.getAddress())
                .queryParam("province", request.getProvince())
                .queryParam("district", request.getDistrict())
                .queryParam("ward", request.getWard())
                .queryParam("street", request.getStreet())
                .queryParam("weight", request.getWeight())
                .queryParam("value", request.getValue())
                .queryParam("transport", request.getTransport())
                .queryParam("deliver_option", request.getDeliverOption())
                .queryParam("tags", request.getTags())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
