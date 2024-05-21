package com.example.biblio.service;


import com.example.biblio.dao.entities.Book;
import com.example.biblio.dao.entities.Category;
import com.example.biblio.dao.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService implements BookManager {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public Book getBookById(Integer id) {
        return bookRepository.findById(id).get();
    }
    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getBooksByCategory(Category category) {
        return bookRepository.findByCategory(category);
    }

    @Override
    public Book editBook(Integer id, Book updatedBook) {
        Optional<Book> existingBookOptional = bookRepository.findById(id);
        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setCategory(updatedBook.getCategory());
            existingBook.setPrice(updatedBook.getPrice());

            return bookRepository.save(existingBook);
        } else {
            return null;
        }
    }
    @Override
    public boolean deleteBook(Integer id) {
        try {
            bookRepository.deleteById(id);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
    public Page<Book> getAllBooks(int page) {
        return bookRepository.findAll(PageRequest.of(page, 3)); // Fetch 3 elements per page
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable);
    }
}
