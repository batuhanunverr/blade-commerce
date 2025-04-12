package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.knife.KnifeDto;

import java.util.List;

public interface KnifeService {
    List<KnifeDto> getAllKnives();
    List<KnifeDto> searchKnives(String searchTerm);

    KnifeDto getKnifeById(String id);

    KnifeDto getKnifeByName(String name);

    KnifeDto addKnife(KnifeDto knifeDto);

    KnifeDto updateKnife(KnifeDto knifeDto);

    void deleteKnife(String id);
}
