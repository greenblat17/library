package com.greenblat.spring.controllers;

import com.greenblat.spring.models.Book;
import com.greenblat.spring.models.Person;
import com.greenblat.spring.services.BookService;
import com.greenblat.spring.services.PersonService;
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

    private final BookService bookService;
    private final PersonService personService;

    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BookService bookService, PersonService personService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.personService = personService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false, defaultValue = "false") Boolean sortByYear,
                        Model model) {

        if (page != null && booksPerPage != null) {
            model.addAttribute("books", bookService.findWithPaginationAndSort(page, booksPerPage, sortByYear));
        } else {
            model.addAttribute("books", bookService.findAll());
        }

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookService.findOne(id));

        Optional<Person> ownerBook = bookService.getOwnerBook(id);

        if (ownerBook.isPresent()) {
            model.addAttribute("owner", ownerBook.get());
        } else {
            model.addAttribute("people", personService.findAll());
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
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findOne(id));

        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        bookService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);

        return "redirect:/books";
    }

    @PatchMapping("/{id}/reader")
    public String addBookToPerson(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        bookService.addBookToPerson(id, person);

        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/unreader")
    public String returnBookFromPerson(@PathVariable("id") int id) {
        bookService.returnBookFromOwner(id);

        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String search() {
        return "books/search";
    }

    @GetMapping("/search/title")
    public String searchBook(@RequestParam("query") String titleQuery, Model model) {
        model.addAttribute("books", bookService.findBookByTitle(titleQuery));

        return "books/search";
    }

}
