package com.kesik.bladecommerce.config;

import com.kesik.bladecommerce.dto.order.OrderDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

@Configuration
public class MongoIndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        createOrderIndexes();
    }

    private void createOrderIndexes() {
        // Index for order date sorting (most common query)
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index("orderDate", Sort.Direction.DESC));

        // Index for status filtering
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index("orderStatus.orderStatusCode", Sort.Direction.ASC));

        // Index for price range queries
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index("totalAmount", Sort.Direction.ASC));

        // Compound index for common search patterns
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index()
                .on("orderStatus.orderStatusCode", Sort.Direction.ASC)
                .on("orderDate", Sort.Direction.DESC));

        // Text index for search functionality
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("userName")
                .onField("userSurname")
                .onField("email")
                .onField("phoneNumber")
                .build());

        // Individual indexes for exact matches
        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index("email", Sort.Direction.ASC));

        mongoTemplate.indexOps(OrderDto.class)
            .ensureIndex(new Index("phoneNumber", Sort.Direction.ASC));
    }
}