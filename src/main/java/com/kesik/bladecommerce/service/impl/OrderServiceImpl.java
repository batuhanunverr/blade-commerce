package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.repository.order.OrderRepository;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
    public List<OrderDto> searchOrders(String searchTerm, int page, int size, Optional<String> startDate, Optional<String> endDate) {
        Pageable pageable = PageRequest.of(page, size);

        String start = startDate.orElse(null);
        String end = endDate.orElse(null);

        return orderRepository.searchOrders(searchTerm, start, end, pageable).getContent();
    }
}
