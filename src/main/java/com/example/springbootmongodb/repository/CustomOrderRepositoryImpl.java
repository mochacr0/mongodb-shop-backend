package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.OrderState;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.OrderStatus;
import com.example.springbootmongodb.model.ProductItemEntity;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomOrderRepositoryImpl implements CustomOrderRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public void cancelExpiredOrders() {
        Query queryStatement = Query.query(where("expiredAt").lte(LocalDateTime.now()));
        Update updateStatement = new Update();
        updateStatement.push("statusHistory", OrderStatus.builder().state(OrderState.CANCELED).createdAt(LocalDateTime.now()).build());
        updateStatement.set("expiredAt", null);
        BulkOperations cancelExpiredOrdersOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, OrderEntity.class);
        cancelExpiredOrdersOperation.updateMulti(queryStatement, updateStatement);
        cancelExpiredOrdersOperation.execute();
    }

    @Override
    public void rollbackOrderItemQuantities(List<OrderItem> orderItems) {
        BulkOperations rollbackOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ProductItemEntity.class);
        if (Collections.isEmpty(orderItems)) {
            return;
        }
        for (OrderItem orderItem : orderItems) {
            rollbackOperations.updateOne(Query.query(where("_id").is(orderItem.getProductItemId())),
                    new Update().inc("quantity", orderItem.getQuantity()));
        }
        rollbackOperations.execute();
    }

    @Override
    public void markCompletedOrders() {
        Query queryStatement = Query.query(where("completedAt").lte(LocalDateTime.now()));
        Update updateStatement = new Update();
        updateStatement.push("statusHistory", OrderStatus.builder().state(OrderState.COMPLETED).createdAt(LocalDateTime.now()).build());
        updateStatement.set("completedAt", null);
        BulkOperations cancelExpiredOrdersOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, OrderEntity.class);
        cancelExpiredOrdersOperation.updateMulti(queryStatement, updateStatement);
        cancelExpiredOrdersOperation.execute();
    }
}
