package com.kruosant.bookwalker.repositories;


import com.kruosant.bookwalker.domains.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
  @EntityGraph(attributePaths = {"orders.books"})
  List<Client> findAll();

  Page<Client> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"orders.books"})
  Optional<Client> findById(Long id);
}
