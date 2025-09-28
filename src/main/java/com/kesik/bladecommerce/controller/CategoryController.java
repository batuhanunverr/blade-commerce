package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import com.kesik.bladecommerce.service.CategoryService;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final KnifeService knifeService;

    public CategoryController(CategoryService categoryService, KnifeService knifeService) {
        this.categoryService = categoryService;
        this.knifeService = knifeService;
    }
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Integer categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @PutMapping("/{categoryId}")
    public CategoryDto updateCategory(@PathVariable Integer categoryId, @RequestBody CategoryDto categoryDto) {
        categoryDto.setCategoryId(categoryId);
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    // Optimized endpoint: Get all categories with their product counts in one request
    @GetMapping("/with-counts")
    public Map<String, Object> getCategoriesWithCounts() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        Map<String, Object> result = new HashMap<>();

        // Create categories with counts
        List<Map<String, Object>> categoriesWithCounts = categories.stream()
            .map(category -> {
                Map<String, Object> categoryWithCount = new HashMap<>();
                categoryWithCount.put("categoryId", category.getCategoryId());
                categoryWithCount.put("categoryName", category.getCategoryName());
                categoryWithCount.put("productCount", knifeService.getKnifeCountByCategory(String.valueOf(category.getCategoryId())));
                return categoryWithCount;
            })
            .toList();

        result.put("categories", categoriesWithCounts);
        result.put("totalCategories", categories.size());

        return result;
    }
}
