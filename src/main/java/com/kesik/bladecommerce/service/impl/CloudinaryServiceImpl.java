package com.kesik.bladecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kesik.bladecommerce.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    private final Cloudinary cloudinary;
    private final String cloudName = "dqgnkfvrz";
    private final String apiKey = "314381283678893";
    private final String apiSecret = "YMAACTcENDc_w1tpKzxuM9hceaA";

    public CloudinaryServiceImpl() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            logger.info("Starting file upload to Cloudinary: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String secureUrl = uploadResult.get("secure_url").toString();

            logger.info("Successfully uploaded file to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (Exception e) {
            logger.error("Failed to upload file to Cloudinary: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    public String uploadBase64(String base64Image) throws IOException {
        try {
            logger.info("Starting base64 image upload to Cloudinary");

            // Validate base64 string
            if (base64Image == null || base64Image.trim().isEmpty()) {
                throw new IllegalArgumentException("Base64 image string cannot be null or empty");
            }

            // Check if the base64 string already has the data URI prefix
            String dataUri;
            if (base64Image.startsWith("data:")) {
                dataUri = base64Image;
            } else {
                // Add the data URI prefix for JPEG format
                dataUri = "data:image/jpeg;base64," + base64Image;
            }

            logger.debug("Uploading image with data URI prefix: {}", dataUri.substring(0, Math.min(50, dataUri.length())));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(dataUri, ObjectUtils.emptyMap());
            String secureUrl = uploadResult.get("secure_url").toString();

            logger.info("Successfully uploaded image to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (Exception e) {
            logger.error("Failed to upload base64 image to Cloudinary", e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }
}
