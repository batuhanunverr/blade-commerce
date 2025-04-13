package com.kesik.bladecommerce.repository.category;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryDto, String> {
    void deleteByCategoryId(Integer categoryId);

    public interface getAllCategories {
    }
    Optional<CategoryDto> findByCategoryId(Integer categoryId);

}
