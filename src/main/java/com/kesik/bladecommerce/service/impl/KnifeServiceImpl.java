package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.knife.AddKnifeRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.knife.UpdateKnifeRequestDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import com.kesik.bladecommerce.service.CategoryService;
import com.kesik.bladecommerce.service.CloudinaryService;
import com.kesik.bladecommerce.service.KnifeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@Slf4j
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

    @Override
    public Page<KnifeDto> searchKnivesPageable(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                              String knifeType, String bladeMaterial, String sortDirection, Pageable pageable) {
        try {
            Query query = new Query();
            List<Criteria> criteriaList = buildSearchCriteria(searchTerm, categoryId, minPrice, maxPrice, knifeType, bladeMaterial);

            if (!criteriaList.isEmpty()) {
                query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
            }

            // Add sorting
            query.with(getSort(sortDirection));

            // Get total count for pagination
            long total = mongoTemplate.count(query, KnifeDto.class);

            // Apply pagination
            query.with(pageable);

            // Execute query
            List<KnifeDto> knives = mongoTemplate.find(query, KnifeDto.class);

            return new PageImpl<>(knives, pageable, total);
        } catch (Exception e) {
            throw new RuntimeException("Error searching knives with pagination: " + e.getMessage(), e);
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
        // Validate and normalize pricing
        validateAndNormalizePricing(knifeDto);

        KnifeDto newKnife = mapAddKnifeRequestToDto(knifeDto);

        // Handle image upload
        String imageUrl = handleImageUpload(knifeDto.getImageFile(), knifeDto.getImageBase64());
        newKnife.setImageUrl(imageUrl);

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
        newKnife.setPercentageDiscount(knifeDto.isPercentageDiscount());
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

        // Validate and normalize pricing
        validateAndNormalizePricing(knifeDto);

        updateKnifeFields(existingKnife, knifeDto);

        // Handle image upload
        String imageUrl = handleImageUpload(knifeDto.getImageFile(), knifeDto.getImageBase64());
        if (imageUrl != null) {
            existingKnife.setImageUrl(imageUrl);
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
        knife.setPercentageDiscount(dto.isPercentageDiscount());
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

    /**
     * Atomically decrement stock quantity using MongoDB's findAndModify.
     * This prevents overselling in concurrent scenarios.
     *
     * @param id Product ID
     * @param decrementBy Amount to decrement (must be positive)
     * @return Updated KnifeDto if successful, null if insufficient stock or product not found
     * @throws IllegalArgumentException if decrementBy is not positive
     * @throws IllegalStateException if insufficient stock
     */
    @Override
    public KnifeDto decrementStockAtomic(String id, int decrementBy) {
        if (decrementBy <= 0) {
            throw new IllegalArgumentException("Decrement amount must be positive");
        }

        // Use atomic findAndModify to check and decrement in one operation
        // This ensures no race condition between check and update
        Query query = new Query(Criteria.where("_id").is(id)
                .and("stockQuantity").gte(decrementBy));

        org.springframework.data.mongodb.core.query.Update update =
                new org.springframework.data.mongodb.core.query.Update()
                        .inc("stockQuantity", -decrementBy);

        org.springframework.data.mongodb.core.FindAndModifyOptions options =
                org.springframework.data.mongodb.core.FindAndModifyOptions.options()
                        .returnNew(true);

        KnifeDto updatedKnife = mongoTemplate.findAndModify(query, update, options, KnifeDto.class);

        if (updatedKnife == null) {
            // Either product doesn't exist or insufficient stock
            KnifeDto existing = knifeRepository.findById(id).orElse(null);
            if (existing == null) {
                throw new IllegalArgumentException("Product not found: " + id);
            } else {
                throw new IllegalStateException(
                        String.format("Insufficient stock for %s. Requested: %d, Available: %d",
                                existing.getName(), decrementBy, existing.getStockQuantity()));
            }
        }

        return updatedKnife;
    }

    /**
     * Atomically restore stock quantity (rollback operation).
     * Used when order creation fails after stock was decremented.
     *
     * @param id Product ID
     * @param incrementBy Amount to restore
     */
    @Override
    public void incrementStockAtomic(String id, int incrementBy) {
        if (incrementBy <= 0) {
            return;
        }

        org.springframework.data.mongodb.core.query.Update update =
                new org.springframework.data.mongodb.core.query.Update()
                        .inc("stockQuantity", incrementBy);

        mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(id)),
                update,
                KnifeDto.class
        );
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
        query.addCriteria(Criteria.where("categoryId").is(Integer.parseInt(categoryId)));
        return (int) mongoTemplate.count(query, KnifeDto.class);
    }

    /**
     * Handles image upload for both file and base64 formats
     * @param imageFile MultipartFile from form upload
     * @param imageBase64 Base64 encoded image string
     * @return Uploaded image URL, or null if no image provided
     */
    private String handleImageUpload(org.springframework.web.multipart.MultipartFile imageFile, String imageBase64) {
        try {
            String imageUrl = null;

            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(imageFile);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                imageUrl = cloudinaryService.uploadBase64(imageBase64);
            }

            return imageUrl;
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Error uploading image to server", e);
        }
    }

    /**
     * Validates and normalizes pricing to ensure data integrity
     * - If discountPrice >= price: Set discountPrice = price (no discount)
     * - If discountPrice < 0: Throw validation error
     * - If price <= 0: Throw validation error
     */
    private void validateAndNormalizePricing(Object dto) {
        double price;
        double discountPrice;

        // Extract price values using reflection to work with both AddKnifeRequestDto and UpdateKnifeRequestDto
        try {
            price = (Double) dto.getClass().getMethod("getPrice").invoke(dto);
            discountPrice = (Double) dto.getClass().getMethod("getDiscountPrice").invoke(dto);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid DTO structure for price validation");
        }

        // Validate price constraints
        if (price <= 0) {
            throw new IllegalArgumentException("Regular price must be greater than 0");
        }

        if (discountPrice < 0) {
            throw new IllegalArgumentException("Discount price cannot be negative");
        }

        // Normalize: if discount price >= regular price, set them equal (no discount)
        if (discountPrice >= price) {
            try {
                dto.getClass().getMethod("setDiscountPrice", double.class).invoke(dto, price);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to normalize discount price");
            }
        }
    }
}