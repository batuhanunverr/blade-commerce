package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.service.KnifeService;
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

    // Get all knives or search with filters
    @GetMapping
    public List<KnifeDto> getKnives(@RequestParam(required = false) String searchTerm,
                                   @RequestParam(required = false) Integer categoryId,
                                   @RequestParam(required = false) Double minPrice,
                                   @RequestParam(required = false) Double maxPrice,
                                   @RequestParam(required = false) String knifeType,
                                   @RequestParam(required = false) String bladeMaterial,
                                   @RequestParam(defaultValue = "asc") String sortDirection,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        // If no search parameters, return all knives
        if (searchTerm == null && categoryId == null && minPrice == null && maxPrice == null
            && knifeType == null && bladeMaterial == null) {
            return knifeService.getAllKnives();
        }
        return knifeService.searchKnives(searchTerm, categoryId, minPrice, maxPrice, knifeType, bladeMaterial, sortDirection, page, size);
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
        knifeDto.setId(id); // Ensure ID matches path
        return knifeService.updateKnife(knifeDto);
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