package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnifeServiceImpl implements KnifeService {
    private final KnifeRepository knifeRepository;

    public KnifeServiceImpl(KnifeRepository knifeRepository) {
        this.knifeRepository = knifeRepository;
    }

    @Override
    public List<KnifeDto> getAllKnives() {
        return knifeRepository.findAll();
    }
    @Override
    public List<KnifeDto> searchKnives(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return knifeRepository.searchKnives(searchTerm, pageable).getContent();
    }
    @Override
    public KnifeDto getKnifeById(String id) {
        return knifeRepository.findById(id).orElse(null);
    }
    @Override
    public KnifeDto getKnifeByName(String name) {
        return knifeRepository.getKnifeByName(name);
    }
    @Override
    public KnifeDto addKnife(KnifeDto knifeDto) {
        return knifeRepository.save(knifeDto);
    }

    @Override
    public KnifeDto updateKnife(KnifeDto knifeDto) {
        KnifeDto existingKnife = knifeRepository.findById(knifeDto.getId()).orElse(null);
        if (existingKnife != null) {
            existingKnife.setName(knifeDto.getName());
            existingKnife.setDescription(knifeDto.getDescription());
            existingKnife.setPrice(knifeDto.getPrice());
            existingKnife.setCategoryId(knifeDto.getCategoryId());
            existingKnife.setTags(knifeDto.getTags());
            return knifeRepository.save(existingKnife);
        }
        return null;
    }
    @Override
    public void deleteKnife(String id) {
        knifeRepository.deleteById(id);
    }
}