package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.example.springbootmongodb.model.ModelConstants.PAYMENT_COLLECTION_NAME;

@Document(collection = PAYMENT_COLLECTION_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PaymentEntity extends AbstractEntity {
    private PaymentMethod method;
    private String orderId;
    private boolean isPaid;
    private long amount;
    private String transId;
}
