package com.example.biblio.dao.repositories;

import com.example.biblio.dao.entities.Book;
import com.example.biblio.dao.entities.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@Transactional

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByCategory(Category category);
    Page<Book> findByDesignationContains(String keyword, Pageable pageable);


    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);
}
