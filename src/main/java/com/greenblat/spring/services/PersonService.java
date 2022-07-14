package com.greenblat.spring.services;

import com.greenblat.spring.models.Book;
import com.greenblat.spring.models.Person;
import com.greenblat.spring.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findOne(int id) {
        return personRepository.findById(id).orElse(null);

    }

    public Optional<Person> getPersonByNameAndYearOfBirth(String name, int yearOfBirth) {
        return personRepository.findByNameAndYearOfBirth(name, yearOfBirth);
    }

    @Transactional()
    public void save(Person person) {
        personRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        personRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        personRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = personRepository.findById(id);

        if (person.isPresent()) {

            List<Book> books = person.get().getBooks();

            long currentTime = new Date().getTime();
            for (Book book : books) {
                book.setOverdue(currentTime - book.getTimeToAdd().getTime() > 24 * 60 * 60 * 1000 * 10);
            }

            return person.get().getBooks();
        }
        return new ArrayList<>();
    }
}
