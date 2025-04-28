package com.kesik.bladecommerce.dto.knife;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
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
