package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import com.kruosant.bookwalker.dtos.auth.AuthResponseDto;
import com.kruosant.bookwalker.dtos.auth.LoginRequestDto;
import com.kruosant.bookwalker.dtos.auth.MeDto;
import com.kruosant.bookwalker.dtos.auth.RegisterRequestDto;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.exceptions.ConflictException;
import com.kruosant.bookwalker.security.AuthenticatedClientService;
import com.kruosant.bookwalker.security.ClientPrincipal;
import com.kruosant.bookwalker.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final ClientRepository clientRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticatedClientService authenticatedClientService;

  @PostMapping("/login")
  public AuthResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
    String normalizedEmail = normalizeEmail(dto.getEmail());
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, dto.getPassword()));
    Client client = clientRepository.findFirstByEmailIgnoreCase(normalizedEmail).orElseThrow();
    String token = jwtService.generateToken(new ClientPrincipal(client));
    return AuthResponseDto.builder()
        .token(token)
        .name(client.getName())
        .email(client.getEmail())
        .role(client.getRole().name())
        .build();
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto dto) {
    String normalizedEmail = normalizeEmail(dto.getEmail());
    if (clientRepository.existsByEmailIgnoreCase(normalizedEmail)) {
      throw new ConflictException("Email already in use");
    }
    if (clientRepository.existsByNameIgnoreCase(dto.getName().trim())) {
      throw new ConflictException("Name already in use");
    }
    Client client = Client.builder()
        .name(dto.getName().trim())
        .email(normalizedEmail)
        .password(passwordEncoder.encode(dto.getPassword()))
        .city(dto.getCity())
        .role(UserRole.CUSTOMER)
        .status(UserStatus.ACTIVE)
        .joinedAt(LocalDate.now())
        .build();
    clientRepository.save(client);
    String token = jwtService.generateToken(new ClientPrincipal(client));
    return AuthResponseDto.builder()
        .token(token)
        .name(client.getName())
        .email(client.getEmail())
        .role(client.getRole().name())
        .build();
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }

  @GetMapping("/me")
  public MeDto me() {
    Client client = authenticatedClientService.getCurrentClient();
    return MeDto.builder()
        .id(client.getId())
        .name(client.getName())
        .email(client.getEmail())
        .city(client.getCity())
        .role(client.getRole().name())
        .build();
  }
}
