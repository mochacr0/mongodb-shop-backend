package com.example.springbootmongodb.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomReturnRepositoryImpl implements CustomReturnRepository{
    private final MongoTemplate template;

    @Override
    public void acceptReturnRequests() {
//        Query queryStatement = Query.query(where("expiredAt").lte(LocalDateTime.now()).and("statusHistory.").is());
    }
}
