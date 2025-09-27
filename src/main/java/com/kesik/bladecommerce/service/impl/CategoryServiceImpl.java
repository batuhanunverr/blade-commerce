package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import com.kesik.bladecommerce.repository.category.CategoryRepository;
import com.kesik.bladecommerce.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryDto getCategoryById(Integer categoryId) {
        return categoryRepository.findByCategoryId(categoryId).orElse(null);
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        // Find the maximum categoryId and add 1 to ensure uniqueness
        List<CategoryDto> allCategories = categoryRepository.findAll();
        Integer maxId = allCategories.stream()
                .filter(cat -> cat.getCategoryId() != null)
                .mapToInt(CategoryDto::getCategoryId)
                .max()
                .orElse(0);
        categoryDto.setCategoryId(maxId + 1);
        return categoryRepository.save(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        CategoryDto existingCategory = categoryRepository.findByCategoryId(categoryDto.getCategoryId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setCategoryName(categoryDto.getCategoryName());
            return categoryRepository.save(existingCategory);
        }
        return null;
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteByCategoryId(categoryId);
    }
}
