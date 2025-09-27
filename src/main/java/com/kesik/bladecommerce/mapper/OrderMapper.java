package com.kesik.bladecommerce.mapper;

import com.kesik.bladecommerce.dto.iyzico.BasketItemDto;
import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.KnifeOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class OrderMapper {
    private static KnifeService knifeService = null;
    private static OrderService orderService = null;

    @Autowired
    public OrderMapper(KnifeService knifeService, OrderService orderService) {
        OrderMapper.knifeService = knifeService;
        OrderMapper.orderService = orderService;
    }

    @Transactional
    public static OrderDto mapOrderRequestToOrder(OrderRequestDto orderRequest) {
        try {
            OrderDto orderDto = new OrderDto();
            if (orderRequest.getBuyer() == null) {
                log.warn("Buyer information is missing in the order request: {}", orderRequest.getConversationId());
                throw new IllegalArgumentException("Buyer information is required.");
            }

            String currentDate = LocalDate.now().toString();
            orderDto.setOrderDate(currentDate);

            // Generate order number: ORD-YYYYMMDD-NNN
            String orderNumber = generateOrderNumber(currentDate);
            orderDto.setOrderNumber(orderNumber);
            log.info("Generated order number: {}", orderNumber);
            orderDto.setShippingAddress(
                    orderRequest.getShippingAddress() != null ? orderRequest.getShippingAddress().getAddress() : null
            );
            orderDto.setBillingAddress(
                    orderRequest.getBillingAddress() != null ? orderRequest.getBillingAddress().getAddress() : null
            );

            try {
                orderDto.setTotalAmount(Double.parseDouble(orderRequest.getPrice()));
            } catch (NumberFormatException e) {
                log.info("Invalid price format: {}", orderRequest.getPrice());
                throw new IllegalArgumentException("Invalid price format: " + orderRequest.getPrice(), e);
            }
            orderDto.setUserName(orderRequest.getBuyer().getName());
            orderDto.setUserSurname(orderRequest.getBuyer().getSurname());
            orderDto.setEmail(orderRequest.getBuyer().getEmail());
            orderDto.setPhoneNumber(orderRequest.getBuyer().getGsmNumber());
            orderDto.setConversationId(orderRequest.getConversationId());
            orderDto.setPaymentId(orderRequest.getPaymentId());
            orderDto.setHistory("Sipariş oluşturuldu.");
            List<KnifeOrderDto> orderKnifes = new ArrayList<>();
            for (BasketItemDto knife : orderRequest.getBasketItems()) {
                KnifeDto knifeDto = knifeService.getKnifeById(knife.getId());
                if (knifeDto == null) {
                    log.info("Knife not found with id: {}", knife.getId());
                    throw new IllegalArgumentException("Knife not found with id: " + knife.getId());
                }
                if (knife.getQuantity() > knifeDto.getStockQuantity()) {
                    log.info("Insufficient stock for knife: {}. Requested: {}, Available: {}",
                            knifeDto.getName(), knife.getQuantity(), knifeDto.getStockQuantity());
                    throw new IllegalArgumentException("Insufficient stock for knife: " + knifeDto.getName());
                }
                if(knifeDto.getKnifeSizes() != null){
                    if (knife.getSelectedSize() != null && !knifeDto.getKnifeSizes().contains(knife.getSelectedSize())) {
                        log.info("Invalid knife size selected for knife: {}. Selected size: {}, Available sizes: {}",
                                knifeDto.getName(), knife.getSelectedSize(), knifeDto.getKnifeSizes());
                        throw new IllegalArgumentException("Invalid knife size selected for knife: " + knifeDto.getName());
                    }
                }

                KnifeOrderDto knifeOrderDto = generateKnifeOrder(knifeDto, knife);
                int quantity = knifeDto.getStockQuantity() - knife.getQuantity();
                knifeService.updateKnifeStockQuantity(knifeDto.getId(), quantity);
                log.info("Updated stock for knife: {}. New stock quantity: {}", knifeDto.getName(), quantity);
                knifeOrderDto.setStockQuantity(quantity);
                orderKnifes.add(knifeOrderDto);
            }
            orderDto.setKnife(orderKnifes);
            return orderDto;
        } catch (Exception e) {
            log.error("Error mapping OrderRequest to Order: {}", e.getMessage(), e);
            throw new RuntimeException("Error mapping OrderRequest to Order: " + e.getMessage(), e);
        }
    }

    private static KnifeOrderDto generateKnifeOrder(KnifeDto knifeDto, BasketItemDto knife) {
        KnifeOrderDto knifeOrderDto = new KnifeOrderDto();
        knifeOrderDto.setId(knifeDto.getId());
        knifeOrderDto.setName(knifeDto.getName());
        knifeOrderDto.setCategoryId(knifeDto.getCategoryId());
        knifeOrderDto.setDescription(knifeDto.getDescription());
        knifeOrderDto.setPrice(knifeDto.getPrice());
        knifeOrderDto.setDiscountPrice(knifeDto.getDiscountPrice());
        knifeOrderDto.setImageUrl(knifeDto.getImageUrl());
        knifeOrderDto.setKnifeType(knifeDto.getKnifeType());
        knifeOrderDto.setBladeLength(knifeDto.getBladeLength());
        knifeOrderDto.setBladeMaterial(knifeDto.getBladeMaterial());
        knifeOrderDto.setHandleMaterial(knifeDto.getHandleMaterial());
        knifeOrderDto.setColor(knifeDto.getColor());
        knifeOrderDto.setSelectedSize(knife.getSelectedSize());
        knifeOrderDto.setCustomerNote(knife.getNote());
        return knifeOrderDto;
    }

    /**
     * Generates a unique order number using hash-based approach
     * Format: ORD-XXXXXXXX (e.g., ORD-A7B8C9D2)
     * Uses hash of timestamp, date, and daily count for security
     *
     * @param dateString The order date in YYYY-MM-DD format
     * @return Generated order number
     */
    private static String generateOrderNumber(String dateString) {
        try {
            // Get daily order count for uniqueness
            long dailyOrderCount = orderService.getOrderCountForDate(dateString);

            // Create unique input for hashing
            long timestamp = System.currentTimeMillis();
            String input = timestamp + ":" + dateString + ":" + (dailyOrderCount + 1) + ":BLADE_COMMERCE_SALT_2025";

            // Generate SHA-256 hash and take first 8 characters
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));

            // Convert to hex and take first 8 characters, make uppercase
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < Math.min(4, hashBytes.length); i++) {
                String hex = Integer.toHexString(0xff & hashBytes[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String orderCode = hexString.toString().toUpperCase();
            return "ORD-" + orderCode;

        } catch (Exception e) {
            log.error("Error generating hash-based order number for date {}: {}", dateString, e.getMessage());
            // Fallback: random alphanumeric code
            String randomCode = Long.toString(System.currentTimeMillis(), 36).substring(2, 10).toUpperCase();
            return "ORD-" + randomCode;
        }
    }
}
