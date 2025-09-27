package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.service.CategoryService;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {
    private final CategoryService categoryService;
    private final KnifeService knifeService;

    public AdminStatsController(CategoryService categoryService, KnifeService knifeService) {
        this.categoryService = categoryService;
        this.knifeService = knifeService;
    }

    @GetMapping("/category-product-counts")
    public List<Map<String, Object>> getCategoryProductCounts() {
        return categoryService.getAllCategories().stream()
            .map(category -> {
                Map<String, Object> stat = new HashMap<>();
                stat.put("categoryId", category.getCategoryId());
                stat.put("productCount", knifeService.getKnifeCountByCategory(category.getCategoryId().toString()));
                return stat;
            })
            .toList();
    }

}