package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @EntityGraph(attributePaths = {"books", "client"})
  List<Order> findAll();

  Page<Order> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"books", "client"})
  Optional<Order> findById(Long id);

  List<Order> findByBooksContaining(Book book);

  List<Order> findAllByBooksContains(Book book);

  @Query("""
      SELECT DISTINCT o FROM Order o
      JOIN o.books b
      JOIN b.authors a
      WHERE a.surname = :surname""")
  Page<Order> findByAuthorSurname(@Param("surname") String surname, Pageable p);

  @Query(value = """
      SELECT DISTINCT o.* FROM orders o
      JOIN order_book ob ON o.id = ob.order_id
      JOIN clients c ON c.id = o.client_id
      JOIN books b ON ob.book_id = b.id
      JOIN book_author ba ON b.id = ba.book_id
      JOIN authors a ON ba.author_id = a.id
      WHERE a.surname = :surname
      """,
      nativeQuery = true)
  Page<Order> findByAuthorSurnameNative(@Param("surname") String surname, Pageable p);
}



