package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
