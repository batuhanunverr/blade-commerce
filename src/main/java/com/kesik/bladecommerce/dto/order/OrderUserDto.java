package com.kesik.bladecommerce.dto.order;

import lombok.Data;

@Data
public class OrderUserDto {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
}
