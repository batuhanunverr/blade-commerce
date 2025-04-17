package com.kesik.bladecommerce.repository.order;

import com.kesik.bladecommerce.dto.order.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderDto, String> {

    List<OrderDto> findByOrderStatus(String orderStatus);

    @Query("{ $and: [ " +
            "{ $or: [ { 'knife.name': { $regex: ?0, $options: 'i' } }, { 'knife.description': { $regex: ?0, $options: 'i' } } ] }, " +
            "{ $or: [ { ?1: null }, { 'orderDate': { $gte: ?1 } } ] }, " +
            "{ $or: [ { ?2: null }, { 'orderDate': { $lte: ?2 } } ] } " +
            "] }")
    Page<OrderDto> searchOrders(String searchTerm, String startDate, String endDate, Pageable pageable);}
