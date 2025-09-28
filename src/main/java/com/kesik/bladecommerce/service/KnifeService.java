package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KnifeService {
    List<KnifeDto> getAllKnives();

    List<KnifeDto> searchKnives(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                String knifeType, String bladeMaterial, String sortDirection, int page, int size);

    Page<KnifeDto> searchKnivesPageable(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                       String knifeType, String bladeMaterial, String sortDirection, Pageable pageable);

    KnifeDto getKnifeById(String id);

    KnifeDto getKnifeByName(String name);

    KnifeDto addKnife(AddKnifeRequestDto knifeDto);

    KnifeDto updateKnife(String id, UpdateKnifeRequestDto knifeDto);

    KnifeDto updateKnifeStockQuantity(String id, int quantity);

    void deleteKnife(String id);

    List<String> getKnifeTypes();

    int getKnifeCountByCategory(String categoryId);
}