package com.kruosant.bookwalker.repositories;


import com.kruosant.bookwalker.domains.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
  @EntityGraph(attributePaths = {"orders.books"})
  List<Client> findAll();
}
