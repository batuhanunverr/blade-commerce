package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping(path = "/addOrder")
    public OrderDto addOrder(OrderDto orderDto) {
        return orderService.addOrder(orderDto);
    }
    @PostMapping(path = "/updateOrder")
    public OrderDto updateOrder(OrderDto orderDto) {
        return orderService.updateOrder(orderDto);
    }
    @PostMapping(path = "/deleteOrder")
    public void deleteOrder(String id) {
        orderService.deleteOrder(id);
    }
    @GetMapping(path = "/getAllOrders")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping(path = "/getOrderById")
    public Optional<OrderDto> getOrderById(String id) {
        return orderService.getOrderById(id);
    }
    @GetMapping(path = "/getOrdersByStatus")
    public List<OrderDto> getOrdersByStatus(String orderStatus) {
        return orderService.getOrdersByStatus(orderStatus);
    }
    @GetMapping(path = "/search")
    public List<OrderDto> searchOrders(@RequestParam String searchTerm,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        return orderService.searchOrders(searchTerm, page, size);
    }
}
