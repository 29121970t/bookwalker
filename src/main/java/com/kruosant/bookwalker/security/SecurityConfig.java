package com.kruosant.bookwalker.security;

import com.kruosant.bookwalker.repositories.ClientRepository;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

import java.util.List;

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

  @Bean
  public UserDetailsService userDetailsService(ClientRepository clientRepository) {
    return username -> clientRepository.findFirstByEmailIgnoreCase(username == null ? null : username.trim())
        .map(ClientPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
    try {
      return configuration.getAuthenticationManager();
    } catch (Exception ex) {
      throw new BeanCreationException("Failed to create authentication manager", ex);
    }
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    try {
      http
          .csrf(csrf -> csrf.disable())
          .cors(cors -> {})
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authenticationProvider(authenticationProvider)
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/auth/**", "/uploads/**", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
              .requestMatchers(HttpMethod.GET, BOOKS_PATTERN, AUTHORS_PATTERN, PUBLISHERS_PATTERN, GENRES_PATTERN, TAGS_PATTERN).permitAll()
              .requestMatchers(HttpMethod.GET, "/orders/me").authenticated()
              .requestMatchers(HttpMethod.GET, ORDERS_PATTERN).hasRole("ADMIN")
              .requestMatchers(HttpMethod.GET, CLIENTS_PATTERN).hasRole("ADMIN")
              .requestMatchers(HttpMethod.POST, "/orders").authenticated()
              .requestMatchers(HttpMethod.POST, BOOKS_PATTERN, AUTHORS_PATTERN, PUBLISHERS_PATTERN, GENRES_PATTERN, TAGS_PATTERN, CLIENTS_PATTERN).hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, BOOKS_PATTERN, AUTHORS_PATTERN, PUBLISHERS_PATTERN, GENRES_PATTERN, TAGS_PATTERN, CLIENTS_PATTERN, ORDERS_PATTERN).hasRole("ADMIN")
              .requestMatchers(HttpMethod.PATCH, BOOKS_PATTERN, AUTHORS_PATTERN, PUBLISHERS_PATTERN, CLIENTS_PATTERN, ORDERS_PATTERN).hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, BOOKS_PATTERN, AUTHORS_PATTERN, PUBLISHERS_PATTERN, GENRES_PATTERN, TAGS_PATTERN, CLIENTS_PATTERN, ORDERS_PATTERN).hasRole("ADMIN")
              .anyRequest().authenticated()
          );
      return http.build();
    } catch (Exception ex) {
      throw new BeanCreationException("Failed to create security filter chain", ex);
    }
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origin}") String allowedOrigin) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(allowedOrigin));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
