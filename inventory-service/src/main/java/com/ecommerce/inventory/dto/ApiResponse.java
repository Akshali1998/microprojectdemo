package com.ecommerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;      // human readable message
    private String status;       // OK or ERROR
    private int statusCode;      // HTTP status code
    private T data;              // payload
    private String error;        // error code or null
    private String exception;    // exception message or "NA"

    public static <T> ApiResponse<T> success(String message, int statusCode, T data) {
        return new ApiResponse<>(message, "OK", statusCode, data, null, "NA");
    }

    public static <T> ApiResponse<T> failure(String message, int statusCode, String error, String exception) {
        return new ApiResponse<>(message, "ERROR", statusCode, null, error, exception == null ? "" : exception);
    }
}

