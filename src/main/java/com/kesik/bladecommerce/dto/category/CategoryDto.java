package com.kesik.bladecommerce.dto.category;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "knife_categories")
@Data
public class CategoryDto {
    private String id;
    private String categoryName;
    private Integer categoryId;

    // Enhanced category metadata
    private String description;           // Category description for SEO and UI
    private String icon;                   // Icon name/identifier (e.g., "chef", "outdoor", "tactical")
    private Integer displayOrder;          // Sort order for display (lower = first)
    private Boolean isActive = true;       // Toggle visibility in frontend
}
