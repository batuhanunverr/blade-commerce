package com.kesik.bladecommerce.dto.knife;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "knives")
@Data
public class KnifeDto {

    @Id
    private String id;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private String description;
    private double price;
    private double discountPrice;
    private boolean percentageDiscount;
    private int stockQuantity;
    private List<String> tags;
    private String imageUrl;
    private String knifeType;
    private double bladeLength;
    private String color;
    private String bladeMaterial;
    private String handleMaterial;
    private List<String> knifeSizes;
    private String selectedSize;
    private String customerNote;
}

