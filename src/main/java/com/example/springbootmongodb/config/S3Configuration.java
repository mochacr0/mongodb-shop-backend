package com.example.springbootmongodb.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class S3Configuration {
    public static final String DEFAULT_BUCKET = "mochaimages";
    public static final String TEMPORARY_TAG = "temporary";
    public static final long MAX_IMAGE_SIZE_KB = 1024;
}
