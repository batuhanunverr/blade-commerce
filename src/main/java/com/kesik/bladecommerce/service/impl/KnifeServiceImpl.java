package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.model.Knife.Knife;
import com.kesik.bladecommerce.service.KnifeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KnifeServiceImpl implements KnifeService {

    private List<Knife> knifeRepository = new ArrayList<>();

    @Override
    public List<Knife> getAllKnives() {
        return knifeRepository;
    }

    @Override
    public List<Knife> getKnivesByName(String name, int page, int size) {
        return knifeRepository.stream()
                .filter(knife -> knife.getName().toLowerCase().contains(name.toLowerCase()))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public List<Knife> getKnivesByType(String type, int page, int size) {
        return knifeRepository.stream()
                .filter(knife -> knife.getKnifeDetails().getKnifeType().equalsIgnoreCase(type))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Knife createKnife(Knife knife) {
        knifeRepository.add(knife);
        return knife;
    }

    @Override
    public Knife updateKnife(String id, Knife knife) {
        for (int i = 0; i < knifeRepository.size(); i++) {
            if (knifeRepository.get(i).getId().equals(id)) {
                knifeRepository.set(i, knife);
                return knife;
            }
        }
        return null;
    }

    @Override
    public void deleteKnife(String id) {
        knifeRepository.removeIf(knife -> knife.getId().equals(id));
    }
}