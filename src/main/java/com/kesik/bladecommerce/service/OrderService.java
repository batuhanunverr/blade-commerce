package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.order.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDto addOrder(OrderDto orderDto);

    OrderDto updateOrder(OrderDto orderDto);

    void deleteOrder(String id);

    List<OrderDto> getAllOrders();

    Optional<OrderDto> getOrderById(String id);

    List<OrderDto> getOrdersByStatus(String orderStatus);

    Page<OrderDto> searchOrders(String searchTerm, String minPrice, String maxPrice, String startDate, String endDate,
                                int sortDirection, String status, Pageable pageable);

}