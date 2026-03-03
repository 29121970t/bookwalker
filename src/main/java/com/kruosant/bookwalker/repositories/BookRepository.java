package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  List<Book> findAllByName(String name);
}
