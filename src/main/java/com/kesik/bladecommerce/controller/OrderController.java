package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final KnifeService knifeService;
    public OrderController(OrderService orderService, KnifeService knifeService) {
        this.orderService = orderService;
        this.knifeService = knifeService;
    }
    @PostMapping(path = "/addOrder")
    public OrderDto addOrder(@RequestBody OrderDto orderDto, @RequestParam String knifeId) {
        KnifeDto knife = knifeService.getKnifeById(knifeId);
        orderDto.setKnife(List.of(knife));
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
                                       @RequestParam int size,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate) {
        return orderService.searchOrders(searchTerm, page, size, Optional.ofNullable(startDate), Optional.ofNullable(endDate));
    }
}
