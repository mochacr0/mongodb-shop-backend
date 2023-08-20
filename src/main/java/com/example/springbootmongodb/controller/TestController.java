package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.common.data.TemporaryImage;
import com.example.springbootmongodb.common.data.VariationOption;
import com.example.springbootmongodb.common.data.mapper.OrderMapper;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.data.mapper.VariationOptionMapper;
import com.example.springbootmongodb.common.data.payment.PaymentStatus;
import com.example.springbootmongodb.common.data.payment.momo.*;
import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKAbstractResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.config.GHTKCredentials;
import com.example.springbootmongodb.config.MomoCredentials;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.ProductItemRepository;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
import com.example.springbootmongodb.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.springbootmongodb.common.data.shipment.ghtk.GHTKEndpoints.GHTK_CANCEL_SHIPMENT_ROUTE_PATTERN;
import static com.example.springbootmongodb.config.S3Configuration.DEFAULT_BUCKET;
import static com.example.springbootmongodb.config.S3Configuration.TEMPORARY_TAG;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@RestController()
@RequestMapping(value = "/test")
@RequiredArgsConstructor
public class TestController {
    private final ProductRepository productRepository;
    private final ProductItemRepository itemRepository;
    private final VariationOptionRepository optionRepository;
    private final MongoTemplate mongoTemplate;
    private final VariationOptionMapper optionMapper;
    private final S3Client s3Client;
    private final MediaService mediaService;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final MomoCredentials momoCredentials;
    private final OrderService orderService;
    private final GHTKCredentials ghtkCredentials;
    private final HttpClient httpClient;
    private final OrderMapper orderMapper;
    private final ReturnService returnService;
    private final MailService mailService;
    private final ReviewService reviewService;

    private final String GHTK_API_TOKEN_KEY = "641cd4f20fecc058dc822b5163ceb3abb797431f";
//    @GetMapping(value = "/test")
//    ProductEntity test() {
//        ProductEntity product = ProductEntity.builder().name("product").build();
//        product = productRepository.save(product);
//        ProductItemEntity productItem = ProductItemEntity.builder().product(product).build();
//        productItem = itemRepository.save(productItem);
//        Optional<ProductItemEntity> productItemOpt = itemRepository.findById(productItem.getId());
//        if (productItemOpt.isPresent()) {
//            productItem = productItemOpt.get();
//        }
//        return productRepository.findById(product.getId()).get();
//    }

    @GetMapping(value = "/2")
    String test2() {
        ProductEntity product = new ProductEntity();
        product.setName("product");
        product = productRepository.save(product);
        ProductItemEntity item = new ProductItemEntity();
        item.setProduct(product);
        itemRepository.save(item);
        item = itemRepository.findById(item.getId()).get();
        product.getItems().add(item);
        ProductEntity savedProduct = productRepository.save(product);
//        product = productRepository.findById(product.getId()).get();
        return null;
    }

    @GetMapping(value = "/3")
    ProductItemEntity test3() {
//        ProductItemEntity item = itemRepository.save(new ProductItemEntity());
//        VariationOptionEntity option = optionRepository.save(new VariationOptionEntity());
//        item.getOptions().add(option);
//        ProductItemEntity savedItem = itemRepository.save(item);
//        option.getItems().add(item);
//        VariationOptionEntity savedOption = optionRepository.save(option);
//        ProductItemEntity retrievedItem = itemRepository.findById(savedItem.getId()).get();
//        VariationOptionEntity retrievedOption = optionRepository.findById(savedOption.getId()).get();
//        itemRepository.delete(retrievedItem);
//        retrievedOption = optionRepository.findById(savedOption.getId()).get();
        return null;
    }

    @PostMapping(value = "/4")
    int test4(@RequestBody List<String> productIds) {
        return itemRepository.bulkDelete(productIds);
    }

    @GetMapping(value = "/5")
    VariationOption test5(@RequestParam String variationId) {
        return optionMapper.fromEntity(mongoTemplate.findOne(Query.query(where("variationId").in(Collections.singleton(variationId))), VariationOptionEntity.class));
    }

