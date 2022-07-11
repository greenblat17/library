package com.greenblat.spring.util;

import com.greenblat.spring.dao.PersonDAO;
import com.greenblat.spring.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {
    private final PersonDAO personDAO;

    @Autowired
    public PersonValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person validatePerson = (Person) target;

        Optional<Person> copyPerson = personDAO.getPersonByNameAndYearOfBirth(validatePerson.getName(), validatePerson.getYearOfBirth());
        if (copyPerson.isPresent()) {
            errors.rejectValue("yearOfBirth", "", "Человек с таким ФИО и годом рождения уже существует");
        }
    }
}
