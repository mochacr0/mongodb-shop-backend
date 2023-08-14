package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.ReturnItem;
import com.example.springbootmongodb.common.data.ReturnOffer;
import com.example.springbootmongodb.common.data.ReturnReason;
import com.example.springbootmongodb.common.data.ReturnStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.ORDER_RETURN_COLLECTION_NAME;

@Document(collection = ORDER_RETURN_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class OrderReturnEntity extends AbstractEntity {
    @DocumentReference
    @JsonManagedReference
    private OrderEntity order;
    private ReturnReason reason;
    private String description;
    private ReturnOffer offer;
    @Builder.Default
    private List<ReturnItem> items = new ArrayList<>();
    @Builder.Default
    private List<ReturnStatus> statusHistory = new ArrayList<>();
    private ShipmentEntity shipment;
    private LocalDateTime expiredAt;
}
