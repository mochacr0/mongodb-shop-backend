package com.example.springbootmongodb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.example.springbootmongodb.model.ModelConstants.CREATED_AT_FIELD;

@Document(value = "productSavingProcesses")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
public class ProductSavingProcessEntity {
    @Id
    private String id;
    //Will be expired after a day minus 5 minutes
    @Indexed(name = CREATED_AT_FIELD, expireAfterSeconds = 86100)
    @CreatedDate
    private LocalDateTime createdAt;
    private Set<String> imageKeys = new HashSet<>();
}
