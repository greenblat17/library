package com.greenblat.spring.util;

import com.greenblat.spring.models.Person;
import com.greenblat.spring.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private final PersonService personService;

    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person validatePerson = (Person) target;

        Optional<Person> copyPerson = personService.getPersonByNameAndYearOfBirth(validatePerson.getName(), validatePerson.getYearOfBirth());
        if (copyPerson.isPresent()) {
            errors.rejectValue("yearOfBirth", "", "Человек с таким ФИО и годом рождения уже существует");
        }
    }
}
