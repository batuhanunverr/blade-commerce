package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import com.kesik.bladecommerce.repository.category.CategoryRepository;
import com.kesik.bladecommerce.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        // Return all categories sorted by displayOrder
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        cat -> cat.getDisplayOrder() != null ? cat.getDisplayOrder() : Integer.MAX_VALUE
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getActiveCategories() {
        // Return only active categories sorted by displayOrder
        return categoryRepository.findAll().stream()
                .filter(cat -> cat.getIsActive() == null || cat.getIsActive()) // null defaults to active
                .sorted(Comparator.comparing(
                        cat -> cat.getDisplayOrder() != null ? cat.getDisplayOrder() : Integer.MAX_VALUE
                ))
                .collect(Collectors.toList());
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

        // Set default values for new fields if not provided
        if (categoryDto.getIsActive() == null) {
            categoryDto.setIsActive(true);
        }
        if (categoryDto.getDisplayOrder() == null) {
            // Auto-assign displayOrder as max + 1 (place at end)
            Integer maxOrder = allCategories.stream()
                    .filter(cat -> cat.getDisplayOrder() != null)
                    .mapToInt(CategoryDto::getDisplayOrder)
                    .max()
                    .orElse(0);
            categoryDto.setDisplayOrder(maxOrder + 1);
        }

        return categoryRepository.save(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        CategoryDto existingCategory = categoryRepository.findByCategoryId(categoryDto.getCategoryId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setCategoryName(categoryDto.getCategoryName());

            // Update new fields if provided
            if (categoryDto.getDescription() != null) {
                existingCategory.setDescription(categoryDto.getDescription());
            }
            if (categoryDto.getIcon() != null) {
                existingCategory.setIcon(categoryDto.getIcon());
            }
            if (categoryDto.getDisplayOrder() != null) {
                existingCategory.setDisplayOrder(categoryDto.getDisplayOrder());
            }
            if (categoryDto.getIsActive() != null) {
                existingCategory.setIsActive(categoryDto.getIsActive());
            }

            return categoryRepository.save(existingCategory);
        }
        return null;
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteByCategoryId(categoryId);
    }

    @Override
    public CategoryDto toggleCategoryActive(Integer categoryId) {
        CategoryDto category = categoryRepository.findByCategoryId(categoryId).orElse(null);
        if (category != null) {
            // Toggle the isActive field
            Boolean currentStatus = category.getIsActive();
            category.setIsActive(currentStatus == null || !currentStatus);
            return categoryRepository.save(category);
        }
        return null;
    }

    @Override
    public List<CategoryDto> reorderCategories(List<Integer> categoryIds) {
        // Update displayOrder for each category based on position in the list
        for (int i = 0; i < categoryIds.size(); i++) {
            Integer categoryId = categoryIds.get(i);
            CategoryDto category = categoryRepository.findByCategoryId(categoryId).orElse(null);
            if (category != null) {
                category.setDisplayOrder(i + 1); // 1-indexed
                categoryRepository.save(category);
            }
        }
        // Return the updated sorted list
        return getAllCategories();
    }
}
