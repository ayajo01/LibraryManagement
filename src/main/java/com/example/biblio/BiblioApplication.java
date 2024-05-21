package com.example.biblio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BiblioApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BiblioApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
