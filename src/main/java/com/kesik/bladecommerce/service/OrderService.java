package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.order.AddOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.order.OrderStatistics;
import com.kesik.bladecommerce.dto.order.OrderStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

    OrderDto addOrder(OrderRequestDto orderDto);

    OrderDto updateOrder(String id, int orderStatus, String history, String adminNote);

    void deleteOrder(String id);

    List<OrderDto> getAllOrders();

    Optional<OrderDto> getOrderById(String id);

    List<OrderDto> getOrdersByStatus(int orderStatus);

    Page<OrderDto> searchOrders(String searchTerm, String minPrice, String maxPrice, String startDate, String endDate,
                                int sortDirection, String status, String paymentId, String conversationId,
                                String shippingCity, String adminNote, Pageable pageable);

    OrderDto updateOrderStatus(String id, int orderStatus);

    List<OrderStatusDto> getAllOrderStatus();

    /**
     * Get count of orders for a specific date
     * @param date Date in YYYY-MM-DD format
     * @return Number of orders for that date
     */
    long getOrderCountForDate(String date);

    /**
     * Get order statistics for a date range
     * @param startDate Start date in YYYY-MM-DD format
     * @param endDate End date in YYYY-MM-DD format
     * @return Order statistics
     */
    OrderStatistics getOrderStatistics(String startDate, String endDate);

    /**
     * Migrate existing orders to add order numbers
     * @return Migration results with statistics
     */
    Map<String, Object> migrateExistingOrderNumbers();
}