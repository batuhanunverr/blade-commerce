package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.AddOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.order.OrderKnifeDto;
import com.kesik.bladecommerce.dto.order.OrderStatusDto;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto addOrder(@RequestBody OrderRequestDto orderDto) {
        try {
            log.info("Received OrderRequest: {}", orderDto.getConversationId());
            return orderService.addOrder(orderDto);
        }catch (Exception e){
            throw new RuntimeException("Error mapping OrderRequest to Order: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public OrderDto updateOrder(@PathVariable String id, @RequestParam int orderStatusCode, @RequestParam(required = false) String history, @RequestParam(required = false) String adminNote) {
        log.info("Received UpdateOrderRequest: {}", id);
        return orderService.updateOrder(id, orderStatusCode, history, adminNote);
    }

    @GetMapping(path = "/getAllOrderStatus")
    public List<OrderStatusDto> getAllOrderStatus() {
        return orderService.getAllOrderStatus();
    }

    @GetMapping(path = "/deleteOrder")
    public void deleteOrder(String id) {
        orderService.deleteOrder(id);
    }

    @GetMapping(path = "/getAllOrders")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Optional<OrderDto> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @GetMapping(path = "/getOrdersByStatus")
    public List<OrderDto> getOrdersByStatus(int orderStatus) {
        return orderService.getOrdersByStatus(orderStatus);
    }

    @GetMapping(path = "/search")
    public Page<OrderDto> searchOrders(@RequestParam(required = false) String searchTerm,
                                       @RequestParam(required = false) String minPrice,
                                       @RequestParam(required = false) String maxPrice,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate,
                                       @RequestParam(required = false, defaultValue = "1") int sortDirection,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return orderService.searchOrders(searchTerm, minPrice, maxPrice, startDate, endDate, sortDirection, status, pageable);
    }
}
