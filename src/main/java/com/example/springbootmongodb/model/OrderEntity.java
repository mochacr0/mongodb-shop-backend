package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.Packable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

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
public class OrderEntity extends AbstractEntity implements Packable {
    @DocumentReference
    @Field("userId")
    private UserEntity user;
    private UserAddressEntity userAddress;
    private long subTotal;
    private long total;
    private long deliveryFee;
    private Payment payment;
    private List<OrderItem> orderItems = new ArrayList<>();
    private OrderStatus currentStatus;
    private List<OrderStatus> statusHistory = new ArrayList<>();
    private LocalDateTime expiredAt;
    private String note;
    @DocumentReference
    private ShipmentEntity shipment;
    private LocalDateTime completedAt;
    @DocumentReference
    @JsonBackReference
    private OrderReturnEntity orderReturn = null;
    @DocumentReference
    @JsonBackReference
    private OrderReturnEntity orderRefund = null;
}
