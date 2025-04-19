package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.repository.order.OrderRepository;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public OrderDto addOrder(OrderDto orderDto) {
        return orderRepository.save(orderDto);
    }

    @Override
    public OrderDto updateOrder(OrderDto orderDto) {
        OrderDto existingOrder = orderRepository.findById(orderDto.getId()).orElse(null);
        if (existingOrder != null) {
            existingOrder.setBillingAddress(orderDto.getBillingAddress());
            existingOrder.setShippingAddress(orderDto.getShippingAddress());
            existingOrder.setKnife(orderDto.getKnife());
            existingOrder.setTotalAmount(orderDto.getTotalAmount());
            existingOrder.setOrderStatus(orderDto.getOrderStatus());
            return orderRepository.save(existingOrder);
        }
        return null;
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<OrderDto> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<OrderDto> getOrdersByStatus(String orderStatus) {
        return orderRepository.findByOrderStatus(orderStatus);
    }
    @Override
    public Page<OrderDto> searchOrders(String searchTerm, String minPrice, String maxPrice, String startDate, String endDate,
                                       int sortDirection, String status, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isBlank()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("knife.name").regex(searchTerm, "i"),
                    Criteria.where("knife.description").regex(searchTerm, "i")
            ));
        }

        if (minPrice != null) {
            criteriaList.add(Criteria.where("totalAmount").gte(Double.parseDouble(minPrice)));
        }

        if (maxPrice != null) {
            criteriaList.add(Criteria.where("totalAmount").lte(Double.parseDouble(maxPrice)));
        }

        if (startDate != null) {
            criteriaList.add(Criteria.where("orderDate").gte(startDate));
        }

        if (endDate != null) {
            criteriaList.add(Criteria.where("orderDate").lte(endDate));
        }

        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("orderStatus").is(status));
        }

        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Sort sort = sortDirection == 1 ? Sort.by("totalAmount").ascending() : Sort.by("totalAmount").descending();
        Query query = new Query(criteria).with(pageable).with(sort);

        List<OrderDto> orders = mongoTemplate.find(query, OrderDto.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), OrderDto.class);

        return PageableExecutionUtils.getPage(orders, pageable, () -> count);
    }
}
