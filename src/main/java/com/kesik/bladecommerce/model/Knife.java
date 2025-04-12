package com.kesik.bladecommerce.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Knife {
    private String id;
    private String name;                   // e.g., "Tactical Folding Knife"
    private String description;
    private double price;
    private KnifeDetails knifeDetails;
    private int stockQuantity;
    private List<String> tags;             // e.g., "outdoor", "EDC", "tactical"
    private String imageUrl;
}
