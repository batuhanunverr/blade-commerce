package com.kesik.bladecommerce.dto.socialproof;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for social proof recent purchase notifications
 * Contains anonymized customer and product information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPurchaseDTO {

    /**
     * Anonymized customer name (e.g., "Ahmet K.")
     */
    private String customerName;

    /**
     * Product name purchased
     */
    private String productName;

    /**
     * Product slug for linking
     */
    private String productSlug;

    /**
     * City location only (privacy-safe)
     */
    private String location;

    /**
     * Fuzzy time description (e.g., "15 dakika önce")
     */
    private String timeAgo;

    /**
     * Product category for context-aware display
     * TODO: Use this for category-matching when viewing specific categories
     */
    private String category;
}
