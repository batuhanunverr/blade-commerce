package com.kesik.bladecommerce.controller.knife;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knives")
public class KnifeController {

    private final KnifeService knifeService;

    public KnifeController(KnifeService knifeService) {
        this.knifeService = knifeService;
    }

    @GetMapping(path = "/getAllKnives")
    public List<KnifeDto> getAllKnives() {
        return knifeService.getAllKnives();
    }
    @GetMapping(path = "/search")
    public List<KnifeDto> searchKnives(@RequestParam String searchTerm) {
        return knifeService.searchKnives(searchTerm);
    }
    @GetMapping(path = "/getKnifeById")
    public KnifeDto getKnifeById(@RequestParam String id) {
        return knifeService.getKnifeById(id);
    }
    @GetMapping(path = "/getKnifeByName")
    public KnifeDto getKnifeByName(@RequestParam String name) {
        return knifeService.getKnifeByName(name);
    }
    @PostMapping(path = "/addKnife")
    public KnifeDto addKnife(@RequestBody KnifeDto knifeDto) {
        return knifeService.addKnife(knifeDto);
    }
    @PostMapping(path = "/updateKnife")
    public KnifeDto updateKnife(@RequestBody KnifeDto knifeDto) {
        return knifeService.updateKnife(knifeDto);
    }
    @DeleteMapping(path = "/deleteKnife")
    public void deleteKnife(@RequestParam String id) {
        knifeService.deleteKnife(id);
    }
}
