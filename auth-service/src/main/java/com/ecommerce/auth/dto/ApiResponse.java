package com.ecommerce.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Generic API response")
public class ApiResponse {

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Success status")
    private boolean success;

    @Schema(description = "Response data")
    private Object data;

    @Schema(description = "Error details")
    private String error;
}

