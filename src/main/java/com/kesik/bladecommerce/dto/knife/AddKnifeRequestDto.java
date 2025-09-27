package com.kesik.bladecommerce.dto.knife;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class AddKnifeRequestDto {
    private MultipartFile imageFile; // For file upload
    private String imageBase64;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Integer categoryId;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Positive(message = "Price must be positive")
    private double price;

    @PositiveOrZero(message = "Discount price must be positive or zero")
    private double discountPrice;

    private boolean percentageDiscount;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private int stockQuantity;

    private List<String> tags;

    @NotBlank(message = "Knife type is required")
    private String knifeType;

    @Positive(message = "Blade length must be positive")
    private double bladeLength;

    private String color;

    @NotBlank(message = "Blade material is required")
    private String bladeMaterial;

    @NotBlank(message = "Handle material is required")
    private String handleMaterial;

    @NotEmpty(message = "At least one knife size is required")
    private List<String> knifeSizes;
}
