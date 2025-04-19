package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnifeServiceImpl implements KnifeService {
    private final KnifeRepository knifeRepository;
    private final MongoTemplate mongoTemplate;

    public KnifeServiceImpl(KnifeRepository knifeRepository, MongoTemplate mongoTemplate) {
        this.knifeRepository = knifeRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<KnifeDto> getAllKnives() {
        return knifeRepository.findAll();
    }
    @Override
    public List<KnifeDto> searchKnives(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                       String knifeType, String bladeMaterial, String sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sortDirection.equalsIgnoreCase("asc") ? Sort.by("price").ascending() : Sort.by("price").descending());

        // Criteria ile sorgu oluştur
        List<Criteria> criteriaList = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isBlank()) {
            criteriaList.add(Criteria.where("name").regex(searchTerm, "i"));
        }
        if (categoryId != null) {
            criteriaList.add(Criteria.where("categoryId").is(categoryId));
        }
        if (minPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            criteriaList.add(Criteria.where("price").lte(maxPrice));
        }
        if (knifeType != null && !knifeType.isBlank()) {
            criteriaList.add(Criteria.where("knifeDetails.knifeType").is(knifeType));
        }
        if (bladeMaterial != null && !bladeMaterial.isBlank()) {
            criteriaList.add(Criteria.where("knifeDetails.bladeMaterial").is(bladeMaterial));
        }

        // Criteria'ları birleştir
        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        // Query oluştur
        Query query = new Query(criteria).with(pageable);

        // MongoTemplate ile sorguyu çalıştır
        List<KnifeDto> knives = mongoTemplate.find(query, KnifeDto.class);

        return knives;
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

    @Override
    public List<String> getKnifeTypes() {
        List<KnifeDto> knives = knifeRepository.findAll();
        List<String> knifeTypes = new ArrayList<>();
        for (KnifeDto knife : knives) {
            if (knife.getKnifeDetails() != null && knife.getKnifeDetails().getKnifeType() != null) {
                knifeTypes.add(knife.getKnifeDetails().getKnifeType().toLowerCase());
            }
        }
        return knifeTypes.stream().distinct().toList();
    }
}