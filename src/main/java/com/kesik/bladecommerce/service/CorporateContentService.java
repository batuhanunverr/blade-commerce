package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.content.ContentResponse;
import com.kesik.bladecommerce.dto.content.ContentUpdateRequest;
import com.kesik.bladecommerce.entity.ContentKey;
import com.kesik.bladecommerce.entity.CorporateContent;
import com.kesik.bladecommerce.repository.CorporateContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorporateContentService {

    private final CorporateContentRepository repository;

    // Public: Sadece aktif içeriği getir
    public ContentResponse getPublicContent(ContentKey key) {
        CorporateContent content = repository.findByKeyAndActiveTrue(key)
                .orElseThrow(() -> new RuntimeException("İçerik bulunamadı veya aktif değil: " + key));

        return mapToResponse(content);
    }

    // Public: Tüm aktif içerikleri getir
    public List<CorporateContent> getAllPublicContents() {
        return repository.findAllByActiveTrue();
    }

    // Admin: Tüm içerikleri listele
    public List<CorporateContent> getAllContentsForAdmin() {
        return repository.findAll();
    }

    // Admin: İçerik güncelle veya yoksa oluştur (Upsert)
    public CorporateContent updateContent(ContentKey key, ContentUpdateRequest request) {
        CorporateContent content = repository.findByKey(key)
                .orElse(CorporateContent.builder()
                        .key(key)
                        .createdAt(LocalDateTime.now())
                        .build());

        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        content.setActive(request.isActive());
        content.setUpdatedAt(LocalDateTime.now());

        return repository.save(content);
    }

    private ContentResponse mapToResponse(CorporateContent entity) {
        ContentResponse response = new ContentResponse();
        response.setKey(entity.getKey());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        return response;
    }
}