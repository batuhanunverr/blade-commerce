package com.kesik.bladecommerce.dto.category;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "knife_categories")
@Data
public class CategoryDto {
    private String  id;
    private String categoryName;
    private Integer categoryId;
}
