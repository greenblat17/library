package com.greenblat.spring.util;

import com.greenblat.spring.models.Book;
import com.greenblat.spring.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class BookValidator implements Validator {

    private final BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book validateBook = (Book) target;

        // First letter of book's title should be upper case
        String title = validateBook.getTitle();
        if (title.length() != 0 && !Character.isUpperCase(title.codePointAt(0))) {
            errors.rejectValue("title", "", "Название прозведения должно быть с большой буквы");
        }

        // First letter of author name show be upper case
        String authorName = validateBook.getAuthor();
        if (authorName.length() != 0 && !Character.isUpperCase(authorName.codePointAt(0))) {
            errors.rejectValue("author", "", "Имя автора должно быть с большой буквы");
        }

        // Title of then book and author name should be unique
        Optional<Book> copyTitleBook = bookService.getTitleAndAuthorOfBook(validateBook.getTitle(), authorName);
        if (copyTitleBook.isPresent()) {
            errors.rejectValue("title", "", "Название книги и автора должно быть уникальным");
        }

    }
}
