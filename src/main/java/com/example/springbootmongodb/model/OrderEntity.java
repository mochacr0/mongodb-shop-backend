package com.example.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.ORDER_COLLECTION_NAME;

@Document(collection = ORDER_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class OrderEntity extends AbstractEntity {
    @DocumentReference
    private UserEntity user;
    private UserAddressEntity userAddress;
    private long subTotal;
    private Payment payment;
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<OrderStatus> statusHistory = new ArrayList<>();
    private LocalDateTime expiredAt;
    private String note;
    @DocumentReference
    private ShipmentEntity shipment;
    private LocalDateTime completedAt;
    @DocumentReference(lazy = true)
    @JsonBackReference
    private OrderReturnEntity orderReturn = null;
    @DocumentReference(lazy = true)
    @JsonBackReference
    private OrderReturnEntity orderRefund = null;
}
