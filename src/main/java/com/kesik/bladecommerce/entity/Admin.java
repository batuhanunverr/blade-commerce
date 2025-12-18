package com.kesik.bladecommerce.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "admins")
public class Admin {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password; // BCrypt hashed

    private String email;

    private String role = "ADMIN";

    private boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;
}
