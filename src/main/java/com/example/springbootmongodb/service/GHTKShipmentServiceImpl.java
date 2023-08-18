package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.AbstractItem;
import com.example.springbootmongodb.common.HasAddress;
import com.example.springbootmongodb.common.data.Packable;
import com.example.springbootmongodb.common.data.mapper.ShopAddressMapper;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import com.example.springbootmongodb.common.data.shipment.OrderType;
import com.example.springbootmongodb.common.data.shipment.ShipmentAddress;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import com.example.springbootmongodb.common.data.shipment.ghtk.*;
import com.example.springbootmongodb.common.validator.Length;
import com.example.springbootmongodb.config.GHTKCredentials;
import com.example.springbootmongodb.exception.*;
import com.example.springbootmongodb.model.ShipmentEntity;
import com.example.springbootmongodb.model.ShopAddressEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import com.example.springbootmongodb.repository.ShipmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
public class GHTKShipmentServiceImpl extends DataBaseService<ShipmentEntity> implements ShipmentService {
    private final HttpClient httpClient;
    private final GHTKCredentials ghtkCredentials;
    private final ObjectMapper objectMapper;
    private final ShopAddressService shopAddressService;
    private final UserAddressService userAddressService;
    private final ShopAddressMapper shopAddressMapper;
    private final ShipmentRepository shipmentRepository;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Autowired
    @Lazy
    private ReturnService returnService;

    private static final String TOKEN_HEADER_NAME = "Token";

    @Override
    public MongoRepository<ShipmentEntity, String> getRepository() {
        return shipmentRepository;
    }

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
    public ShipmentEntity findById(String shipmentId) {
        log.info("Performing ShipmentService findById");
        if (StringUtils.isNotEmpty(shipmentId)) {
            throw new InvalidDataException("Shipment Id should be specified");
        }
        return shipmentRepository.findById(shipmentId).orElseThrow(() -> new ItemNotFoundException(String.format("Shipment with id [%s] is not found", shipmentId)));
    }

