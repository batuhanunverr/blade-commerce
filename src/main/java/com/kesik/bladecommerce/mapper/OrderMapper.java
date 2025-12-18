package com.kesik.bladecommerce.mapper;

import com.kesik.bladecommerce.dto.iyzico.BasketItemDto;
import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.KnifeOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OrderMapper {
    private final KnifeService knifeService;

    @Autowired
    public OrderMapper(KnifeService knifeService) {
        this.knifeService = knifeService;
    }

    /**
     * Maps an OrderRequestDto to an OrderDto with atomic stock management.
     * Uses findAndModify for atomic stock decrement to prevent overselling.
     * Includes rollback mechanism if any part of the order fails.
     *
     * @param orderRequest The order request from Iyzico
     * @param dailyOrderCount The daily order count for order number generation
     * @return The mapped OrderDto ready for persistence
     * @throws IllegalArgumentException for validation errors
     * @throws IllegalStateException for stock availability issues
     */
    @Transactional
    public OrderDto mapOrderRequestToOrder(OrderRequestDto orderRequest, long dailyOrderCount) {
        // Track decremented stock for rollback
        List<StockChange> stockChanges = new ArrayList<>();

        try {
            OrderDto orderDto = new OrderDto();
            if (orderRequest.getBuyer() == null) {
                log.warn("Buyer information is missing in the order request: {}", orderRequest.getConversationId());
                throw new IllegalArgumentException("Buyer information is required.");
            }

            String currentDate = LocalDate.now().toString();
            orderDto.setOrderDate(currentDate);

            // Generate order number: ORD-XXXXXXXX
            String orderNumber = generateOrderNumber(currentDate, dailyOrderCount);
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
                    rollbackStockChanges(stockChanges);
                    throw new IllegalArgumentException("Knife not found with id: " + knife.getId());
                }

                // Validate size selection
                if (knifeDto.getKnifeSizes() != null) {
                    if (knife.getSelectedSize() != null && !knifeDto.getKnifeSizes().contains(knife.getSelectedSize())) {
                        log.info("Invalid knife size selected for knife: {}. Selected size: {}, Available sizes: {}",
                                knifeDto.getName(), knife.getSelectedSize(), knifeDto.getKnifeSizes());
                        rollbackStockChanges(stockChanges);
                        throw new IllegalArgumentException("Invalid knife size selected for knife: " + knifeDto.getName());
                    }
                }

                // Atomically decrement stock - this prevents overselling
                try {
                    KnifeDto updatedKnife = knifeService.decrementStockAtomic(knife.getId(), knife.getQuantity());
                    stockChanges.add(new StockChange(knife.getId(), knife.getQuantity()));
                    log.info("Atomically decremented stock for knife: {}. New stock: {}",
                            knifeDto.getName(), updatedKnife.getStockQuantity());

                    KnifeOrderDto knifeOrderDto = generateKnifeOrder(knifeDto, knife);
                    knifeOrderDto.setStockQuantity(updatedKnife.getStockQuantity());
                    orderKnifes.add(knifeOrderDto);

                } catch (IllegalStateException e) {
                    // Insufficient stock - rollback and throw
                    log.warn("Insufficient stock for knife: {}. Requested: {}, Error: {}",
                            knifeDto.getName(), knife.getQuantity(), e.getMessage());
                    rollbackStockChanges(stockChanges);
                    throw new IllegalArgumentException("Insufficient stock for knife: " + knifeDto.getName());
                }
            }

            orderDto.setKnives(orderKnifes);
            return orderDto;

        } catch (IllegalArgumentException e) {
            // Known validation errors - rollback already done above
            throw e;

        } catch (Exception e) {
            // Unexpected error - rollback all stock changes
            log.error("Error mapping OrderRequest to Order: {}", e.getMessage(), e);
            rollbackStockChanges(stockChanges);
            throw new RuntimeException("Error mapping OrderRequest to Order: " + e.getMessage(), e);
        }
    }

    /**
     * Rollback stock changes in case of order creation failure.
     * Restores stock for all products that were decremented.
     */
    private void rollbackStockChanges(List<StockChange> stockChanges) {
        if (stockChanges.isEmpty()) {
            return;
        }

        log.info("Rolling back {} stock changes", stockChanges.size());
        for (StockChange change : stockChanges) {
            try {
                knifeService.incrementStockAtomic(change.productId, change.quantity);
                log.info("Rolled back stock for product: {} (+{})", change.productId, change.quantity);
            } catch (Exception e) {
                // Log but don't throw - best effort rollback
                log.error("Failed to rollback stock for product: {}. Manual correction may be needed.", change.productId, e);
            }
        }
    }

    /**
     * Tracks stock changes for rollback purposes.
     */
    private record StockChange(String productId, int quantity) {}

    private KnifeOrderDto generateKnifeOrder(KnifeDto knifeDto, BasketItemDto knife) {
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
     * @param dailyOrderCount The count of orders for the given date
     * @return Generated order number
     */
    private String generateOrderNumber(String dateString, long dailyOrderCount) {
        try {

            // Create unique input for hashing
            long timestamp = System.currentTimeMillis();
            String input = timestamp + ":" + dateString + ":" + (dailyOrderCount + 1) + ":BLADE_COMMERCE_SALT_2025";

            // Generate SHA-256 hash and take first 8 characters
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

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
