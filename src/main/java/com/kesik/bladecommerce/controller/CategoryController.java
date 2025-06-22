package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import com.kesik.bladecommerce.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping(path = "/getAllCategories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
    @GetMapping(path = "/getCategoryById")
    public CategoryDto getCategoryById(Integer categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
    @PostMapping(path = "/addCategory")
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }
    @PostMapping(path = "/updateCategory")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }
    @GetMapping(path = "/deleteCategory")
    public void deleteCategory(Integer categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
