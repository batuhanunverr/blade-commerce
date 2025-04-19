package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public OrderDto addOrder(@RequestBody OrderDto orderDto) {
        List<KnifeDto> fullKnifeList = new ArrayList<>();

        for (KnifeDto knife : orderDto.getKnife()) {
            KnifeDto fullKnife = knifeService.getKnifeById(knife.getId());
            fullKnife.setStockQuantity(fullKnife.getStockQuantity() - 1);
            knifeService.updateKnife(fullKnife);
            fullKnifeList.add(fullKnife);
        }
        orderDto.setKnife(fullKnifeList);
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
    public Page<OrderDto> searchOrders(@RequestParam(required = false) String searchTerm,
                                       @RequestParam(required = false) String minPrice,
                                       @RequestParam(required = false) String maxPrice,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate,
                                       @RequestParam(required = false, defaultValue = "1") int sortDirection,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.searchOrders(searchTerm, minPrice, maxPrice, startDate, endDate, sortDirection, status, pageable);
    }
}
