package com.kesik.bladecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kesik.bladecommerce.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

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
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
    public String uploadBase64(String base64Image) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}
