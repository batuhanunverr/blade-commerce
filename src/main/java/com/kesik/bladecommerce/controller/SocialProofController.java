package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.common.ApiResponse;
import com.kesik.bladecommerce.dto.order.KnifeOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.dto.socialproof.RecentPurchaseDTO;
import com.kesik.bladecommerce.repository.order.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Social Proof Controller
 * Provides recent purchase data for social proof notifications
 *
 * TODO: Make this configurable via admin panel
 * TODO: Add admin settings for:
 *   - Enable/disable social proof
 *   - Timing configuration (initial delay, rotation interval, max per session)
 *   - Display limits (min/max order age, max notifications)
 *   - Privacy settings (anonymization level)
 *   - Filtering rules (categories, minimum order value)
 *   - A/B testing toggle
 */
@RestController
@RequestMapping("/api/social-proof")
@Slf4j
public class SocialProofController {

    private final OrderRepository orderRepository;

    // TODO: Move these to database configuration table (admin-configurable)
    private static final int DEFAULT_LIMIT = 25;

    public SocialProofController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get recent purchases for social proof display
     * Returns anonymized purchase data to build trust and urgency
     *
     * TODO: CRITICAL - Add filtering by order status:
     *  - ONLY show orders with status: "Shipped", "Delivered", "Completed"
     *  - EXCLUDE orders with status: "Cancelled", "Returned", "Refunded", "Pending Payment"
     *  - This prevents showing purchases that didn't actually complete
     *  - Check OrderStatusDto.orderStatusCode field
     *
     * TODO: Add filtering by:
     *  - Min order age (don't show too recent - prevents fake feeling)
     *  - Max order age (don't show too old - keeps it relevant)
     *  - Minimum order value (show only quality purchases)
     *
     * @param limit Maximum number of purchases to return (default: 25)
     * @return List of recent anonymized purchases
     */
    @GetMapping("/recent-purchases")
    public ResponseEntity<ApiResponse<List<RecentPurchaseDTO>>> getRecentPurchases(
            @RequestParam(defaultValue = "25") int limit) {

        try {
            log.debug("Fetching {} recent purchases for social proof", limit);

            // Fetch recent orders sorted by date
            List<OrderDto> recentOrders = orderRepository.findAll(
                    PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"))
            ).getContent();

            // Convert to purchase DTOs
            List<RecentPurchaseDTO> purchases = recentOrders.stream()
                    .flatMap(order -> convertOrderToPurchases(order).stream())
                    .limit(limit)
                    .collect(Collectors.toList());

            // Randomize order to avoid chronological display
            // TODO: Implement smart selection algorithm:
            //  - Prefer popular items
            //  - Diverse categories
            //  - Weight by order value
            //  - Context-aware (match user's browsing category)
            Collections.shuffle(purchases);

            log.info("Returning {} recent purchases for social proof", purchases.size());

            return ResponseEntity.ok(ApiResponse.success(
                    "Recent purchases retrieved",
                    purchases
            ));

        } catch (Exception e) {
            log.error("Error fetching recent purchases for social proof", e);
            // Return empty list on error (graceful degradation)
            return ResponseEntity.ok(ApiResponse.success(
                    "Recent purchases retrieved",
                    new ArrayList<>()
            ));
        }
    }

    /**
     * Convert an order to anonymized purchase DTOs
     * One DTO per knife in the order
     */
    private List<RecentPurchaseDTO> convertOrderToPurchases(OrderDto order) {
        List<RecentPurchaseDTO> purchases = new ArrayList<>();

        if (order.getKnives() == null || order.getKnives().isEmpty()) {
            return purchases;
        }

        // Get anonymized customer name
        String anonymizedName = anonymizeCustomerName(order.getUserName(), order.getUserSurname());

        // Get location (TODO: extract city from address when available)
        String location = "Türkiye";  // Default location

        // Calculate fuzzy time
        String timeAgo = calculateTimeAgo(order.getOrderDate());

        // Create DTO for each knife in order
        // TODO: Consider showing only the most expensive or first item instead of all items
        for (KnifeOrderDto knife : order.getKnives()) {
            purchases.add(RecentPurchaseDTO.builder()
                    .customerName(anonymizedName)
                    .productName(knife.getName())
                    .productSlug(generateSlug(knife.getName()))
                    .location(location)
                    .timeAgo(timeAgo)
                    .category(null) // TODO: Add category field to KnifeOrderDto or fetch from Knife entity
                    .build());
        }

        return purchases;
    }

    /**
     * Anonymize customer name: "Ahmet Yılmaz" → "Ahmet Y."
     * TODO: Make anonymization level configurable via admin panel:
     *  - Level 1: Full name (less privacy)
     *  - Level 2: First name + last initial (current, balanced)
     *  - Level 3: Just first name (more privacy)
     *  - Level 4: Generic "Müşteri" (most privacy)
     */
    private String anonymizeCustomerName(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return "Müşteri";
        }

        String first = firstName.trim();

        if (lastName == null || lastName.trim().isEmpty()) {
            return first;
        }

        String last = lastName.trim();

        // Return "FirstName L."
        return first + " " + last.charAt(0) + ".";
    }

    /**
     * Calculate fuzzy time description in Turkish
     * TODO: Support multiple languages based on user locale (EN/TR toggle)
     * TODO: Make time descriptions configurable
     */
    private String calculateTimeAgo(String orderDateStr) {
        try {
            LocalDateTime orderDate;

            if (orderDateStr.length() == 10) {
                // Format: yyyy-MM-dd
                orderDate = LocalDate.parse(orderDateStr)
                        .atStartOfDay();
            } else {
                // Format: yyyy-MM-ddTHH:mm:ss
                orderDate = LocalDateTime.parse(
                        orderDateStr,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
            }

            Duration duration = Duration.between(orderDate, LocalDateTime.now());

            long minutes = duration.toMinutes();
            if (minutes < 60) {
                return minutes + " dakika önce";
            }

            long hours = duration.toHours();
            if (hours < 24) {
                return hours + " saat önce";
            }

            long days = duration.toDays();
            if (days == 1) {
                return "dün";
            }
            if (days < 7) {
                return days + " gün önce";
            }
            if (days < 30) {
                return "bu ay";
            }

            return "geçen ay";

        } catch (Exception e) {
            log.warn("Could not parse order date: {}", orderDateStr, e);
            return "kısa süre önce";
        }
    }

    /**
     * Generate URL-friendly slug from product name
     * TODO: Use actual product slug from knife database instead of generating
     */
    private String generateSlug(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "";
        }

        return productName
                .toLowerCase()
                .replaceAll("ğ", "g")
                .replaceAll("ü", "u")
                .replaceAll("ş", "s")
                .replaceAll("ı", "i")
                .replaceAll("ö", "o")
                .replaceAll("ç", "c")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
