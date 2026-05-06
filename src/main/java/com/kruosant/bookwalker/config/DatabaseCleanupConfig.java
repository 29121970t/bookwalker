package com.kruosant.bookwalker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class DatabaseCleanupConfig {
  private final JdbcTemplate jdbcTemplate;

  @Bean
  ApplicationRunner removeLegacyBookSlugColumn() {
    return args -> jdbcTemplate.execute("ALTER TABLE books DROP COLUMN IF EXISTS slug");
  }
}
