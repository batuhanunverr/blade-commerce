package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import com.kesik.bladecommerce.service.CategoryService;
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
import java.util.Objects;

@Service
public class KnifeServiceImpl implements KnifeService {
    private final KnifeRepository knifeRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;

    public KnifeServiceImpl(KnifeRepository knifeRepository, MongoTemplate mongoTemplate, CloudinaryService cloudinaryService, CategoryService categoryService) {
        this.knifeRepository = knifeRepository;
        this.mongoTemplate = mongoTemplate;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
    }

    @Override
    public List<KnifeDto> getAllKnives() {
        return knifeRepository.findAll();
    }

    @Override
    public List<KnifeDto> searchKnives(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                       String knifeType, String bladeMaterial, String sortDirection, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, getSort(sortDirection));
            Query query = new Query().with(pageable);
            List<Criteria> criteriaList = buildSearchCriteria(searchTerm, categoryId, minPrice, maxPrice, knifeType, bladeMaterial);

            if (!criteriaList.isEmpty()) {
                query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
            }

            return mongoTemplate.find(query, KnifeDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error searching knives: " + e.getMessage(), e);
        }
    }

    private Sort getSort(String sortDirection) {
        return "asc".equalsIgnoreCase(sortDirection) ? Sort.by("price").ascending() : Sort.by("price").descending();
    }

    private List<Criteria> buildSearchCriteria(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                               String knifeType, String bladeMaterial) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (searchTerm != null && !searchTerm.isEmpty()) {
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
        if (knifeType != null && !knifeType.isEmpty()) {
            criteriaList.add(Criteria.where("knifeType").is(knifeType));
        }
        if (bladeMaterial != null && !bladeMaterial.isEmpty()) {
            criteriaList.add(Criteria.where("bladeMaterial").is(bladeMaterial));
        }
        return criteriaList;
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
        KnifeDto newKnife = mapAddKnifeRequestToDto(knifeDto);

        try {
            String imageUrl = null;

            if (knifeDto.getImageFile() != null && !knifeDto.getImageFile().isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(knifeDto.getImageFile());
            } else if (knifeDto.getImageBase64() != null && !knifeDto.getImageBase64().isEmpty()) {
                imageUrl = cloudinaryService.uploadBase64(knifeDto.getImageBase64());
            }

            newKnife.setImageUrl(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to server", e);
        }

        return knifeRepository.save(newKnife);
    }


    private KnifeDto mapAddKnifeRequestToDto(AddKnifeRequestDto knifeDto) {
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
        newKnife.setColor(knifeDto.getColor());
        newKnife.setKnifeSizes(knifeDto.getKnifeSizes());
        newKnife.setCategoryName(categoryService.getCategoryById(knifeDto.getCategoryId()) != null ?
                categoryService.getCategoryById(knifeDto.getCategoryId()).getCategoryName() : null);
        return newKnife;
    }

    @Override
    public KnifeDto updateKnife(String id, UpdateKnifeRequestDto knifeDto) {
        KnifeDto existingKnife = knifeRepository.findById(id).orElse(null);
        if (existingKnife == null) return null;

        updateKnifeFields(existingKnife, knifeDto);

        try {
            String imageUrl = null;

            if (knifeDto.getImageFile() != null && !knifeDto.getImageFile().isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(knifeDto.getImageFile());
            } else if (knifeDto.getImageBase64() != null && !knifeDto.getImageBase64().isEmpty()) {
                imageUrl = cloudinaryService.uploadBase64(knifeDto.getImageBase64());
            }

            if (imageUrl != null) {
                existingKnife.setImageUrl(imageUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to server", e);
        }

        return knifeRepository.save(existingKnife);
    }

    private void updateKnifeFields(KnifeDto knife, UpdateKnifeRequestDto dto) {
        knife.setName(dto.getName());
        knife.setDescription(dto.getDescription());
        knife.setPrice(dto.getPrice());
        knife.setCategoryId(dto.getCategoryId());
        knife.setTags(dto.getTags());
        knife.setDiscountPrice(dto.getDiscountPrice());
        knife.setStockQuantity(dto.getStockQuantity());
        knife.setKnifeType(dto.getKnifeType());
        knife.setBladeMaterial(dto.getBladeMaterial());
        knife.setHandleMaterial(dto.getHandleMaterial());
        knife.setBladeLength(dto.getBladeLength());
        knife.setColor(dto.getColor());
        knife.setKnifeSizes(dto.getKnifeSizes());
        knife.setCategoryName(categoryService.getCategoryById(dto.getCategoryId()) != null ?
                categoryService.getCategoryById(dto.getCategoryId()).getCategoryName() : null);
    }

    @Override
    public KnifeDto updateKnifeStockQuantity(String id, int quantity) {
        KnifeDto knife = knifeRepository.findById(id).orElse(null);
        if (knife == null) return null;
        knife.setStockQuantity(quantity);
        return knifeRepository.save(knife);
    }

    @Override
    public void deleteKnife(String id) {
        knifeRepository.deleteById(id);
    }

    @Override
    public List<String> getKnifeTypes() {
        return knifeRepository.findAll().stream()
                .map(KnifeDto::getKnifeType)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Override
    public int getKnifeCountByCategory(String categoryId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryId").is(categoryId));
        return (int) mongoTemplate.count(query, KnifeDto.class);
    }
}