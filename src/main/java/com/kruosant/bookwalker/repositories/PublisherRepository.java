package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Publisher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
  @EntityGraph(value = "Publisher.books", type = EntityGraph.EntityGraphType.FETCH)
  List<Publisher> findAll();
}
