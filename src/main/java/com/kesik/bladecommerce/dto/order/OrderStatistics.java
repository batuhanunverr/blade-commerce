package com.kesik.bladecommerce.dto.order;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Order Statistics DTO
 * Contains comprehensive statistics about orders for a given period
 */
@Data
public class OrderStatistics {

    // Basic counts
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;

    // Financial metrics
    private double totalRevenue;
    private double averageOrderValue;
    private double pendingRevenue;

    // Time-based breakdown
    private Map<String, Long> ordersByDate; // date -> count
    private Map<String, Double> revenueByDate; // date -> revenue

    // Status breakdown
    private Map<String, Long> ordersByStatus; // status -> count

    // Top products
    private List<ProductStatistics> topProducts;

    // Growth metrics
    private double growthRate; // compared to previous period
    private long ordersLastPeriod;

    // Additional metrics
    private String periodStart;
    private String periodEnd;
    private long daysCovered;

    @Data
    public static class ProductStatistics {
        private String productId;
        private String productName;
        private long orderCount;
        private double revenue;
        private long totalQuantity;
    }
}