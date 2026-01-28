package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.content.ContentResponse;
import com.kesik.bladecommerce.dto.content.ContentUpdateRequest;
import com.kesik.bladecommerce.entity.ContentKey;
import com.kesik.bladecommerce.entity.CorporateContent;
import com.kesik.bladecommerce.service.CorporateContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class CorporateContentController {

    private final CorporateContentService service;

    // --- PUBLIC ENDPOINTS ---

    @GetMapping("/public/{key}")
    public ResponseEntity<ContentResponse> getPublicContent(@PathVariable ContentKey key) {
        return ResponseEntity.ok(service.getPublicContent(key));
    }

    @GetMapping("/public/all")
    public ResponseEntity<List<CorporateContent>> getAllPublicContents() {
        return ResponseEntity.ok(service.getAllPublicContents());
    }

    // --- ADMIN ENDPOINTS ---

    @GetMapping("/admin/all")
    public ResponseEntity<List<CorporateContent>> getAllContentsForAdmin() {
        return ResponseEntity.ok(service.getAllContentsForAdmin());
    }

    @PutMapping("/admin/{key}")
    public ResponseEntity<CorporateContent> updateContent(
            @PathVariable ContentKey key,
            @RequestBody ContentUpdateRequest request) {
        return ResponseEntity.ok(service.updateContent(key, request));
    }
}