package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.AddOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.order.OrderKnifeDto;
import com.kesik.bladecommerce.dto.order.OrderStatistics;
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
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.bson.Document;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;
    private final OrderStatusHolder orderStatusHolder;
    private final MailService mailService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, MongoTemplate mongoTemplate, OrderStatusHolder orderStatusHolder, MailService mailService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
        this.orderStatusHolder = orderStatusHolder;
        this.mailService = mailService;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDto addOrder(OrderRequestDto orderDto) {
        String currentDate = java.time.LocalDate.now().toString();
        long dailyOrderCount = getOrderCountForDate(currentDate);
        OrderDto order = orderMapper.mapOrderRequestToOrder(orderDto, dailyOrderCount);
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
    public Page<OrderDto> getAllOrdersPaginated(Pageable pageable) {
        // Sort by orderDate descending by default
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("orderDate").descending().and(Sort.by("_id").descending())
        );
        return orderRepository.findAll(sortedPageable);
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
                                       int sortDirection, String status, String paymentId, String conversationId,
                                       String shippingCity, String adminNote, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isBlank()) {
            String cleanSearchTerm = searchTerm.trim();
            String[] terms = cleanSearchTerm.split("\\s+");

            List<Criteria> termCriteria = new ArrayList<>();
            for (String term : terms) {
                // For ID searches - handle both full and partial ObjectIds
                Criteria idCriteria;
                if (term.matches("^[0-9a-fA-F]+$")) {
                    // For hex-like strings (ObjectId format)
                    if (term.length() == 24) {
                        // Full ObjectId - use exact match
                        idCriteria = Criteria.where("id").is(term);
                    } else {
                        // Partial ObjectId - use prefix match
                        idCriteria = Criteria.where("id").regex("^" + term, "i");
                    }
                } else {
                    // For non-hex strings, use contains search
                    idCriteria = Criteria.where("id").regex(term, "i");
                }

                termCriteria.add(new Criteria().orOperator(
                    Criteria.where("userName").regex(term, "i"),
                    Criteria.where("userSurname").regex(term, "i"),
                    Criteria.where("email").regex(term, "i"),
                    Criteria.where("phoneNumber").regex(term, "i"),
                    Criteria.where("orderNumber").regex(term, "i"), // Search by order number
                    idCriteria
                ));
            }

            // All terms must match somewhere (AND logic across terms)
            criteriaList.add(new Criteria().andOperator(termCriteria.toArray(new Criteria[0])));
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
            try {
                int statusCode = Integer.parseInt(status);
                criteriaList.add(Criteria.where("orderStatus.orderStatusCode").is(statusCode));
            } catch (NumberFormatException e) {
                log.warn("Invalid status format: {}", status);
            }
        }

        // New advanced search fields
        if (paymentId != null && !paymentId.isBlank()) {
            criteriaList.add(Criteria.where("paymentId").regex(paymentId.trim(), "i"));
        }

        if (conversationId != null && !conversationId.isBlank()) {
            criteriaList.add(Criteria.where("conversationId").regex(conversationId.trim(), "i"));
        }

        if (shippingCity != null && !shippingCity.isBlank()) {
            criteriaList.add(Criteria.where("shippingAddress").regex(shippingCity.trim(), "i"));
        }

        if (adminNote != null && !adminNote.isBlank()) {
            criteriaList.add(Criteria.where("adminNote").regex(adminNote.trim(), "i"));
        }

        Criteria criteria;
        if (criteriaList.isEmpty()) {
            criteria = new Criteria();
        } else {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Sort sort = sortDirection == 1 ?
            Sort.by("orderDate").ascending().and(Sort.by("_id").ascending()) :
            Sort.by("orderDate").descending().and(Sort.by("_id").descending());
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

    @Override
    public long getOrderCountForDate(String date) {
        try {
            Criteria criteria = Criteria.where("orderDate").is(date);
            Query query = new Query(criteria);
            return mongoTemplate.count(query, OrderDto.class);
        } catch (Exception e) {
            log.error("Error counting orders for date {}: {}", date, e.getMessage());
            return 0;
        }
    }

    @Override
    public OrderStatistics getOrderStatistics(String startDate, String endDate) {
        try {
            OrderStatistics stats = new OrderStatistics();
            stats.setPeriodStart(startDate);
            stats.setPeriodEnd(endDate);

            // Build date range criteria
            List<Criteria> criteriaList = new ArrayList<>();
            if (startDate != null) {
                criteriaList.add(Criteria.where("orderDate").gte(startDate));
            }
            if (endDate != null) {
                criteriaList.add(Criteria.where("orderDate").lte(endDate));
            }

            Criteria criteria = criteriaList.isEmpty() ? new Criteria() :
                new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            Query query = new Query(criteria);

            // Get all orders in the period
            List<OrderDto> orders = mongoTemplate.find(query, OrderDto.class);

            // Calculate basic metrics
            stats.setTotalOrders(orders.size());

            long pendingCount = orders.stream()
                .filter(order -> order.getOrderStatus() != null && order.getOrderStatus().getOrderStatusCode() == 1)
                .count();
            stats.setPendingOrders(pendingCount);

            long completedCount = orders.stream()
                .filter(order -> order.getOrderStatus() != null && order.getOrderStatus().getOrderStatusCode() == 5)
                .count();
            stats.setCompletedOrders(completedCount);

            // Calculate financial metrics
            double totalRevenue = orders.stream()
                .filter(order -> order.getTotalAmount() != null)
                .mapToDouble(OrderDto::getTotalAmount)
                .sum();
            stats.setTotalRevenue(totalRevenue);

            if (orders.size() > 0) {
                stats.setAverageOrderValue(totalRevenue / orders.size());
            }

            // Calculate daily breakdown
            Map<String, Long> ordersByDate = orders.stream()
                .filter(order -> order.getOrderDate() != null)
                .collect(Collectors.groupingBy(
                    OrderDto::getOrderDate,
                    Collectors.counting()
                ));
            stats.setOrdersByDate(ordersByDate);

            Map<String, Double> revenueByDate = orders.stream()
                .filter(order -> order.getOrderDate() != null && order.getTotalAmount() != null)
                .collect(Collectors.groupingBy(
                    OrderDto::getOrderDate,
                    Collectors.summingDouble(OrderDto::getTotalAmount)
                ));
            stats.setRevenueByDate(revenueByDate);

            // Calculate status breakdown
            Map<String, Long> ordersByStatus = orders.stream()
                .filter(order -> order.getOrderStatus() != null)
                .collect(Collectors.groupingBy(
                    order -> order.getOrderStatus().getOrderStatusText(),
                    Collectors.counting()
                ));
            stats.setOrdersByStatus(ordersByStatus);

            // Calculate days covered
            if (startDate != null && endDate != null) {
                try {
                    long daysBetween = ChronoUnit.DAYS.between(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                    ) + 1;
                    stats.setDaysCovered(daysBetween);
                } catch (Exception e) {
                    log.warn("Error calculating days between {} and {}: {}", startDate, endDate, e.getMessage());
                }
            }

            return stats;
        } catch (Exception e) {
            log.error("Error generating order statistics for period {} to {}: {}", startDate, endDate, e.getMessage());
            return new OrderStatistics(); // Return empty statistics
        }
    }

    @Override
    public Map<String, Object> migrateExistingOrderNumbers() {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int processedCount = 0;
        int updatedCount = 0;

        try {
            log.info("Starting migration of existing orders to add order numbers");

            // Find all orders without order numbers
            Query query = new Query(new Criteria().orOperator(
                Criteria.where("orderNumber").exists(false),
                Criteria.where("orderNumber").is(null),
                Criteria.where("orderNumber").is("")
            ));

            List<OrderDto> ordersWithoutNumbers = mongoTemplate.find(query, OrderDto.class);
            log.info("Found {} orders without order numbers", ordersWithoutNumbers.size());

            // Get existing order numbers to ensure uniqueness
            Query existingQuery = new Query(
                new Criteria().andOperator(
                    Criteria.where("orderNumber").exists(true),
                    Criteria.where("orderNumber").ne(null),
                    Criteria.where("orderNumber").ne("")
                )
            );
            existingQuery.fields().include("orderNumber");

            List<OrderDto> ordersWithNumbers = mongoTemplate.find(existingQuery, OrderDto.class);
            Set<String> existingOrderNumbers = ordersWithNumbers.stream()
                .map(OrderDto::getOrderNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

            log.info("Found {} existing order numbers", existingOrderNumbers.size());

            // Sort orders by date to maintain chronological order numbers
            ordersWithoutNumbers.sort(Comparator.comparing(order ->
                order.getOrderDate() != null ? order.getOrderDate() : "1970-01-01"
            ));

            // Generate and assign order numbers
            for (int i = 0; i < ordersWithoutNumbers.size(); i++) {
                OrderDto order = ordersWithoutNumbers.get(i);
                processedCount++;

                try {
                    // Generate order number
                    String orderNumber = generateUniqueOrderNumber(order, i, existingOrderNumbers);
                    existingOrderNumbers.add(orderNumber);

                    // Update the order
                    order.setOrderNumber(orderNumber);
                    orderRepository.save(order);
                    updatedCount++;

                    log.debug("Updated order {} with order number {}", order.getId(), orderNumber);

                } catch (Exception e) {
                    String errorMsg = String.format("Failed to update order %s: %s", order.getId(), e.getMessage());
                    errors.add(errorMsg);
                    log.error(errorMsg, e);
                }
            }

            result.put("success", true);
            result.put("message", "Migration completed");
            result.put("totalProcessed", processedCount);
            result.put("totalUpdated", updatedCount);
            result.put("errorCount", errors.size());

            if (!errors.isEmpty()) {
                result.put("errors", errors.subList(0, Math.min(errors.size(), 10))); // Show first 10 errors
            }

            log.info("Migration completed. Processed: {}, Updated: {}, Errors: {}",
                processedCount, updatedCount, errors.size());

        } catch (Exception e) {
            log.error("Migration failed", e);
            result.put("success", false);
            result.put("message", "Migration failed: " + e.getMessage());
            result.put("totalProcessed", processedCount);
            result.put("totalUpdated", updatedCount);
            result.put("errorCount", errors.size());
        }

        return result;
    }

    private String generateUniqueOrderNumber(OrderDto order, int index, Set<String> existingNumbers) {
        // Extract year from order date
        String year = "2024"; // Default year
        if (order.getOrderDate() != null && !order.getOrderDate().isEmpty()) {
            try {
                if (order.getOrderDate().length() >= 4) {
                    year = order.getOrderDate().substring(0, 4);
                }
            } catch (Exception e) {
                log.warn("Could not extract year from order date {}, using default", order.getOrderDate());
            }
        }

        // Generate base order number
        String baseNumber = String.format("ORD-%s-%06d", year, index + 1);

        // Ensure uniqueness
        String orderNumber = baseNumber;
        int counter = 1;
        while (existingNumbers.contains(orderNumber)) {
            orderNumber = String.format("ORD-%s-%06d-%d", year, index + 1, counter);
            counter++;
        }

        return orderNumber;
    }
}
