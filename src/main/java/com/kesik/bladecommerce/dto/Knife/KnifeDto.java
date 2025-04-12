package com.kesik.bladecommerce.dto.Knife;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KnifeDto {
    private String id;
    private String name;
    private Integer categoryId;
    private String description;
    private double price;
    private double discountPrice;
    private KnifeDetailsDto knifeDetails;
    private int stockQuantity;
    private List<String> tags;
    private String imageUrl;
}
