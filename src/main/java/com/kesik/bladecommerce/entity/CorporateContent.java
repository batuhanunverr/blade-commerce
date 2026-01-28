package com.kesik.bladecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "content")
public class CorporateContent {

    @Id
    private String id;

    @Indexed(unique = true)
    private ContentKey key; // İçeriğin türünü belirleyen unique anahtar

    private String title;   // Örn: "Kargo ve Teslimat"

    private String content; // HTML veya düz metin içerik

    @Builder.Default
    private String language = "TR"; // İleride EN, DE eklenebilir

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}