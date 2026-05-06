package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
  List<Client> findAllByEmailIgnoreCaseOrderByIdAsc(String email);
  boolean existsByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
  boolean existsByNameIgnoreCase(String name);
  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

  default Optional<Client> findFirstByEmailIgnoreCase(String email) {
    return findAllByEmailIgnoreCaseOrderByIdAsc(email).stream().findFirst();
  }
}