    @PostMapping(value = "/6")
    String test6(@RequestParam(name = "image") MultipartFile image) throws IOException {
//        ObjectMetadata metadata = ObjectMetadata
//                .builder()
//                .contentType(image.getContentType())
//                .build();
        //TODO: validate image size
        //TODO: validate image type
        //strip out image extension: "image/<extension>"
        String imageKey = UUID.randomUUID() + "." + image.getContentType().substring(6);
        PutObjectRequest request = PutObjectRequest
                .builder()
                .bucket(DEFAULT_BUCKET)
                .key(imageKey)
                .tagging(TEMPORARY_TAG)
                .contentType(image.getContentType())
                .build();
        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(image.getInputStream(), image.getInputStream().available()));
        return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build()).toString();

    }

    @PostMapping(value = "/7")
    TemporaryImage test7(@RequestParam(required = false) String processId,
                         @RequestParam(name = "image") MultipartFile image) {
        return mediaService.uploadImage(processId, image);
    }

    @PostMapping(value = "/8")
    void test8() {
        DeleteObjectTaggingRequest request = DeleteObjectTaggingRequest
                .builder()
                .bucket(DEFAULT_BUCKET)
                .key("031fd984-fb86-4168-b0a9-7ababc50176e.jpeg")
                .build();
        s3Client.deleteObjectTagging(request);
    }

    @GetMapping(value = "/9")
    ProductEntity test9() throws JsonProcessingException {
        ProductEntity product = productService.findById("64afb1600a1ca72790e3be99");
        ProductEntity productCopy = objectMapper.readValue(objectMapper.writeValueAsString(product), ProductEntity.class);
        return productCopy;
    }

    @GetMapping(value = "/10")
    CartEntity test10() {
        return cartService.create("");
    }

    @GetMapping(value = "/11")
    MomoCaptureWalletResponse test11() {
        return null;
    }

    @GetMapping(value = "/momo/callback")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void momoCallback(@RequestParam("partnerCode") String partnerCode,
                      @RequestParam("orderId") String orderId,
                      @RequestParam("requestId") String requestId,
                      @RequestParam("amount") long amount,
                      @RequestParam("orderInfo") String orderInfo,
                      @RequestParam("orderType") String orderType,
                      @RequestParam("transId") String transId,
                      @RequestParam("resultCode") int resultCode,
                      @RequestParam("message") String message,
                      @RequestParam("payType") String payType,
                      @RequestParam("extraData") String extraData,
                      @RequestParam("signature") String signature,
                      HttpServletRequest httpServletRequest) {
        MomoIpnCallbackResponse request = MomoIpnCallbackResponse.builder()
                .partnerCode(partnerCode)
                .orderId(orderId)
                .requestId(requestId)
                .amount(amount)
                .orderInfo(orderInfo)
                .orderType(orderType)
                .transId(transId)
                .resultCode(resultCode)
                .message(message)
                .payType(payType)
                .extraData(extraData)
                .signature(signature)
                .build();
        paymentService.processIpnRequest(request, httpServletRequest);
    }

    @GetMapping(value = "/12")
    MomoRefundResponse test12(@RequestParam String orderId) {
        HmacUtils hmacUtils1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String value = "accessKey=F8BBA842ECF85&amount=1000&extraData=Thanh toán qua ví Momo&ipnUrl=http://localhost:5000/orders/momo/callback&orderId=64c25081f3ce0e2e30059bea&orderInfo=Thanh toán qua ví Momo&partnerCode=MOMO&redirectUrl=http://localhost:5000/orders/momo/callback&requestId=5bd266c2-73c1-490a-b8af-2713a66cde4a&requestType=captureWallet";
        String hash1 = hmacUtils1.hmacHex(value);
        HmacUtils hmacUtils2 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String hash2 = hmacUtils2.hmacHex(value);
        log.info(hash1);
        log.info(hash2);
        return null;
    }

    @GetMapping(value = "/13")
    void test13(@RequestParam(required = false) int value) {
        log.info("-------------------------------------Value: " + value);
    }

