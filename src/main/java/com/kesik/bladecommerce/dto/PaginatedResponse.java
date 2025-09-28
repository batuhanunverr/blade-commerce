package com.kesik.bladecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Centralized pagination response that matches frontend PaginatedResponse interface
 * Supports both 1-based and 0-based pagination modes with automatic conversion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private int page;           // Page number (1-based for user-friendly pagination)
    private int size;           // Number of items per page
    private long totalElements; // Total number of items across all pages
    private int totalPages;     // Total number of pages
    private int numberOfElements; // Number of items in current page
    private boolean first;      // Whether this is the first page
    private boolean last;       // Whether this is the last page
    private boolean empty;      // Whether the content is empty

    /**
     * Create PaginatedResponse from Spring Boot Page with 1-based page numbering
     * Converts Spring Boot's 0-based page numbering to user-friendly 1-based numbering
     */
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1)  // Convert 0-based to 1-based
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Create PaginatedResponse from Spring Boot Page with custom page numbering
     * Use this when you want to control the page numbering mode
     */
    public static <T> PaginatedResponse<T> fromPage(Page<T> page, int userRequestedPage) {
        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .page(userRequestedPage)  // Use the original user-requested page number
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}