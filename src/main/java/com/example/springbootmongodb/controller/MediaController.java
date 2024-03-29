package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.TemporaryImage;
import com.example.springbootmongodb.common.data.TemporaryImages;
import com.example.springbootmongodb.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.springbootmongodb.controller.ControllerConstants.MEDIA_UPLOAD_IMAGE_ROUTE;
import static com.example.springbootmongodb.controller.ControllerConstants.MEDIA_UPLOAD_MULTIPLE_IMAGES_ROUTE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Media")
public class MediaController {
    private final MediaService mediaService;

    @PostMapping(value = MEDIA_UPLOAD_IMAGE_ROUTE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ảnh sản phẩm")
    TemporaryImage uploadImage(@RequestParam(required = false) String processId,
                               @RequestParam(name = "image") MultipartFile image) {
        return mediaService.uploadImage(processId, image);
    }

    @PostMapping(value = MEDIA_UPLOAD_MULTIPLE_IMAGES_ROUTE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ảnh review sản phẩm")
    TemporaryImages uploadImages(@RequestParam(required = false) String processId,
                                 @RequestParam(name = "multiImages") List<MultipartFile> multiImages) {
        return mediaService.uploadImages(processId, multiImages);
    }
}
