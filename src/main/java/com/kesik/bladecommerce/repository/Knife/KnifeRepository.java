package com.kesik.bladecommerce.repository.Knife;

import com.kesik.bladecommerce.dto.Knife.KnifeDto;
import com.kesik.bladecommerce.model.Knife.Knife;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnifeRepository extends MongoRepository<Knife, String> {
    List<Knife> findByBrand(String brand);
    List<Knife> getAll();
}