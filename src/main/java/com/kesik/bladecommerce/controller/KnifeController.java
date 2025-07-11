package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knives")
public class KnifeController {

    private final KnifeService knifeService;

    public KnifeController(KnifeService knifeService) {
        this.knifeService = knifeService;
    }

    @GetMapping(path = "/getAllKnives")
    public List<KnifeDto> getAllKnives() {
        return knifeService.getAllKnives();
    }
    @GetMapping(path = "/search")
    public List<KnifeDto> searchKnives(@RequestParam(required = false) String searchTerm,
                                       @RequestParam(required = false) Integer categoryId,
                                       @RequestParam(required = false) Double minPrice,
                                       @RequestParam(required = false) Double maxPrice,
                                       @RequestParam(required = false) String knifeType,
                                       @RequestParam(required = false) String bladeMaterial,
                                       @RequestParam(defaultValue = "asc") String sortDirection,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        return knifeService.searchKnives(searchTerm, categoryId, minPrice, maxPrice, knifeType, bladeMaterial, sortDirection, page, size);
    }
    @GetMapping(path = "/getKnifeById")
    public KnifeDto getKnifeById(@RequestParam String id) {
        return knifeService.getKnifeById(id);
    }
    @GetMapping(path = "/getKnifeByName")
    public KnifeDto getKnifeByName(@RequestParam String name) {
        return knifeService.getKnifeByName(name);
    }
    @PostMapping(path = "/addKnife")
    public KnifeDto addKnife(@RequestBody AddKnifeRequestDto knifeDto) {
        return knifeService.addKnife(knifeDto);
    }
    @PostMapping(path = "/updateKnife")
    public KnifeDto updateKnife(@RequestBody UpdateKnifeRequestDto knifeDto) {
        return knifeService.updateKnife(knifeDto);
    }
    @DeleteMapping(path = "/deleteKnife")
    public void deleteKnife(@RequestParam String id) {
        knifeService.deleteKnife(id);
    }
    @GetMapping(path = "/getKnifeTypes")
    public List<String> getKnifeTypes() {
        return knifeService.getKnifeTypes();
    }
    public int getKnifeCountByCategory(String categoryId) {
        return knifeService.getKnifeCountByCategory(categoryId);
    }
}