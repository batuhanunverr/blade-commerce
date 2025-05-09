package com.kesik.bladecommerce.dto.knife;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddKnifeRequestDto {
    private MultipartFile imageFile; // For file upload
    private String name;
    private Integer categoryId;
    private String description;
    private double price;
    private double discountPrice;
    private int stockQuantity;
    private List<String> tags;
    private String knifeType;
    private double bladeLength;
    private String color;
    private String bladeMaterial;
    private String handleMaterial;
}
