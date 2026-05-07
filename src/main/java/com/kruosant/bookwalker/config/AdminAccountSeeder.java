package com.kruosant.bookwalker.config;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import com.kruosant.bookwalker.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AdminAccountSeeder implements ApplicationRunner {
  private final ClientRepository clientRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.enabled:true}")
  private boolean enabled;

  @Value("${app.admin.email:admin@bookwalker.local}")
  private String email;

  @Value("${app.admin.password:admin}")
  private String password;

  @Value("${app.admin.name:Admin}")
  private String name;

  @Value("${app.admin.city:}")
  private String city;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!enabled) {
      return;
    }

    String normalizedEmail = requireConfiguredValue(email, "app.admin.email").toLowerCase();
    Client client = clientRepository.findFirstByEmailIgnoreCase(normalizedEmail)
        .orElseGet(() -> createAdmin(normalizedEmail));

    client.setRole(UserRole.ADMIN);
    client.setStatus(UserStatus.ACTIVE);
    clientRepository.save(client);
  }

  private Client createAdmin(String normalizedEmail) {
    String configuredPassword = requireConfiguredValue(password, "app.admin.password");
    String configuredName = requireConfiguredValue(name, "app.admin.name");

    return Client.builder()
        .name(configuredName)
        .email(normalizedEmail)
        .password(passwordEncoder.encode(configuredPassword))
        .city(city == null || city.isBlank() ? null : city.trim())
        .role(UserRole.ADMIN)
        .status(UserStatus.ACTIVE)
        .joinedAt(LocalDate.now())
        .build();
  }

  private String requireConfiguredValue(String value, String propertyName) {
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(propertyName + " must not be blank");
    }
    return value.trim();
  }
}
