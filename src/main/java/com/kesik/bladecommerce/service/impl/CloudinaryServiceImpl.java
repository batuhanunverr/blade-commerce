package com.kesik.bladecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kesik.bladecommerce.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud.name}") String cloudName,
            @Value("${cloudinary.api.key}") String apiKey,
            @Value("${cloudinary.api.secret}") String apiSecret
    ) {
        log.info("Initializing Cloudinary service with cloud name: {}", cloudName);

        if (cloudName == null || cloudName.trim().isEmpty()) {
            throw new IllegalStateException("Cloudinary cloud name is not configured. Please set CLOUDINARY_CLOUD_NAME environment variable.");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Cloudinary API key is not configured. Please set CLOUDINARY_API_KEY environment variable.");
        }
        if (apiSecret == null || apiSecret.trim().isEmpty()) {
            throw new IllegalStateException("Cloudinary API secret is not configured. Please set CLOUDINARY_API_SECRET environment variable.");
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);

        log.info("Cloudinary service initialized successfully");
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            log.info("Starting file upload to Cloudinary: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Validate file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB in bytes
            if (file.getSize() > maxSize) {
                throw new IllegalArgumentException("File size exceeds maximum allowed (5MB). Current size: " + (file.getSize() / 1024 / 1024) + "MB");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Only images are allowed.");
            }

            // Configure Cloudinary upload with optimization
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("quality", "auto:good");  // Automatic quality optimization
            uploadOptions.put("fetch_format", "auto");  // Auto format (WebP for supported browsers)
            uploadOptions.put("width", 1200);           // Max width 1200px
            uploadOptions.put("height", 1200);          // Max height 1200px
            uploadOptions.put("crop", "limit");         // Don't upscale, only downscale if needed
            uploadOptions.put("flags", "progressive");  // Progressive JPEG loading

            Map<?, ?> rawResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String secureUrl = rawResult.get("secure_url").toString();

            log.info("Successfully uploaded and optimized file to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (Exception e) {
            log.error("Failed to upload file to Cloudinary: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    public String uploadBase64(String base64Image) throws IOException {
        try {
            System.out.println("uploadBase64 çağrıldı");

            log.info("Starting base64 image upload to Cloudinary");

            // Validate base64 string
            if (base64Image == null || base64Image.trim().isEmpty()) {
                System.out.println("Base64 image string null veya boş!");
                throw new IllegalArgumentException("Base64 image string cannot be null or empty");
            }
            System.out.println("Base64 string uzunluğu: " + base64Image.length());

            // Check if the base64 string already has the data URI prefix
            String dataUri;
            if (base64Image.startsWith("data:")) {
                dataUri = base64Image;
                System.out.println("Base64 zaten data URI ile başlıyor");
            } else {
                dataUri = "data:image/jpeg;base64," + base64Image;
                System.out.println("Base64'a data URI prefix eklendi");
            }

            log.debug("Uploading image with data URI prefix: {}", dataUri.substring(0, Math.min(50, dataUri.length())));
            System.out.println("Cloudinary upload başlıyor");

            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("quality", "auto:good");
            uploadOptions.put("fetch_format", "auto");
            uploadOptions.put("width", 1200);
            uploadOptions.put("height", 1200);
            uploadOptions.put("crop", "limit");
            uploadOptions.put("flags", "progressive");
            uploadOptions.put("resource_type", "image");

            Map<?, ?> rawResult = cloudinary.uploader().upload(dataUri, uploadOptions);
            String secureUrl = rawResult.get("secure_url").toString();


            log.info("Successfully uploaded and optimized image to Cloudinary: {}", secureUrl);
            System.out.println("Cloudinary upload başarılı, url: " + secureUrl);
            return secureUrl;

        } catch (Exception e) {
            System.out.println("Cloudinary base64 upload sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            log.error("Failed to upload base64 image to Cloudinary", e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }
}
