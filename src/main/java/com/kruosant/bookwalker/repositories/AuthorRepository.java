package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
  @EntityGraph(value = "Author.books", type = EntityGraph.EntityGraphType.FETCH)
  List<Author> findAll();

  @EntityGraph(value = "Author.books", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Author> findById(Long id);
}
