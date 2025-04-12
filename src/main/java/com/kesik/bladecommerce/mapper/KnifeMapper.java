package com.kesik.bladecommerce.mapper;

import com.kesik.bladecommerce.dto.Knife.KnifeDto;
import com.kesik.bladecommerce.dto.Knife.KnifeDetailsDto;
import com.kesik.bladecommerce.model.Knife.Knife;

import java.util.List;
import java.util.stream.Collectors;

public class KnifeMapper {

    public static KnifeDto toDto(Knife knife) {
        if (knife == null) {
            return null;
        }

        KnifeDetailsDto knifeDetailsDto = new KnifeDetailsDto();
        if (knife.getKnifeDetails() != null) {
            knifeDetailsDto.setKnifeType(knife.getKnifeDetails().getKnifeType());
            knifeDetailsDto.setBladeLength(knife.getKnifeDetails().getBladeLength());
            knifeDetailsDto.setColor(knife.getKnifeDetails().getColor());
            knifeDetailsDto.setBladeMaterial(knife.getKnifeDetails().getBladeMaterial());
            knifeDetailsDto.setHandleMaterial(knife.getKnifeDetails().getHandleMaterial());
        }

        KnifeDto knifeDto = new KnifeDto();
        knifeDto.setId(knife.getId());
        knifeDto.setName(knife.getName());
        knifeDto.setCategoryId(knife.getCategoryId());
        knifeDto.setDescription(knife.getDescription());
        knifeDto.setPrice(knife.getPrice());
        knifeDto.setDiscountPrice(knife.getDiscountPrice());
        knifeDto.setKnifeDetails(knifeDetailsDto);
        knifeDto.setStockQuantity(knife.getStockQuantity());
        knifeDto.setTags(knife.getTags());
        knifeDto.setImageUrl(knife.getImageUrl());

        return knifeDto;
    }

    public static List<KnifeDto> toDtoList(List<Knife> knives) {
        if (knives == null || knives.isEmpty()) {
            return List.of();
        }
        return knives.stream()
                .map(KnifeMapper::toDto)
                .collect(Collectors.toList());
    }
}