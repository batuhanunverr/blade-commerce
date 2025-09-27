package com.kesik.bladecommerce.repository.knife;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnifeRepository extends MongoRepository<KnifeDto, String> {

    @Query("{ $and: [ " +
            "{ $or: [ { ?0: null }, { 'name': { $regex: ?0, $options: 'i' } } ] }, " +
            "{ $or: [ { ?1: null }, { 'categoryId': ?1 } ] }, " +
            "{ $or: [ { ?2: null }, { 'price': { $gte: ?2 } } ] }, " +
            "{ $or: [ { ?3: null }, { 'price': { $lte: ?3 } } ] }, " +
            "{ $or: [ { ?4: null }, { 'knifeType': ?4 } ] }, " +
            "{ $or: [ { ?5: null }, { 'bladeMaterial': ?5 } ] } " +
            "] }")
    Page<KnifeDto> searchKnives(String searchTerm, Integer categoryId, Double minPrice, Double maxPrice,
                                String knifeType, String bladeMaterial, Pageable pageable);
    @Query("{ 'name': ?0 }")
    KnifeDto getKnifeByName(String name);
}