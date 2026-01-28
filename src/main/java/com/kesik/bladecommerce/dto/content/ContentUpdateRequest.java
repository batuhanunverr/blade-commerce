package com.kesik.bladecommerce.dto.content;

import com.kesik.bladecommerce.entity.ContentKey;
import lombok.Data;

@Data
public class ContentUpdateRequest {
    private String title;
    private String content;
    private boolean active;
}