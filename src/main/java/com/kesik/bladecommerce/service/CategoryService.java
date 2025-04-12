package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Integer id);

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Integer id);
}
