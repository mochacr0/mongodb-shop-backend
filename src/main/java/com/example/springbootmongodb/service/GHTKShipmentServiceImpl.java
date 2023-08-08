package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import com.example.springbootmongodb.common.data.shipment.ghtk.*;
import com.example.springbootmongodb.config.GHTKCredentials;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.Shipment;
import com.example.springbootmongodb.model.ShopAddressEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.springbootmongodb.common.data.shipment.ghtk.GHTKEndpoints.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GHTKShipmentServiceImpl extends AbstractService implements ShipmentService {
    private final HttpClient httpClient;
    private final GHTKCredentials ghtkCredentials;
    private final ObjectMapper objectMapper;
    private final ShopAddressService shopAddressService;
    private final UserAddressService userAddressService;
    private final ShopAddressMapper shopAddressMapper;
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
    public GHTKCalculateFeeResponse calculateDeliveryFee(String userAddressId, double weight, int quantity) {
        log.info("Performing ShipmentService calculateFee");
        UserAddressEntity userAddress;
        try {
            userAddress = userAddressService.findById(userAddressId);
        } catch(ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        ShopAddressEntity shopAddress;
        try {
            shopAddress = shopAddressService.findDefaultAddress();
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException(exception.getMessage());
        }
        GHTKCalculateFeeRequest calculateFeeRequest = GHTKCalculateFeeRequest
                .builder()
                .pickProvince(shopAddress.getProvince())
                .pickDistrict(shopAddress.getDistrict())
                .pickWard(shopAddress.getWard())
                .pickStreet(shopAddress.getStreet())
                .province(userAddress.getProvince())
                .district(userAddress.getDistrict())
                .ward(userAddress.getWard())
                .address(userAddress.getStreet())
                .weight((int)weight * 1000 * quantity)
                .build();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(buildCalculateFeeUri(calculateFeeRequest)))
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
        response.setShopAddress(shopAddressMapper.fromEntity(shopAddress));
        return response;
    }

    @Override
    public Shipment place(OrderEntity order, ShipmentRequest request) {
        log.info("Performing ShipmentService placeOrder");
        //TODO: build GHTK place shipment order request body;
        String requestBody = buildCreateShipmentRequestBody(order, request);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(GHTKEndpoints.GHTK_CREATE_SHIPMENT_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(TOKEN_HEADER_NAME, ghtkCredentials.getApiToken())
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new InternalErrorException(httpResponse.body());
        }
        GHTKCreateShipmentResponse shipmentResponse;
        try {
            shipmentResponse = objectMapper.readValue(httpResponse.body(), GHTKCreateShipmentResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!shipmentResponse.isSuccess()) {
            switch (httpResponse.statusCode()) {
                case 400 -> throw new InvalidDataException(shipmentResponse.getMessage());
                case 422 -> throw new UnprocessableContentException(shipmentResponse.getMessage());
                default -> throw new InternalErrorException(shipmentResponse.getMessage());
            }
        }
        Shipment orderShipment = order.getShipment();
        GHTKOrderResponse orderResponse = shipmentResponse.getOrder();
        orderShipment.setId(orderResponse.getLabel());
        orderShipment.setDeliveryFee(orderResponse.getFee());
        orderShipment.setEstimatedPickTime(orderResponse.getEstimatedPickTime());
        orderShipment.setEstimatedDeliverTime(orderResponse.getEstimatedDeliverTime());
        orderShipment.setEstimatedPickTime(orderResponse.getEstimatedPickTime());
        orderShipment.setInsuranceFee(orderResponse.getInsuranceFee());
        ShipmentStatus succededShipmentStatus = ShipmentStatus
                .builder()
                .description("Đang chờ tiếp nhận")
                .state(ShipmentState.parseFromInt(shipmentResponse.getOrder().getStatusId()))
                .build();
        orderShipment.getStatusHistory().add(succededShipmentStatus);
        return orderShipment;
    }

    @Override
    public Shipment cancel(String orderId, Shipment shipment) {
        log.info("Performing ShipmentService cancel");
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(String.format(GHTK_CANCEL_SHIPMENT_ROUTE_PATTERN, shipment.getId())))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(TOKEN_HEADER_NAME, ghtkCredentials.getApiToken())
                .build();
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (httpResponse.statusCode() >= 500) {
            throw new InternalErrorException(httpResponse.body());
        }
        GHTKAbstractResponse cancelResponse;
        try {
            cancelResponse = objectMapper.readValue(httpResponse.body(), GHTKAbstractResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!cancelResponse.isSuccess()) {
            switch (httpResponse.statusCode()) {
                case 400 -> throw new InvalidDataException(cancelResponse.getMessage());
                case 422 -> throw new UnprocessableContentException(cancelResponse.getMessage());
                default -> throw new InternalErrorException(cancelResponse.getMessage());
            }
        }
        ShipmentStatus cancelShipmentStatus = ShipmentStatus
                .builder()
                .state(ShipmentState.CANCELED)
                .description("Đơn vị vận chuyển đang xử lý yêu cầu hủy đơn")
                .build();
        shipment.getStatusHistory().add(cancelShipmentStatus);
        return shipment;
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

    private String buildCreateShipmentRequestBody(OrderEntity order, ShipmentRequest request) {
        GHTKCreateShipmentRequest createShipmentRequest = GHTKCreateShipmentRequest
                .builder()
                .order(buildOrderShipmentRequest(order, request))
                .products(buildProductShipmentRequest(order))
                .build();
        try {
            return objectMapper.writeValueAsString(createShipmentRequest);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
    }

    private GHTKOrderRequest buildOrderShipmentRequest(OrderEntity order , ShipmentRequest request) {
        GHTKPickOption pickOption = GHTKPickOption.parseFromString(request.getPickOption());
        GHTKWorkShiftOption pickWorkShiftOption = GHTKWorkShiftOption.parseFromString(request.getPickWorkShipOption());
        GHTKWorkShiftOption deliverWorkShiftOption = GHTKWorkShiftOption.parseFromString(request.getDeliverWorkShipOption());
        Shipment orderShipment = order.getShipment();
        ShopAddress shopAddress = orderShipment.getShopAddress();
        UserAddress userAddress = orderShipment.getUserAddress();
        GHTKOrderRequest orderRequest = GHTKOrderRequest
                .builder()
                .orderId(order.getId())
                .pickOption(pickOption.getValue())
                .pickName("Shop")
                .pickMoney((int)order.getSubTotal())
                .pickAddress(shopAddress.getAddress())
                .pickProvince(shopAddress.getProvince())
                .pickDistrict(shopAddress.getDistrict())
                .pickWard(shopAddress.getWard())
                .pickStreet(shopAddress.getStreet())
                .pickPhoneNumber(shopAddress.getPhoneNumber())
                .note(request.getNote())
                .name(userAddress.getName())
                .address(userAddress.getAddress())
                .province(userAddress.getProvince())
                .district(userAddress.getDistrict())
                .ward(userAddress.getWard())
                .street(userAddress.getStreet())
                .hamlet(StringUtils.isNotEmpty(userAddress.getHamlet()) ? userAddress.getHamlet() : "Khác")
                .phoneNumber(userAddress.getPhoneNumber())
                .note(request.getNote())
                .email("")
                .isFreeShip(BooleanUtils.toInteger(request.isFreeShip()))
                .value((int)order.getSubTotal())
                .pickWorkShift(pickWorkShiftOption.getCode())
                .deliverWorkShift(deliverWorkShiftOption.getCode())
                .transport(GHTKTransportMethod.ROAD.getValue())
                .build();
        if (request.getReturnAddressId() != null) {
            ShopAddress returnAddress;
            try {
                returnAddress = shopAddressMapper.fromEntity(shopAddressService.findById(request.getReturnAddressId()));
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException(exception.getMessage());
            }
            if (returnAddress != null) {
                orderRequest.setReturnAddress(returnAddress.getAddress());
                orderRequest.setReturnName("Shop");
                orderRequest.setReturnEmail("");
                orderRequest.setReturnProvince(returnAddress.getProvince());
                orderRequest.setReturnDistrict(returnAddress.getDistrict());
                orderRequest.setReturnWard(returnAddress.getWard());
                orderRequest.setReturnStreet(returnAddress.getStreet());
                orderRequest.setReturnPhoneNumber(returnAddress.getPhoneNumber());
            }
        }
        return orderRequest;
    }

    private List<GHTKProductRequest> buildProductShipmentRequest(OrderEntity order) {
        return order.getOrderItems().stream().map(item -> GHTKProductRequest
                .builder()
                .name(item.getProductName())
                .weight(item.getWeight())
                .quantity(item.getQuantity())
                .build()).toList();
    }

}
