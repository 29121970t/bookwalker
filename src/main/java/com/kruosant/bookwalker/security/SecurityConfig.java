package com.kruosant.bookwalker.security;

import com.kruosant.bookwalker.repositories.ClientRepository;
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
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) throws Exception {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/uploads/**", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/books/**", "/authors/**", "/publishers/**", "/genres/**", "/tags/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/orders/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/orders/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/clients/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/orders").authenticated()
            .requestMatchers(HttpMethod.POST, "/books/**", "/authors/**", "/publishers/**", "/genres/**", "/tags/**", "/clients/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/books/**", "/authors/**", "/publishers/**", "/genres/**", "/tags/**", "/clients/**", "/orders/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/books/**", "/authors/**", "/publishers/**", "/clients/**", "/orders/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/books/**", "/authors/**", "/publishers/**", "/genres/**", "/tags/**", "/clients/**", "/orders/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
    return http.build();
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
