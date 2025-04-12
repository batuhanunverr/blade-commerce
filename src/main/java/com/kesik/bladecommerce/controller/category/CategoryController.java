package com.kesik.bladecommerce.controller.category;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CategoryDto getCategoryById(Integer id) {
        return categoryService.getCategoryById(id);
    }
    @GetMapping(path = "/addCategory")
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }
    @GetMapping(path = "/updateCategory")
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }
    @GetMapping(path = "/deleteCategory")
    public void deleteCategory(Integer id) {
        categoryService.deleteCategory(id);
    }
}
