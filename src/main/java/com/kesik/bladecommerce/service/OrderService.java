package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.order.AddOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.order.OrderStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDto addOrder(OrderRequestDto orderDto);

    OrderDto updateOrder(String id, int orderStatus, String history);

    void deleteOrder(String id);

    List<OrderDto> getAllOrders();

    Optional<OrderDto> getOrderById(String id);

    List<OrderDto> getOrdersByStatus(int orderStatus);

    Page<OrderDto> searchOrders(String searchTerm, String minPrice, String maxPrice, String startDate, String endDate,
                                int sortDirection, String status, Pageable pageable);

    OrderDto updateOrderStatus(String id, int orderStatus);

    List<OrderStatusDto> getAllOrderStatus();
}