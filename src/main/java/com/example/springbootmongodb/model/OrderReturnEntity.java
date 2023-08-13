package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.ReturnItem;
import com.example.springbootmongodb.common.data.ReturnOffer;
import com.example.springbootmongodb.common.data.ReturnReason;
import com.example.springbootmongodb.common.data.ReturnStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String orderId;
    private ReturnReason reason;
    private String description;
    private ReturnOffer offer;
    private List<ReturnItem> items = new ArrayList<>();
    private List<ReturnStatus> statusHistory = new ArrayList<>();
    private ShipmentEntity shipment;
    private LocalDateTime expiredAt;
}
