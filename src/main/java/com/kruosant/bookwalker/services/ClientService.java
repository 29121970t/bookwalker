package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.exceptions.ConflictException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.ClientMapper;
import com.kruosant.bookwalker.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public List<ClientFullDto> getAll() {
    return clientRepository.findAll().stream().map(clientMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public ClientFullDto getById(Long id) {
    return clientMapper.toFullDto(getEntity(id));
  }

  @Transactional
  public ClientFullDto create(ClientCreateDto dto) {
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
        .role(dto.getRole() == null ? UserRole.CUSTOMER : dto.getRole())
        .status(UserStatus.ACTIVE)
        .joinedAt(LocalDate.now())
        .build();
    return clientMapper.toFullDto(clientRepository.save(client));
  }

  @Transactional
  public ClientFullDto patch(Long id, ClientPatchDto dto) {
    Client client = getEntity(id);
    if (dto.getName() != null) {
      String trimmedName = dto.getName().trim();
      if (clientRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
        throw new ConflictException("Name already in use");
      }
      client.setName(trimmedName);
    }
    if (dto.getEmail() != null) {
      String normalizedEmail = normalizeEmail(dto.getEmail());
      if (clientRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
        throw new ConflictException("Email already in use");
      }
      client.setEmail(normalizedEmail);
    }
    if (dto.getCity() != null) client.setCity(dto.getCity());
    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      client.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    if (dto.getRole() != null) client.setRole(dto.getRole());
    if (dto.getStatus() != null) client.setStatus(dto.getStatus());
    return clientMapper.toFullDto(clientRepository.save(client));
  }

  @Transactional
  public ClientFullDto put(Long id, ClientPutDto dto) {
    Client client = getEntity(id);
    String normalizedEmail = normalizeEmail(dto.getEmail());
    if (clientRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
      throw new ConflictException("Email already in use");
    }
    String trimmedName = dto.getName().trim();
    if (clientRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
      throw new ConflictException("Name already in use");
    }
    client.setName(trimmedName);
    client.setEmail(normalizedEmail);
    client.setCity(dto.getCity());
    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      client.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    client.setRole(dto.getRole() == null ? UserRole.CUSTOMER : dto.getRole());
    client.setStatus(dto.getStatus() == null ? UserStatus.ACTIVE : dto.getStatus());
    return clientMapper.toFullDto(clientRepository.save(client));
  }

  @Transactional
  public void delete(Long id) {
    clientRepository.delete(getEntity(id));
  }

  public Client getEntity(Long id) {
    return clientRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
}