    @Override
    public ShipmentEntity place(ShipmentEntity shipment, String orderId, int subTotal, int cod, boolean isFreeShip, List<? extends AbstractItem> items, ShipmentRequest request) {
        log.info("Performing ShipmentService placeOrder");
        if (shipment == null) {
            throw new UnprocessableContentException("Shipment has not been initiated");
        }
        if (StringUtils.isNotEmpty(request.getReturnAddressId())) {
            ShopAddressEntity returnAddress;
            try {
                returnAddress = shopAddressService.findById(request.getReturnAddressId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException(exception.getMessage());
            }
            shipment.setReturnAddress(fromHasAddressToShipmentAddress(returnAddress));
        }
        String requestBody = buildCreateShipmentRequestBody(shipment, orderId, subTotal, cod, isFreeShip, items, request);
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
        GHTKOrderResponse orderResponse = shipmentResponse.getOrder();
        shipment.setId(orderResponse.getLabel());
        shipment.setDeliveryFee(orderResponse.getFee());
        shipment.setEstimatedPickTime(orderResponse.getEstimatedPickTime());
        shipment.setEstimatedDeliverTime(orderResponse.getEstimatedDeliverTime());
        shipment.setEstimatedPickTime(orderResponse.getEstimatedPickTime());
        shipment.setInsuranceFee(orderResponse.getInsuranceFee());
        ShipmentStatus succededShipmentStatus = ShipmentStatus
                .builder()
                .description("Đang chờ tiếp nhận")
                .state(ShipmentState.parseFromInt(shipmentResponse.getOrder().getStatusId()))
                .build();
        shipment.getStatusHistory().add(succededShipmentStatus);
        return save(shipment);
    }

    @Override
    public ShipmentEntity cancel(ShipmentEntity shipment) {
        log.info("Performing ShipmentService cancel");
        if (shipment == null) {
            throw new UnprocessableContentException("Shipment is not found");
        }
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
        return save(shipment);
    }

    @Override
    public ShipmentEntity initiate(String orderId, HasAddress pickUpAddress, HasAddress deliverAddress, OrderType orderType) {
        log.info("Performing ShipmentService initiate");
        return super.insert(ShipmentEntity
                .builder()
                .pickUpAddress(fromHasAddressToShipmentAddress(pickUpAddress))
                .deliverAddress(fromHasAddressToShipmentAddress(deliverAddress))
                .orderType(orderType)
                .build());
    }

    @Override
    public ShipmentEntity save(ShipmentEntity shipment) {
        log.info("Performing ShipmentService save");
        return super.save(shipment);
    }

    @Override
    public Packable processShipmentStatusUpdateRequest(GHTKUpdateStatusRequest request) {
        log.info("Performing ShipmentService processShipmentStatusUpdateRequest");
        ShipmentEntity shipment;
        try {
            shipment = findById(request.getShipmentId());
        } catch (ItemNotFoundException exception) {
            throw new UnavailableServiceException(exception.getMessage());
        }
        Packable packable = null;
        if (shipment.getOrderType() == OrderType.ORDER) {
            packable = orderService.processShipmentStatusUpdateRequest(request);
        }
        else if(shipment.getOrderType() == OrderType.RETURN ){
            packable = returnService.processShipmentStatusUpdateRequest(request);
        }
        return packable;
    }

    private ShipmentAddress fromHasAddressToShipmentAddress(HasAddress address) {
        return ShipmentAddress
                .builder()
                .name(address.getName())
                .phoneNumber(address.getPhoneNumber())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .hamlet(address.getHamlet())
                .street(address.getStreet())
                .addressDetails(address.getAddressDetails())
                .build();
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

    private String buildCreateShipmentRequestBody(ShipmentEntity shipment, String orderId, int subTotal, int cod, boolean isFreeShip, List<? extends AbstractItem> items, ShipmentRequest request) {
        GHTKCreateShipmentRequest createShipmentRequest = GHTKCreateShipmentRequest
                .builder()
                .order(buildOrderShipmentRequest(shipment, orderId, subTotal, cod, isFreeShip, request))
                .products(buildProductShipmentRequest(items))
                .build();
        try {
            return objectMapper.writeValueAsString(createShipmentRequest);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
    }

    private GHTKOrderRequest buildOrderShipmentRequest(ShipmentEntity shipment, String orderId, int subTotal, int cod, boolean isFreeShip, ShipmentRequest request) {
        GHTKPickOption pickOption = GHTKPickOption.parseFromString(request.getPickOption());
        GHTKWorkShiftOption pickWorkShiftOption = GHTKWorkShiftOption.parseFromString(request.getPickWorkShipOption());
        GHTKWorkShiftOption deliverWorkShiftOption = GHTKWorkShiftOption.parseFromString(request.getDeliverWorkShipOption());
        ShipmentAddress pickUpAddress = shipment.getPickUpAddress();
        ShipmentAddress deliverAddress = shipment.getDeliverAddress();
        GHTKOrderRequest orderRequest = GHTKOrderRequest
                .builder()
                .orderId(orderId)
                .pickOption(pickOption.getValue())
                .pickName("Shop")
                .pickMoney(cod)
                .pickAddress(pickUpAddress.getAddressDetails())
                .pickProvince(pickUpAddress.getProvince())
                .pickDistrict(pickUpAddress.getDistrict())
                .pickWard(pickUpAddress.getWard())
                .pickStreet(pickUpAddress.getStreet())
                .pickPhoneNumber(pickUpAddress.getPhoneNumber())
                .note(request.getNote())
                .name(deliverAddress.getName())
                .address(deliverAddress.getAddressDetails())
                .province(deliverAddress.getProvince())
                .district(deliverAddress.getDistrict())
                .ward(deliverAddress.getWard())
                .street(deliverAddress.getStreet())
                .hamlet(StringUtils.isNotEmpty(deliverAddress.getHamlet()) ? deliverAddress.getHamlet() : "Khác")
                .phoneNumber(deliverAddress.getPhoneNumber())
                .note(request.getNote())
                .email("")
                .isFreeShip(BooleanUtils.toInteger(isFreeShip))
                .value(subTotal)
                .pickWorkShift(pickWorkShiftOption.getCode())
                .deliverWorkShift(deliverWorkShiftOption.getCode())
                .transport(GHTKTransportMethod.ROAD.getValue())
                .build();
        ShipmentAddress returnAddress = shipment.getReturnAddress();
        if (returnAddress != null) {
            orderRequest.setReturnAddress(returnAddress.getAddressDetails());
            orderRequest.setReturnName("Shop");
            orderRequest.setReturnEmail("");
            orderRequest.setReturnProvince(returnAddress.getProvince());
            orderRequest.setReturnDistrict(returnAddress.getDistrict());
            orderRequest.setReturnWard(returnAddress.getWard());
            orderRequest.setReturnStreet(returnAddress.getStreet());
            orderRequest.setReturnPhoneNumber(returnAddress.getPhoneNumber());
        }
        return orderRequest;
    }

    private List<GHTKProductRequest> buildProductShipmentRequest(List<? extends AbstractItem> items) {
        return items.stream().map(item -> GHTKProductRequest
                .builder()
                .name(item.getProductName())
                .weight(item.getWeight())
                .quantity(item.getQuantity())
                .build()).toList();
    }
}
