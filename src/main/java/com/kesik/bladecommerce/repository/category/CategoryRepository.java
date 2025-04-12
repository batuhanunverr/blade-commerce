package com.kesik.bladecommerce.repository.category;

import com.kesik.bladecommerce.dto.category.CategoryDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<CategoryDto, String> {
    public interface getAllCategories {
    }
}