//    @GetMapping(value = "/14")
//    Payment test14(@RequestParam String orderId) {
//        return paymentService.queryPaymentStatus(orderId);
//    }

    @GetMapping(value = "/15")
    void test15() {
        orderService.cancelExpiredOrders();
    }

    @GetMapping(value = "/16")
    GHTKAbstractResponse test16(@RequestParam(required = true) String labelId) {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(String.format(GHTK_CANCEL_SHIPMENT_ROUTE_PATTERN, labelId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Token", ghtkCredentials.getApiToken())
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
        return cancelResponse;
    }

    @GetMapping(value = "/17")
    Payment test17(@RequestParam String orderId,
                @RequestParam Integer amount) {
        OrderEntity order = orderService.findById(orderId);
        Payment payment = order.getPayment();
        String requestBody = buildRefundRequest(payment, amount);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(MomoEndpoints.MOMO_REFUND_ROUTE))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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
        MomoRefundResponse response;
        try {
            response = objectMapper.readValue(httpResponse.body(), MomoRefundResponse.class);
        } catch (JsonProcessingException exception) {
            throw new InternalErrorException(exception.getMessage());
        }
        if (response.getResultCode() != 0 && response.getResultCode() != 9000) {
            throw new UnprocessableContentException(response.getMessage());
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setDescription(response.getMessage());
        return payment;
    }

    public String buildRefundRequest(Payment payment, int amount) {
        String requestId = UUID.randomUUID().toString();
        String testOrderId = UUID.randomUUID().toString();
        String valueToDigest = "accessKey=" + momoCredentials.getAccessKey() +
                "&amount=" + amount +
                "&description=" + "Test refund" +
                "&orderId=" + testOrderId +
                "&partnerCode=" + momoCredentials.getPartnerCode() +
                "&requestId=" + requestId +
                "&transId=" + payment.getTransId();
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, momoCredentials.getSecretKey());
        String signedSignature = hmacUtils.hmacHex(valueToDigest);
        MomoRefundRequest refundRequest = MomoRefundRequest
                .builder()
                .orderId(testOrderId)
                .partnerCode(momoCredentials.getPartnerCode())
                .amount(amount)
                .requestId(requestId)
                .description("Test refund")
                .lang(MomoResponseLanguage.VI.getValue())
                .transId(payment.getTransId())
                .signature(signedSignature)
                .build();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(refundRequest);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Serializing failed");
        }
        return requestBody;
    }

    @GetMapping("/completeOrder/{orderId}")
    Order testCaseOrderCompleted(@PathVariable String orderId) {
        OrderEntity order = orderService.findById(orderId);
        GHTKUpdateStatusRequest request = GHTKUpdateStatusRequest
                .builder()
                .shipmentId(order.getShipment().getId())
                .partnerId(order.getId())
                .weight(0.2f)
                .fee(45000)
                .returnPartPackage(0)
                .statusId(Integer.valueOf(ShipmentState.ACCEPTED.getCode()))
                .build();
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKING_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKED_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERING.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERED.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        return orderMapper.fromEntity(orderService.confirmDelivered(order.getId()));
    }

    @GetMapping("/waitConfirm/{orderId}")
    Order waitConfirm(@PathVariable String orderId) {
        OrderEntity order = orderService.findById(orderId);
        GHTKUpdateStatusRequest request = GHTKUpdateStatusRequest
                .builder()
                .shipmentId(order.getShipment().getId())
                .partnerId(order.getId())
                .weight(0.2f)
                .fee(45000)
                .returnPartPackage(0)
                .statusId(Integer.valueOf(ShipmentState.ACCEPTED.getCode()))
                .build();
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKING_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.PICKED_UP.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERING.getCode()));
        orderService.processShipmentStatusUpdateRequest(request);
        request.setStatusId(Integer.valueOf(ShipmentState.DELIVERED.getCode()));
        return orderMapper.fromEntity(orderService.processShipmentStatusUpdateRequest(request));
    }

    @GetMapping("/18")
    OrderReturnEntity test18() {
        OrderReturnEntity orderReturn = OrderReturnEntity.builder().build();
        return orderReturn;
    }

    @GetMapping("/19")
    void test19() {
        returnService.acceptExpiredReturnRequests();
    }

    @GetMapping("/20")
    void test20() {
        mailService.sendRefundProcessingMail("nthai2001cr@gmail.com");
    }

    @GetMapping("/21")
    void test21() {
        mailService.sendRefundConfirmationEmail("nthai2001cr@gmail.com", 50000);
    }

    @GetMapping("/22")
    void test22() {
        mailService.sendAcceptedReturnEmail("nthai2001cr@gmail.com");
    }

    @GetMapping("/23")
    int test23(@RequestParam String productId) {
        return reviewService.countProductReviews(productId);
    }

    @GetMapping("/24")
    double test24(@RequestParam String productId) {
        return reviewService.calculateProductRatings(productId);
    }
}
