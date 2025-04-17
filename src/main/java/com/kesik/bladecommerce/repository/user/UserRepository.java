package com.kesik.bladecommerce.repository.user;

import com.kesik.bladecommerce.dto.user.UserDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDto, String> {
    Optional<UserDto> findByUsername(String username);
}
