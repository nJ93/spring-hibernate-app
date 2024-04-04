package ru.chembaev.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chembaev.spring.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    List<Book> findAllByOrderByPublishYearDesc();

    Optional<Book> findFirstByNameStartingWith(String name);
}
