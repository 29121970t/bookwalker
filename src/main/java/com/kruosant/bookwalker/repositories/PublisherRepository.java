package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
