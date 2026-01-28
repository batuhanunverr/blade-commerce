package com.kesik.bladecommerce.repository;

import com.kesik.bladecommerce.entity.ContentKey;
import com.kesik.bladecommerce.entity.CorporateContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorporateContentRepository extends MongoRepository<CorporateContent, String> {

    Optional<CorporateContent> findByKey(ContentKey key);

    Optional<CorporateContent> findByKeyAndActiveTrue(ContentKey key);

    List<CorporateContent> findAllByActiveTrue();
}