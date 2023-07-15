package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.TemporaryImage;
import com.example.springbootmongodb.common.data.VariationOption;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.common.data.mapper.VariationOptionMapper;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductItemRepository;
import com.example.springbootmongodb.repository.ProductRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
import com.example.springbootmongodb.service.MediaService;
import com.example.springbootmongodb.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.InternalException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import jakarta.xml.bind.annotation.XmlType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}
