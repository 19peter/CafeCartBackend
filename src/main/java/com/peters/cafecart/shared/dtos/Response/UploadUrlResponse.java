package com.peters.cafecart.shared.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadUrlResponse {
    private String uploadUrl;
    private String fileUrl;
}
