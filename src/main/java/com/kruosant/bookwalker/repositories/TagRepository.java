package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
