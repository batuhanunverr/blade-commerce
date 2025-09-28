package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.PaginatedResponse;
import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/knives")
public class KnifeController {

    private final KnifeService knifeService;

    public KnifeController(KnifeService knifeService) {
        this.knifeService = knifeService;
    }

    // Get all knives or search with filters - with 1-based pagination
    @GetMapping
    public PaginatedResponse<KnifeDto> getKnives(@RequestParam(required = false) String searchTerm,
                                                @RequestParam(required = false) Integer categoryId,
                                                @RequestParam(required = false) Double minPrice,
                                                @RequestParam(required = false) Double maxPrice,
                                                @RequestParam(required = false) String knifeType,
                                                @RequestParam(required = false) String bladeMaterial,
                                                @RequestParam(defaultValue = "asc") String sortDirection,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {

        // Page is already 0-based from frontend conversion
        Pageable pageable = PageRequest.of(page, size);

        // Get paginated results from service
        Page<KnifeDto> knivePage = knifeService.searchKnivesPageable(
            searchTerm, categoryId, minPrice, maxPrice,
            knifeType, bladeMaterial, sortDirection, pageable
        );

        // Return 1-based pagination response (auto-converts 0-based Spring Boot page to 1-based)
        return PaginatedResponse.fromPage(knivePage);
    }

    // Get single knife by ID
    @GetMapping("/{id}")
    public KnifeDto getKnifeById(@PathVariable String id) {
        return knifeService.getKnifeById(id);
    }

    // Get knife by name (special endpoint)
    @GetMapping("/name/{name}")
    public KnifeDto getKnifeByName(@PathVariable String name) {
        return knifeService.getKnifeByName(name);
    }

    // Create new knife
    @PostMapping
    public KnifeDto createKnife(@Valid @RequestBody AddKnifeRequestDto knifeDto) {
        return knifeService.addKnife(knifeDto);
    }

    // Update existing knife
    @PutMapping("/{id}")
    public KnifeDto updateKnife(@PathVariable String id, @Valid @RequestBody UpdateKnifeRequestDto knifeDto) {
        return knifeService.updateKnife(id, knifeDto);
    }

    // Delete knife
    @DeleteMapping("/{id}")
    public void deleteKnife(@PathVariable String id) {
        knifeService.deleteKnife(id);
    }

    // Get all knife types (utility endpoint)
    @GetMapping("/types")
    public List<String> getKnifeTypes() {
        return knifeService.getKnifeTypes();
    }

    // Get knife count by category (utility endpoint)
    @GetMapping("/count")
    public int getKnifeCountByCategory(@RequestParam String categoryId) {
        return knifeService.getKnifeCountByCategory(categoryId);
    }
}