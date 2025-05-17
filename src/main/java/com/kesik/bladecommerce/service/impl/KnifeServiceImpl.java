package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import com.kesik.bladecommerce.service.CloudinaryService;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class KnifeServiceImpl implements KnifeService {
    private final KnifeRepository knifeRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;

    public KnifeServiceImpl(KnifeRepository knifeRepository, MongoTemplate mongoTemplate, CloudinaryService cloudinaryService) {
        this.knifeRepository = knifeRepository;
        this.mongoTemplate = mongoTemplate;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public List<KnifeDto> getAllKnives() {
        return knifeRepository.findAll();
    }
    @Override
    public List<KnifeDto> searchKnives(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                       String knifeType, String bladeMaterial, String sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, sortDirection.equalsIgnoreCase("asc") ? Sort.by("price").ascending() : Sort.by("price").descending());

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

        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        Query query = new Query(criteria).with(pageable);

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
    public KnifeDto addKnife(AddKnifeRequestDto knifeDto) {
        KnifeDto newKnife = new KnifeDto();
        newKnife.setName(knifeDto.getName());
        newKnife.setDescription(knifeDto.getDescription());
        newKnife.setPrice(knifeDto.getPrice());
        newKnife.setCategoryId(knifeDto.getCategoryId());
        newKnife.setTags(knifeDto.getTags());
        newKnife.setStockQuantity(knifeDto.getStockQuantity());
        newKnife.setDiscountPrice(knifeDto.getDiscountPrice());
        newKnife.setKnifeType(knifeDto.getKnifeType());
        newKnife.setBladeMaterial(knifeDto.getBladeMaterial());
        newKnife.setHandleMaterial(knifeDto.getHandleMaterial());
        newKnife.setBladeLength(knifeDto.getBladeLength());
        try {
            newKnife.setImageUrl(cloudinaryService.uploadFile(knifeDto.getImageFile()));
        } catch (IOException e) {
            throw new RuntimeException("error uploading image to server " + e);
        }
        return knifeRepository.save(newKnife);
    }

    @Override
    public KnifeDto updateKnife(UpdateKnifeRequestDto knifeDto) {
        KnifeDto existingKnife = knifeRepository.findById(knifeDto.getId()).orElse(null);
        if (existingKnife != null) {
            existingKnife.setName(knifeDto.getName());
            existingKnife.setDescription(knifeDto.getDescription());
            existingKnife.setPrice(knifeDto.getPrice());
            existingKnife.setCategoryId(knifeDto.getCategoryId());
            existingKnife.setTags(knifeDto.getTags());
            existingKnife.setDiscountPrice(knifeDto.getDiscountPrice());
            existingKnife.setStockQuantity(knifeDto.getStockQuantity());
            existingKnife.setTags(knifeDto.getTags());
            existingKnife.setKnifeType(knifeDto.getKnifeType());
            existingKnife.setBladeMaterial(knifeDto.getBladeMaterial());
            existingKnife.setHandleMaterial(knifeDto.getHandleMaterial());
            existingKnife.setBladeLength(knifeDto.getBladeLength());
            existingKnife.setColor(knifeDto.getColor());
            if (knifeDto.getImageFile() != null && !knifeDto.getImageFile().isEmpty()) {
                try {
                    existingKnife.setImageUrl(cloudinaryService.uploadFile(knifeDto.getImageFile()));
                } catch (IOException e) {
                    throw new RuntimeException("error uploading image to server " + e);
                }
            }
            return knifeRepository.save(existingKnife);
        }
        return null;
    }

    @Override
    public KnifeDto updateKnifeStockQuantity(String id, int quantity) {
        KnifeDto knife = knifeRepository.findById(id).orElse(null);
        if (knife != null) {
            knife.setStockQuantity(quantity);
            return knifeRepository.save(knife);
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
          knifeTypes.add(knife.getKnifeType());
        }
        return knifeTypes.stream().distinct().toList();
    }
}