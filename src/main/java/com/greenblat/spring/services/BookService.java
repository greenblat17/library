package com.greenblat.spring.services;

import com.greenblat.spring.models.Book;
import com.greenblat.spring.models.Person;
import com.greenblat.spring.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findWithPaginationAndSort(int page, int booksPerPage, Boolean sort) {
        if (!sort)
            return bookRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        else
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();

    }

    public Book findOne(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Optional<Book> getTitleAndAuthorOfBook(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public Optional<Person> getOwnerBook(int id) {
        return bookRepository.findById(id).map(Book::getOwner);
    }

    @Transactional
    public void addBookToPerson(int bookId, Person newOwner) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            book.get().setTimeToAdd(new Date());
            book.get().setOwner(newOwner);
        }
    }

    @Transactional
    public void returnBookFromOwner(int bookId) {
        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isPresent()) {
            book.get().setOwner(null);
            book.get().setTimeToAdd(null);
            book.get().setOverdue(false);
        }
    }

    public List<Book> findBookByTitle(String title) {
       return bookRepository.findByTitleStartingWith(title);
    }
}
