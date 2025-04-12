package com.kesik.bladecommerce.repository.knife;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnifeRepository extends MongoRepository<KnifeDto, String> {
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    List<KnifeDto> searchKnives(String searchTerm);

    @Query("{ 'name': ?0 }")
    KnifeDto getKnifeByName(String name);
}