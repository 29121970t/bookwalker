package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
  @EntityGraph(value = "Author.books", type = EntityGraph.EntityGraphType.FETCH)
  List<Author> findAll();

  Page<Author> findAll(Pageable pageable);

  @EntityGraph(value = "Author.books", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Author> findById(Long id);
}
