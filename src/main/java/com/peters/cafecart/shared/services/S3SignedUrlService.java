package com.peters.cafecart.shared.services;

import com.peters.cafecart.shared.dtos.Response.DownloadUrlResponse;
import com.peters.cafecart.shared.dtos.Response.UploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3SignedUrlService {

    private final S3Presigner presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Value("${signed-url.upload-expiry-minutes}")
    private long uploadExpiryMinutes;

    @Value("${signed-url.download-expiry-minutes}")
    private long downloadExpiryMinutes;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public UploadUrlResponse generateUploadUrl(
            Long vendorId,
            String fileName,
            String contentType
    ) {

        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String key = buildKey(vendorId, fileName);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(uploadExpiryMinutes))
                        .putObjectRequest(objectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                presigner.presignPutObject(presignRequest);

        return new UploadUrlResponse(
                presignedRequest.url().toString(),
                buildPublicUrl(key)
        );
    }

    public DownloadUrlResponse generateDownloadUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(downloadExpiryMinutes))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                presigner.presignGetObject(presignRequest);

        return new DownloadUrlResponse(
                presignedRequest.url().toString()
        );
    }

    private String buildKey(Long vendorId, String originalName) {
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        return "products/" + vendorId + "/images/" + UUID.randomUUID() + ext;
    }

    private String buildPublicUrl(String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
        // Replace with CloudFront domain if you have one
    }
}
