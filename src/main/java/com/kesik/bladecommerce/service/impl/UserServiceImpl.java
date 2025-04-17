package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.user.UserDto;
import com.kesik.bladecommerce.repository.user.UserRepository;
import com.kesik.bladecommerce.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String rawPassword) {

        Optional<UserDto> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserDto user = userOptional.get();
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }
}