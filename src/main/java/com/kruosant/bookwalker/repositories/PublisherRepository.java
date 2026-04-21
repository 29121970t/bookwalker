package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
  @EntityGraph(value = "Publisher.books", type = EntityGraph.EntityGraphType.FETCH)
  List<Publisher> findAll();

  Page<Publisher> findAll(Pageable pageable);

  @EntityGraph(value = "Publisher.books", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Publisher> findById(Long id);
}
