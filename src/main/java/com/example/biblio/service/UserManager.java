package com.example.biblio.service;

import com.example.biblio.dao.entities.User;
import org.springframework.stereotype.Component;

@Component

public interface UserManager {
    void registerNewUser(User user);
    User authenticateUser(String username, String password);
}

