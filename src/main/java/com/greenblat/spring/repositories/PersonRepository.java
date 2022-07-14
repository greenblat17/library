package com.greenblat.spring.repositories;

import com.greenblat.spring.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByNameAndYearOfBirth(String name, int yearOfBirth);
}
