package com.kesik.bladecommerce.dto.knife;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "knives")
public class KnifeDto {

    @Id
    private String id;

    private String name;
    private Integer categoryId;
    private String description;
    private double price;
    private double discountPrice;
    private int stockQuantity;
    private List<String> tags;
    private String imageUrl;

    private KnifeDetails knifeDetails;

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public KnifeDetails getKnifeDetails() {
        return knifeDetails;
    }

    public void setKnifeDetails(KnifeDetails knifeDetails) {
        this.knifeDetails = knifeDetails;
    }
}

class KnifeDetails {

    private String knifeType;
    private double bladeLength;
    private String color;
    private String bladeMaterial;
    private String handleMaterial;

    // Getters & Setters
    public String getKnifeType() {
        return knifeType;
    }

    public void setKnifeType(String knifeType) {
        this.knifeType = knifeType;
    }

    public double getBladeLength() {
        return bladeLength;
    }

    public void setBladeLength(double bladeLength) {
        this.bladeLength = bladeLength;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBladeMaterial() {
        return bladeMaterial;
    }

    public void setBladeMaterial(String bladeMaterial) {
        this.bladeMaterial = bladeMaterial;
    }

    public String getHandleMaterial() {
        return handleMaterial;
    }

    public void setHandleMaterial(String handleMaterial) {
        this.handleMaterial = handleMaterial;
    }
}
