package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
