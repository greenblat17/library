package com.greenblat.spring.dao;

import com.greenblat.spring.models.Book;
import com.greenblat.spring.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> index() {
        return jdbcTemplate.query("SELECT * FROM book", new BeanPropertyRowMapper<>(Book.class));
    }

    public Book show(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Book.class))
                .stream().findAny().orElse(null);
    }

    public Optional<Book> getTitleAndAuthorOfBook(String title, String author) {
        return jdbcTemplate.query("SELECT * FROM book WHERE title=? AND author=?", new Object[] {title, author},
                new BeanPropertyRowMapper<>(Book.class)).stream().findAny();
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO book(title, author, year) VALUES(?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getYear());
    }

    public void update(int id, Book updatedBook) {
        jdbcTemplate.update("UPDATE book SET title=?, author=?, year=? WHERE id=?",
                updatedBook.getTitle(), updatedBook.getAuthor(), updatedBook.getYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM book WHERE id=?", id);
    }

    public Optional<Person> getOwnerBook(int id) {
        return jdbcTemplate.query("SELECT person.* FROM person JOIN book ON person.id=book.person_id WHERE book.id=?",
                new Object[] {id}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny();
    }

    public void addBookToPerson(int bookId, int personId) {
        jdbcTemplate.update("UPDATE book SET person_id=? WHERE id=?", personId, bookId);
    }

    public void returnBookFromOwner(int bookId) {
        jdbcTemplate.update("UPDATE book SET person_id=null WHERE id=?", bookId);
    }
}
