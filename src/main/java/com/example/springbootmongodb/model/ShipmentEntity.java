package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import com.example.springbootmongodb.common.data.shipment.ShipmentAddress;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static com.example.springbootmongodb.model.ModelConstants.SHIPMENT_COLLECTION_NAME;

@Document(SHIPMENT_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ShipmentEntity extends AbstractEntity {
    private String note;
    private UserAddress userAddress;
    private ShopAddress shopAddress;
    private ShopAddress returnAddress;
    private List<ShipmentStatus> statusHistory = new ArrayList<>();
    private String estimatedPickTime;
    private String estimatedDeliverTime;
    private int deliveryFee;
    private int insuranceFee;
    private ShipmentAddress pickUpAddress;
    private ShipmentAddress deliverAddress;
}
