package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  @EntityGraph(value = "Book.authors.publisher", type = EntityGraph.EntityGraphType.FETCH)
  List<Book> findAllByName(String name);

  @EntityGraph(value = "Book.authors.publisher", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Book> findById(Long id);

  @EntityGraph(value = "Book.authors.publisher", type = EntityGraph.EntityGraphType.FETCH)
  List<Book> findAll();
}
