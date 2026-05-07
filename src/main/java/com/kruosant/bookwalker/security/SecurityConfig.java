package com.kruosant.bookwalker.security;

import com.kruosant.bookwalker.repositories.ClientRepository;

import java.util.List;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private static final String BOOKS_PATTERN = "/books/**";
  private static final String AUTHORS_PATTERN = "/authors/**";
  private static final String PUBLISHERS_PATTERN = "/publishers/**";
  private static final String GENRES_PATTERN = "/genres/**";
  private static final String TAGS_PATTERN = "/tags/**";
  private static final String CLIENTS_PATTERN = "/clients/**";
  private static final String ORDERS_PATTERN = "/orders/**";
  private static final String ROLE_ADMIN = "ADMIN";

  @Value("${cors.allowed.origins}")
  private List<String> allowedOrigins;

  @Bean
  public UserDetailsService userDetailsService(ClientRepository clientRepository) {
    return username -> clientRepository
        .findFirstByEmailIgnoreCase(username == null ? null : username.trim())
        .map(ClientPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}