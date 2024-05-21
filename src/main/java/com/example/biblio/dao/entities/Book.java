package com.example.biblio.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Book")
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private int numberOfPages;
    private boolean available;
    private double price;
    private String designation;
    private String filePath;
    private String imagePath;
    private String description;



    @Enumerated(EnumType.STRING)
    private Category category;

    public Book(String title, String author, Category category, double price) {
    }

    public Book(Integer id, String title, String author, Category category, double price) {
    }
}
