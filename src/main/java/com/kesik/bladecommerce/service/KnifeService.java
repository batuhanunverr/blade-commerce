package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.model.Knife.Knife;

import java.util.List;

public interface KnifeService {
    List<Knife> getAllKnives();
    List<Knife> getKnivesByName(String name, int page, int size);
    List<Knife> getKnivesByType(String type, int page, int size);
    Knife createKnife(Knife knife);
    Knife updateKnife(String id, Knife knife);
    void deleteKnife(String id);
}
