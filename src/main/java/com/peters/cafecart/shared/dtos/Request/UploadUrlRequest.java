package com.peters.cafecart.shared.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadUrlRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;

    @NotNull
    private Long entityId; // productId, userId, etc
}