package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @Override
  @EntityGraph(value = "Order.full", type = EntityGraph.EntityGraphType.FETCH)
  List<Order> findAll();

  @EntityGraph(value = "Order.full", type = EntityGraph.EntityGraphType.FETCH)
  List<Order> findAllByClientId(Long clientId);
}
