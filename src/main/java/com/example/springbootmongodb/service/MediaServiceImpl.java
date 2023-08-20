package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.exception.InternalErrorException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.*;
import com.example.springbootmongodb.repository.ProductCreationProcessRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.springbootmongodb.config.S3Configuration.*;
import static com.example.springbootmongodb.config.S3Configuration.DEFAULT_BUCKET;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {
    private static final Map<String, Boolean> supportedImageTypes = new HashMap<>();
    private final ProductCreationProcessRepository processRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    @Lazy
    private ProductService productService;

    private final S3Client s3Client;
    static {
        supportedImageTypes.put(MediaType.IMAGE_JPEG_VALUE, true);
        supportedImageTypes.put(MediaType.IMAGE_PNG_VALUE, true);
    }

    public static final String INVALID_IMAGE_TYPE_ERROR_MESSAGE = "Invalid image type";
    public static final String UNSUPPORTED_IMAGE_TYPE_ERROR_MESSAGE = "Unsupported image types";
    public static final String FAILED_TO_UPLOAD_IMAGE_ERROR_MESSAGE = "Encountered error during uploading image, please try again";
    public static final String IMAGE_MAXIMUM_LENGTH_EXCEEDED_ERROR_MESSAGE = "The provided image size has exceeded the maximum allowed size of " + MAX_IMAGE_SIZE_KB;
    public static final int MAXIMUM_REVIEW_IMAGES = 3;
    public static final String MAXIMUM_REVIEW_IMAGES_EXCEEDED_ERROR_MESSAGE = "Only " + MAXIMUM_REVIEW_IMAGES + " images are allowed";

    @Override
    @Transactional
    public TemporaryImage uploadImage(String processId, MultipartFile image) {
        log.info("Performing MediaService uploadImage");
        validateImage(image);
        String imageKey = UUID.randomUUID() + "." + image.getContentType().substring(6);
        PutObjectRequest request = PutObjectRequest
                .builder()
                .bucket(DEFAULT_BUCKET)
                .key(imageKey)
                .tagging(TEMPORARY_TAG)
                .contentType(image.getContentType())
                .build();
        try {
            s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(image.getInputStream(), image.getInputStream().available()));
        } catch (IOException e) {
            throw new InternalErrorException(FAILED_TO_UPLOAD_IMAGE_ERROR_MESSAGE);
        }
        Optional<ProductSavingProcessEntity> processOpt = Optional.empty();
        ProductSavingProcessEntity process;
        if (StringUtils.isNotEmpty(processId)) {
            processOpt = processRepository.findById(processId);
        }
        if (processOpt.isPresent()) {
            process = processOpt.get();
            process.getImageKeys().add(imageKey);
            process = saveProcess(process);
        }
        else {
            process = ProductSavingProcessEntity.builder().imageKeys(Collections.singleton(imageKey)).build();
            process = insertProcess(process);
        }
        String imageUrl = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build()).toString();
        return TemporaryImage.builder().url(imageUrl).processId(process.getId()).build();
    }

    private void validateImage(MultipartFile image) {
        if (StringUtils.isEmpty(image.getContentType())) {
            throw new InvalidDataException(INVALID_IMAGE_TYPE_ERROR_MESSAGE);
        }
        if (!supportedImageTypes.containsKey(image.getContentType())) {
            throw new InvalidDataException(UNSUPPORTED_IMAGE_TYPE_ERROR_MESSAGE);
        }
        long imageKbSize = image.getSize()/1024;
        if (imageKbSize > MAX_IMAGE_SIZE_KB) {
            throw new InvalidDataException(IMAGE_MAXIMUM_LENGTH_EXCEEDED_ERROR_MESSAGE + " : " + image.getSize());
        }
    }

    private void validateImages(List<MultipartFile> images) {
        for (MultipartFile image : images) {
            validateImage(image);
        }
    }

    @Override
    public TemporaryImages uploadImages(String processId, List<MultipartFile> images) {
        log.info("Performing MediaService uploadImages");
        validateImages(images);
        Set<String> imageKeys = new HashSet<>();
        List<String> imageUrls = new ArrayList<>();
        ProductSavingProcessEntity process = ProductSavingProcessEntity.builder().build();
        if (StringUtils.isNotEmpty(processId)) {
            process = processRepository.findById(processId)
                    .orElseThrow(() -> new ItemNotFoundException(String.format("Process with id [%s] is not found", processId)));
        }
        if (process.getImageKeys().size() + images.size()> MAXIMUM_REVIEW_IMAGES) {
            throw new InvalidDataException(MAXIMUM_REVIEW_IMAGES_EXCEEDED_ERROR_MESSAGE);
        }
        for (MultipartFile image : images) {
            String imageKey = UUID.randomUUID() + "." + image.getContentType().substring(6);
            imageKeys.add(imageKey);
            PutObjectRequest request = PutObjectRequest
                    .builder()
                    .bucket(DEFAULT_BUCKET)
                    .key(imageKey)
                    .tagging(TEMPORARY_TAG)
                    .contentType(image.getContentType())
                    .build();
            try {
                s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(image.getInputStream(), image.getInputStream().available()));
            } catch (IOException e) {
                throw new InternalErrorException(FAILED_TO_UPLOAD_IMAGE_ERROR_MESSAGE);
            }
            String imageUrl = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build()).toString();
            imageUrls.add(imageUrl);
        }
        if (StringUtils.isNotEmpty(process.getId())) {
            process.getImageKeys().addAll(imageKeys);
            process = saveProcess(process);
        }
        else {
            process.setImageKeys(imageKeys);
            process = insertProcess(process);
        }
        return TemporaryImages.builder().processId(process.getId()).urls(imageUrls).build();
    }

    @Override
    public void removeTemporaryTag(String imageName) {
        log.info("Performing MediaService unsetTemporaryTag");
        DeleteObjectTaggingRequest request = DeleteObjectTaggingRequest
                .builder()
                .bucket(DEFAULT_BUCKET)
                .key(imageName)
                .build();
        s3Client.deleteObjectTagging(request);
    }

    @Override
    public void persistCreatingProductImages(ProductRequest request) {
        log.info("Performing MediaService persistCreatingProductImagesAsync");
        if (StringUtils.isEmpty(request.getImageUrl())) {
            throw new InvalidDataException("Product should have at least 1 image");
        }
        if (StringUtils.isEmpty(request.getProcessId())) {
            throw new InvalidDataException("Process Id should be specified");
        }
        //validate the amount of image urls
        Set<String> imageRequests = new HashSet<>();
        int imageUrlsCount = 0;
        ProductVariationRequest primaryVariationRequest = request.getVariations().get(0);
        for (VariationOptionRequest optionRequest : primaryVariationRequest.getOptions()) {
            if (StringUtils.isNotEmpty(optionRequest.getImageUrl())) {
                imageUrlsCount++;
                imageRequests.add(parseImageKeyFromUrl(optionRequest.getImageUrl()));
            }
        }
        if (imageUrlsCount != 0 && imageUrlsCount < primaryVariationRequest.getOptions().size()) {
            throw new InvalidDataException("You must either add all classified images or leave them all empty");
        }
        imageRequests.add(parseImageKeyFromUrl(request.getImageUrl()));
        ProductSavingProcessEntity process;
        try {
            process = findProcessById(request.getProcessId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException("There is no current process for this product");
        }
        //take out common image urls
        process.getImageKeys().retainAll(imageRequests);
        List<CompletableFuture<Void>> persistImageFutures = new ArrayList<>();
        //and remove their temporary tag
        for (String imageKey : process.getImageKeys()) {
            DeleteObjectTaggingRequest deleteObjectTaggingRequest = DeleteObjectTaggingRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build();
            persistImageFutures.add(CompletableFuture.supplyAsync(() -> {
                s3Client.deleteObjectTagging(deleteObjectTaggingRequest);
                return null;
            }, taskExecutor));
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(persistImageFutures.toArray(new CompletableFuture[persistImageFutures.size()]));
        combinedFutures.join();
        processRepository.deleteById(request.getProcessId());
    }

    @Override
    public void persistUpdatingProductImages(ProductRequest request, ProductEntity oldProduct) {
        log.info("Performing MediaService persistUpdatingProductImagesAsync");
        List<CompletableFuture<Void>> updateImageFutures = new ArrayList<>();
        if (StringUtils.isEmpty(request.getImageUrl())) {
            throw new InvalidDataException("Product should have at least 1 image");
        }

        //validate the amount of images
        Set<String> imageUrlRequests = new HashSet<>();
        int imageUrlsCount = 0;
        ProductVariationRequest primaryVariationRequest = request.getVariations().get(0);
        for (VariationOptionRequest optionRequest : primaryVariationRequest.getOptions()) {
            if (StringUtils.isNotEmpty(optionRequest.getImageUrl())) {
                imageUrlsCount++;
                imageUrlRequests.add(optionRequest.getImageUrl());
            }
        }
        if (imageUrlsCount != 0 && imageUrlsCount < primaryVariationRequest.getOptions().size()) {
            throw new InvalidDataException("You must either add all classified images or leave them all empty");
        }
        imageUrlRequests.add(request.getImageUrl());
        Set<String> imageUrlRequestsCopy = new HashSet<>(imageUrlRequests);

        //retrieve new images from product request
        //sort variations by index, then take the first variation as the primary one
        oldProduct.getVariations().sort(new ProductVariationServiceImpl.ProductVariationComparator());
        ProductVariationEntity primaryVariation = oldProduct.getVariations().get(0);

        //sort options by index to match with request's options
        primaryVariation.getOptions().sort(new VariationOptionServiceImpl.VariationOptionComparator());
        Set<String> existingImageUrls = primaryVariation.getOptions().stream().map(VariationOptionEntity::getImageUrl).collect(Collectors.toSet());
        existingImageUrls.add(oldProduct.getImageUrl());

        //new images that is not existing in current product
        imageUrlRequests.removeAll(existingImageUrls);
        if (CollectionUtils.isNotEmpty(imageUrlRequests)) {

            //validate process id
            ProductSavingProcessEntity process;
            try {
                process = findProcessById(request.getProcessId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException("There is no current process for this product");
            }
            //retrieve images to persist
            process.getImageKeys().retainAll(imageUrlRequests.stream().map(this::parseImageKeyFromUrl).collect(Collectors.toSet()));

            //remove temporary tag
            for (String imageKey : process.getImageKeys()) {
                DeleteObjectTaggingRequest deleteObjectTaggingRequest = DeleteObjectTaggingRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build();
                updateImageFutures.add(CompletableFuture.supplyAsync(() -> {
                    s3Client.deleteObjectTagging(deleteObjectTaggingRequest);
                    return null;
                }, taskExecutor));
            }
            processRepository.deleteById(request.getProcessId());
        }

        //retrieve images to delete
        existingImageUrls.removeAll(imageUrlRequestsCopy);
        if (CollectionUtils.isEmpty(existingImageUrls)) {
            return;
        }
        //delete images
        for (String imageUrl : existingImageUrls) {
            if (StringUtils.isNotEmpty(imageUrl)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(DEFAULT_BUCKET).key(parseImageKeyFromUrl(imageUrl)).build();
                updateImageFutures.add(CompletableFuture.supplyAsync(() -> {
                    s3Client.deleteObject(deleteObjectRequest);
                    return null;
                }, taskExecutor));
            }
        }

        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(updateImageFutures.toArray(new CompletableFuture[updateImageFutures.size()]));
        combinedFutures.join();
    }

    @Override
    public void persistCreatingReviewImages(ReviewRequest request) {
        log.info("Performing MediaService persistCreatingReviewImages");
        if (CollectionUtils.isEmpty(request.getImageUrls())) {
            return;
        }
        ProductSavingProcessEntity process;
        try {
            process = findProcessById(request.getProcessId());
        } catch (ItemNotFoundException exception) {
            throw new UnprocessableContentException("There is no current process for this product");
        }

        //take out common image urls
        process.getImageKeys().retainAll(request.getImageUrls().stream().map(this::parseImageKeyFromUrl).toList());
        List<CompletableFuture<Void>> persistImageFutures = new ArrayList<>();

        //and remove their temporary tag
        for (String imageKey : process.getImageKeys()) {
            DeleteObjectTaggingRequest deleteObjectTaggingRequest = DeleteObjectTaggingRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build();
            persistImageFutures.add(CompletableFuture.supplyAsync(() -> {
                s3Client.deleteObjectTagging(deleteObjectTaggingRequest);
                return null;
            }, taskExecutor));
        }
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(persistImageFutures.toArray(new CompletableFuture[persistImageFutures.size()]));
        combinedFutures.join();
        processRepository.deleteById(request.getProcessId());

    }

    @Override
    public void persistUpdatingReviewImages(ReviewRequest request, ReviewEntity oldReview) {
        log.info("Performing MediaService persistUpdatingReviewImages");
        List<CompletableFuture<Void>> updateImageFutures = new ArrayList<>();
        List<String> existingImageUrls = new ArrayList<>(oldReview.getImageUrls());
        List<String> requestImageUrls = new ArrayList<>(request.getImageUrls());

        //persist new images
        if (StringUtils.isNotEmpty(request.getProcessId())) {
            ProductSavingProcessEntity process;
            try {
                process = findProcessById(request.getProcessId());
            } catch (ItemNotFoundException exception) {
                throw new UnprocessableContentException("There is no current process for this product");
            }

            List<String> processImageKeys = new ArrayList<>(process.getImageKeys());
            processImageKeys.retainAll(requestImageUrls.stream().map(this::parseImageKeyFromUrl).toList());

            //remove temporary tag
            for (String imageKey : process.getImageKeys()) {
                DeleteObjectTaggingRequest deleteObjectTaggingRequest = DeleteObjectTaggingRequest.builder().bucket(DEFAULT_BUCKET).key(imageKey).build();
                updateImageFutures.add(CompletableFuture.supplyAsync(() -> {
                    s3Client.deleteObjectTagging(deleteObjectTaggingRequest);
                    return null;
                }, taskExecutor));
            }

            processRepository.deleteById(process.getId());
        }

        //retrieve non-existent images
        existingImageUrls.removeAll(requestImageUrls);

        //delete images
        for (String imageUrl : existingImageUrls) {
            if (StringUtils.isNotEmpty(imageUrl)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(DEFAULT_BUCKET).key(parseImageKeyFromUrl(imageUrl)).build();
                updateImageFutures.add(CompletableFuture.supplyAsync(() -> {
                    s3Client.deleteObject(deleteObjectRequest);
                    return null;
                }, taskExecutor));
            }
        }

        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(updateImageFutures.toArray(new CompletableFuture[updateImageFutures.size()]));
        combinedFutures.join();
    }

    @Override
    public ProductSavingProcessEntity findProcessById(String processId) {
        log.info("Performing MediaService findProcessById");
        if (StringUtils.isEmpty(processId)) {
            throw new InvalidDataException("Process Id should be specified");
        }
        return processRepository
                .findById(processId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Process with id [%s] is not found", processId)));
    }

    private ProductSavingProcessEntity saveProcess(ProductSavingProcessEntity entity) {
        if (entity.getCreatedAt() == null) {
            Optional<ProductSavingProcessEntity> existingEntity = processRepository.findById(entity.getId());
            entity.setCreatedAt(existingEntity.map(ProductSavingProcessEntity::getCreatedAt).orElse(null));
        }
        entity = processRepository.save(entity);
        return entity;
    }

    private ProductSavingProcessEntity insertProcess(ProductSavingProcessEntity entity) {
        return processRepository.insert(entity);
    }

    private String parseImageKeyFromUrl(String imageUrl) {
        String[] elements = imageUrl.split("/");
        return elements[elements.length - 1];
    }

}
