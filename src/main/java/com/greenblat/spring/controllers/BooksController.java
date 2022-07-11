package com.greenblat.spring.controllers;

import com.greenblat.spring.dao.BookDAO;
import com.greenblat.spring.dao.PersonDAO;
import com.greenblat.spring.models.Book;
import com.greenblat.spring.models.Person;
import com.greenblat.spring.util.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BookDAO bookDAO, PersonDAO personDAO, BookValidator bookValidator) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("books", bookDAO.index());

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookDAO.show(id));

        Optional<Person> ownerBook = bookDAO.getOwnerBook(id);

        if (ownerBook.isPresent()) {
            model.addAttribute("owner", ownerBook.get());
        } else {
            model.addAttribute("people", personDAO.index());
        }

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        bookDAO.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.show(id));

        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        bookDAO.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookDAO.delete(id);

        return "redirect:/books";
    }

    @PatchMapping("/{id}/reader")
    public String addBookToPerson(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        bookDAO.addBookToPerson(id, person.getId());

        return "redirect:/books" + id;
    }

    @PatchMapping("/{id}/unreader")
    public String returnBookFromPerson(@PathVariable("id") int id) {
        bookDAO.returnBookFromOwner(id);

        return "redirect:/books" + id;
    }
}
