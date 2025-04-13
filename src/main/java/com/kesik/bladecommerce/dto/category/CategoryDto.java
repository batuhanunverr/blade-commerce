package com.kesik.bladecommerce.dto.category;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "knife_categories")
public class CategoryDto {
    private String  id;
    private String categoryName;
    private Integer categoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
