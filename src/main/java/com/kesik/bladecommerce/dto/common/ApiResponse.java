package com.kesik.bladecommerce.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for consistent response format across all endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if the request was successful
     */
    private Boolean success;

    /**
     * Human-readable message describing the result
     */
    private String message;

    /**
     * Response data (null for errors or operations without data)
     */
    private T data;

    /**
     * Error details (only present for failed requests)
     */
    private ErrorDetails error;

    /**
     * Timestamp of the response
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Error details structure
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        /**
         * Error code for client-side handling
         */
        private String code;

        /**
         * Detailed error message
         */
        private String details;

        /**
         * Field-level validation errors (for form validation)
         */
        private java.util.Map<String, String> fieldErrors;
    }

    // Convenience factory methods

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, ErrorDetails errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errorDetails)
                .build();
    }
}
