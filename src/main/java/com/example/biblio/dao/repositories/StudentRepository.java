package com.example.biblio.dao.repositories;

import com.example.biblio.dao.entities.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
@Transactional

public interface StudentRepository extends JpaRepository<Student, Integer> {


}