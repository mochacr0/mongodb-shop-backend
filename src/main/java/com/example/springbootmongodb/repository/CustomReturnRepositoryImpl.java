package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ReturnOffer;
import com.example.springbootmongodb.common.data.ReturnState;
import com.example.springbootmongodb.common.data.ReturnStatus;
import com.example.springbootmongodb.model.OrderReturnEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomReturnRepositoryImpl implements CustomReturnRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public void acceptExpiredReturnRequests() {
        Query queryStatement = Query.query(where("expiredAt").lte(LocalDateTime.now()).and("currentStatus.state").is(ReturnState.REQUESTED.name()));
        List<OrderReturnEntity> expiredRequests = mongoTemplate.find(queryStatement, OrderReturnEntity.class);

//        Query queryStatement = Query.query(where("expiredAt").lte(LocalDateTime.now()).and("currentStatus.state").is(ReturnState.REQUESTED.name()));
//        Update updateStatement = new Update();
//        ReturnStatus processingStatus = ReturnStatus
//                .builder()
//                .state(ReturnState.REQUESTED)
//                .createdAt(LocalDateTime.now())
//                .build();
//        updateStatement.push("statusHistory", processingStatus);
//        updateStatement.set("currentStatus", processingStatus);
//        updateStatement.set("expiredAt", null);
//        BulkOperations acceptReturnRequestsOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, OrderReturnEntity.class);
//        acceptReturnRequestsOperation.updateMulti(queryStatement, updateStatement);
//        acceptReturnRequestsOperation.execute();
    }

    @Override
    public void confirmExpiredReturnsProcessing() {

    }

    @Override
    public void completeRefundedReturns() {

    }
}
