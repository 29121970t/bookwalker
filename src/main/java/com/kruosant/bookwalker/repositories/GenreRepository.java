package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
