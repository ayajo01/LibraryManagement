package com.example.biblio.service;


import com.example.biblio.dao.entities.Book;
import com.example.biblio.dao.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BookManager {
	Page<Book> getAllBooks(int page);
	Book getBookById(Integer id);
	Book addBook(Book book);
	boolean deleteBook(Integer id);

	List<Book> getBooksByCategory(Category category);
	Page<Book> searchBooks(String keyword, Pageable pageable);
	Book editBook(Integer id, Book updatedBook);
}