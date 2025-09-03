package com.kesik.bladecommerce.dto.order;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class KnifeOrderDto {
    @Id
    private String id;
    private String name;
    private Integer categoryId;
    private String description;
    private int stockQuantity;
    private String imageUrl;
    private String knifeType;
    private String selectedSize;
    private String customerNote;
}
