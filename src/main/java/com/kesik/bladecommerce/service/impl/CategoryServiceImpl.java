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
    public CategoryDto getCategoryById(Integer id) {
        return categoryRepository.findById(String.valueOf(id)).orElse(null);
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return categoryRepository.save(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        CategoryDto existingCategory = categoryRepository.findById(String.valueOf(categoryDto.getId())).orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(categoryDto.getName());
            return categoryRepository.save(existingCategory);
        }
        return null;
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(String.valueOf(id));
    }
}
