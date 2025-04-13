package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.order.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDto addOrder(OrderDto orderDto);

    OrderDto updateOrder(OrderDto orderDto);

    void deleteOrder(String id);

    List<OrderDto> getAllOrders();

    Optional<OrderDto> getOrderById(String id);

    List<OrderDto> getOrdersByStatus(String orderStatus);

    List<OrderDto> searchOrders(String searchTerm, int page, int size);
}
