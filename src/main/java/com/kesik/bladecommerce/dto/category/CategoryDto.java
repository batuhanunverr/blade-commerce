package com.kesik.bladecommerce.dto.category;


import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "knife_categories")
public class CategoryDto {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
