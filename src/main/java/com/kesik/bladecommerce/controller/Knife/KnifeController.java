package com.kesik.bladecommerce.controller.Knife;

import com.kesik.bladecommerce.dto.Knife.KnifeDto;
import com.kesik.bladecommerce.mapper.KnifeMapper;
import com.kesik.bladecommerce.repository.Knife.KnifeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knives")
public class KnifeController {

    private final KnifeRepository knifeRepository;

    public KnifeController(KnifeRepository knifeRepository, KnifeMapper knifeMapper) {
        this.knifeRepository = knifeRepository;
    }

    @GetMapping
    public List<KnifeDto> getAllKnives() {
        return KnifeMapper.toDtoList(knifeRepository.getAll());
    }
}
