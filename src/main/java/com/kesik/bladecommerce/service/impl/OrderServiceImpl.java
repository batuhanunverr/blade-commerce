package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.AddOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.order.OrderKnifeDto;
import com.kesik.bladecommerce.dto.order.OrderStatusDto;
import com.kesik.bladecommerce.mapper.OrderMapper;
import com.kesik.bladecommerce.repository.order.OrderRepository;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.service.MailService;
import com.kesik.bladecommerce.service.OrderService;
import com.kesik.bladecommerce.util.OrderStatusHolder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;
    private final OrderStatusHolder orderStatusHolder;
    private final MailService mailService;

    public OrderServiceImpl(OrderRepository orderRepository, MongoTemplate mongoTemplate, OrderStatusHolder orderStatusHolder, MailService mailService) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
        this.orderStatusHolder = orderStatusHolder;
        this.mailService = mailService;
    }

    @Override
    public OrderDto addOrder(OrderRequestDto orderDto) {
        OrderDto order = OrderMapper.mapOrderRequestToOrder(orderDto);
        order.setOrderStatus(orderStatusHolder.getOrderStatusByCode(1));
        return orderRepository.save(order);
    }

    @Override
    public OrderDto updateOrder(String id, int orderStatus, String history, String adminNote) {
        try {
            OrderDto existingOrder = orderRepository.findById(id).orElse(null);
            OrderStatusDto orderStatusDto = orderStatusHolder.getOrderStatusByCode(orderStatus);
            if (existingOrder != null) {
                if (adminNote != null && !adminNote.isEmpty()) {
                    existingOrder.setAdminNote(adminNote);
                }
                existingOrder.setOrderStatus(orderStatusDto);
                existingOrder.setHistory(history);
                OrderDto updatedOrder = orderRepository.save(existingOrder);

                if (updatedOrder.getEmail() != null && !updatedOrder.getEmail().isEmpty()) {
                    try {
                        mailService.sendOrderStatusUpdate(
                                updatedOrder.getEmail(),
                                "Sipariş Durumunuz Güncellendi",
                                "Siparişinizin yeni durumu: " + orderStatusDto.getOrderStatusText()
                        );
                    } catch (Exception mailException) {
                        System.err.println("Mail gönderilemedi: " + mailException.getMessage());
                    }
                }
                return updatedOrder;
            }
        } catch (Exception e) {
            log.info("Error updating order: {}", e.getMessage());
            throw new RuntimeException("Error updating order: " + e.getMessage(), e);
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
    public List<OrderDto> getOrdersByStatus(int orderStatus) {
        return orderRepository.findByOrderStatusCode(orderStatus);
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

        Criteria criteria;
        if (criteriaList.isEmpty()) {
            criteria = new Criteria();
        } else {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Sort sort = sortDirection == 1 ? Sort.by("totalAmount").ascending() : Sort.by("totalAmount").descending();
        Query query = new Query(criteria).with(pageable).with(sort);

        List<OrderDto> orders = mongoTemplate.find(query, OrderDto.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), OrderDto.class);

        return PageableExecutionUtils.getPage(orders, pageable, () -> count);
    }

    @Override
    public OrderDto updateOrderStatus(String id, int orderStatusCode) {
        OrderStatusDto orderStatus = new OrderStatusDto(0, "");
        orderStatus.setOrderStatusCode(orderStatusCode);
        OrderDto existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder != null) {
            existingOrder.setOrderStatus(orderStatus);
            return orderRepository.save(existingOrder);
        }
        return null;
    }

    @Override
    public List<OrderStatusDto> getAllOrderStatus() {
        return orderStatusHolder.getAllOrderStatusAsDtoList();
    }
}
