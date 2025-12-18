package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Boolean login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticate(username, password);
    }
}
