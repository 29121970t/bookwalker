package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
  @Override
  @EntityGraph(value = "Book.full", type = EntityGraph.EntityGraphType.FETCH)
  List<Book> findAll();

  @Override
  @EntityGraph(value = "Book.full", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Book> findById(Long id);
}
