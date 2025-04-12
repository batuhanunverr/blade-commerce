package com.kesik.bladecommerce.dto.knife;

import lombok.Data;

@Data
public class KnifeDetailsDto {
    private String knifeType;
    private double bladeLength;
    private String color;
    private String bladeMaterial;
    private String handleMaterial;
}
