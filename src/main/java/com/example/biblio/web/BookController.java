package com.example.biblio.web;

import com.example.biblio.dao.entities.Book;
import com.example.biblio.dao.entities.Category;
import com.example.biblio.service.BookManager;
import com.example.biblio.service.FileUploadManager;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BookController {

    private final BookManager bookManager;
    private final FileUploadManager fileUploadManager;

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    public BookController(BookManager bookManager, FileUploadManager fileUploadManager) {
        this.bookManager = bookManager;
        this.fileUploadManager = fileUploadManager;
    }
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/books")
    public String listBooks(Model model,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Book> booksPage = bookManager.getAllBooks(page);
        model.addAttribute("listOfBooks", booksPage.getContent());
        model.addAttribute("pages", booksPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "books";
    }

    @GetMapping("/ubooks")
    public String userlistBooks(Model model,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Book> booksPage = bookManager.getAllBooks(page);
        model.addAttribute("listOfBooks", booksPage.getContent());
        model.addAttribute("pages", booksPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "ubooks";
    }

    @GetMapping("/add")
    public String addBook(Model model) {
        model.addAttribute("book", new Book());
        return "addBook";
    }

    @PostMapping("/saveBook")
    public String addBook(Model model,
                          @ModelAttribute @Valid Book book,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam("image") MultipartFile image,
                          @RequestParam("description") String description,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "addBook";
        }

        logger.info("Received file: {}", file.getOriginalFilename());
        logger.info("Received image: {}", image.getOriginalFilename());

        String fileName = fileUploadManager.uploadFile(file);
        String imageName = fileUploadManager.uploadImage(image);

        logger.info("Generated file name: {}", fileName);
        logger.info("Generated image name: {}", imageName);

        book.setFilePath(fileName);
        book.setImagePath(imageName);
        book.setDescription(description);

        bookManager.addBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/books";
    }

    @GetMapping("/category/{category}")
    public List<Book> getBooksByCategory(@PathVariable Category category) {
        return bookManager.getBooksByCategory(category);
    }

    @PostMapping("/edit")
    public String editBook(@RequestParam("id") Integer id,
                           @ModelAttribute @Valid Book updatedBook,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam("image") MultipartFile image,
                           @RequestParam("description") String description,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "editBook";
        }

        logger.info("Received file: {}", file.getOriginalFilename());
        logger.info("Received image: {}", image.getOriginalFilename());

        if (!file.isEmpty()) {
            String fileName = fileUploadManager.uploadFile(file);
            updatedBook.setFilePath(fileName);
        }

        if (!image.isEmpty()) {
            String imageName = fileUploadManager.uploadImage(image);
            updatedBook.setImagePath(imageName);
        }

        updatedBook.setDescription(description);

        Book editedBook = bookManager.editBook(id, updatedBook);
        if (editedBook != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update book. Please try again.");
        }
        return "redirect:/books";
    }

    @GetMapping("/editBook")
    public String editBook(Model model, @RequestParam(name = "id") Integer id) {
        Book book = bookManager.getBookById(id);
        if (book != null) {
            model.addAttribute("bookToBeUpdated", book);
            return "editBook";
        } else {
            return "error";
        }
    }

    @GetMapping("/deleteBook")
    public String deleteBook(Model model, @RequestParam(name = "id") Integer id) {
        if (bookManager.deleteBook(id)) {
            return "redirect:/books";
        } else {
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchBooks(Model model, Pageable pageable,
                              @RequestParam(name = "keyword") String keyword) {
        Page<Book> listOfBooks = bookManager.searchBooks(keyword, pageable);
        model.addAttribute("listOfBooks", listOfBooks);
        model.addAttribute("keyword", keyword);
        return "books";
    }
    @GetMapping("/downloadFile/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) {
        Book book = bookManager.getBookById(id);

        if (book == null || book.getFilePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource("Book not found or file path is null".getBytes()));
        }

        Resource fileResource = fileUploadManager.loadFileAsResource(book.getFilePath());

        if (fileResource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource("File not found".getBytes()));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }



    @GetMapping("/bookDetails/{id}")
    public String showBookDetails(@PathVariable Integer id, Model model) {
        Book book = bookManager.getBookById(id);
        if (book != null) {
            model.addAttribute("book", book);
            return "bookDetails";
        } else {
            model.addAttribute("errorMessage", "Book not found");
            return "error";
        }
    }

    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> loadImage(@PathVariable String imageName) {
        Resource resource = fileUploadManager.loadImageAsResource(imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                .body(resource);
    }
}
