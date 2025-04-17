package com.kesik.bladecommerce.service;

public interface UserService
{
    public boolean authenticate(String username, String rawPassword);
}
