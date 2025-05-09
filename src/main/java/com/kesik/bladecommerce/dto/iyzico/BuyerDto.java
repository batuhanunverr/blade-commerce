package com.kesik.bladecommerce.dto.iyzico;

import lombok.Data;

@Data
public class BuyerDto {
    private String id;
    private String name;
    private String surname;
    private String identityNumber;
    private String email;
    private String gsmNumber;
    private String registrationDate;
    private String lastLoginDate;
    private String registrationAddress;
    private String city;
    private String country;
    private String zipCode;
    private String ip;
}
