package com.kesik.bladecommerce.dto.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String password;
}
