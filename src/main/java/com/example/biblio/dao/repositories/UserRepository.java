package com.example.biblio.dao.repositories;


import com.example.biblio.dao.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
