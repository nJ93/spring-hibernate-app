package ru.chembaev.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chembaev.spring.models.Book;
import ru.chembaev.spring.models.Person;
import ru.chembaev.spring.repositories.BooksRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    private final PeopleService peopleService;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleService peopleService) {
        this.booksRepository = booksRepository;
        this.peopleService = peopleService;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public Book findOne(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(id).get();

        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner());

        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void updateAffilation(int bookId, Person person) {
        booksRepository.findById(bookId).ifPresent(book -> {
            book.setOwner(person);
            book.setTakenDate(LocalDateTime.now());
        });
    }

    @Transactional
    public void deleteAffilation(int bookId) {
        booksRepository.findById(bookId).ifPresent(book -> {
            book.setOwner(null);
            book.setTakenDate(null);
        });
    }

    public Person getOwnerInfo(int bookId) {
        Person person = null;
        Optional<Book> foundBook = booksRepository.findById(bookId);
        if (foundBook.isPresent()) {
            person = foundBook.get().getOwner();
        }
        return person;
    }

    public List<Person> getAllPeoples() {
        return peopleService.findAll();
    }

    public List<Book> getSortedBooksWithPagination(Integer page, Integer booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("publishYear"))).getContent();
    }

    public List<Book> getBooksWithPagination(Integer page, Integer booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> findAllByOrderByPublishYear() {
        return booksRepository.findAllByOrderByPublishYearDesc();
    }

    public Optional<Book> findByNameStartingWith(String name) {
        return booksRepository.findFirstByNameStartingWith(name);
    }
}
