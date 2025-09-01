package com.kesik.bladecommerce.dto.iyzico;

import lombok.Data;

@Data
public class BasketItemDto {
    private String id;
    private Integer quantity;
    private String price;
    private String name;
    private String category1;
    private String category2;
    private String itemType;
    private String selectedSize;
    private String note;
}
