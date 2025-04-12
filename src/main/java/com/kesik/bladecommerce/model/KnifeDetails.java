package com.kesik.bladecommerce.model;

import lombok.Data;

@Data
public class KnifeDetails {
    private String knifeType;              // e.g., "Folding", "Fixed Blade", "Karambit"
    private double bladeLength;            // in cm or inches
    private String color;                  // e.g., "Black", "Camo", "Silver"
    private String bladeMaterial;          // e.g., "D2 Steel", "Carbon Steel", "VG-10"
    private String handleMaterial;         // e.g., "G10", "Micarta", "Wood"
}
