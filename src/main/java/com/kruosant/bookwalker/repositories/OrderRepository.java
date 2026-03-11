package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @EntityGraph(attributePaths = {"books", "client"})
  List<Order> findAll();

  List<Order> findAllByBooksContains(Book book);
}






