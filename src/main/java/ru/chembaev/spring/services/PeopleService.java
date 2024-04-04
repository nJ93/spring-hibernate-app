package ru.chembaev.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chembaev.spring.models.Book;
import ru.chembaev.spring.models.Person;
import ru.chembaev.spring.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        return person.map(value -> value.getBooks().
                stream().
                peek(book -> {
                    if (book.getTakenDate().isBefore(LocalDateTime.now().minusDays(10))) {
                        book.setExpired(true);
                    }
                }).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public Optional<Person> findByName(String name) {
        return peopleRepository.findByName(name);
    }
}
