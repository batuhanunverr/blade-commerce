package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();

    List<CategoryDto> getActiveCategories();  // Get only active categories, sorted by displayOrder

    CategoryDto getCategoryById(Integer categoryId);

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Integer categoryId);

    CategoryDto toggleCategoryActive(Integer categoryId);  // Toggle isActive status

    List<CategoryDto> reorderCategories(List<Integer> categoryIds);  // Reorder by providing ordered list of IDs
}
