package com.kesik.bladecommerce.dto.content;

import com.kesik.bladecommerce.entity.ContentKey;
import lombok.Data;

@Data
public class ContentResponse {
    private ContentKey key;
    private String title;
    private String content;
}