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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
  @Mock
  private ClientRepository clientRepository;
  @Mock
  private ClientMapper clientMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private ClientService clientService;

  @Test
  void getAllMapsClients() {
    Client client = client("Reader", "reader@example.com");
    ClientFullDto dto = ClientFullDto.builder().name("Reader").build();

    when(clientRepository.findAll()).thenReturn(List.of(client));
    when(clientMapper.toFullDto(client)).thenReturn(dto);

    assertEquals(List.of(dto), clientService.getAll());
  }

  @Test
  void createNormalizesEmailAndEncodesPassword() {
    ClientCreateDto dto = new ClientCreateDto();
    dto.setName(" Reader ");
    dto.setEmail("READER@EXAMPLE.COM ");
    dto.setPassword("secret");
    ClientFullDto fullDto = ClientFullDto.builder().email("reader@example.com").build();

    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(clientMapper.toFullDto(any(Client.class))).thenReturn(fullDto);

    assertEquals(fullDto, clientService.create(dto));
    verify(clientRepository).existsByEmailIgnoreCase("reader@example.com");
    verify(clientRepository).existsByNameIgnoreCase("Reader");
  }

  @Test
  void createRejectsTakenEmail() {
    ClientCreateDto dto = new ClientCreateDto();
    dto.setName("Reader");
    dto.setEmail("reader@example.com");

    when(clientRepository.existsByEmailIgnoreCase("reader@example.com")).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.create(dto));
  }

  @Test
  void createRejectsTakenName() {
    ClientCreateDto dto = new ClientCreateDto();
    dto.setName("Reader");
    dto.setEmail("reader@example.com");

    when(clientRepository.existsByNameIgnoreCase("Reader")).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.create(dto));
  }

  @Test
  void createUsesProvidedRole() {
    ClientCreateDto dto = new ClientCreateDto();
    dto.setName("Reader");
    dto.setEmail("reader@example.com");
    dto.setPassword("secret");
    dto.setRole(UserRole.ADMIN);

    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(clientMapper.toFullDto(any(Client.class))).thenReturn(ClientFullDto.builder().build());

    clientService.create(dto);

    verify(clientRepository).save(org.mockito.ArgumentMatchers.argThat(client -> client.getRole() == UserRole.ADMIN));
  }

  @Test
  void getByIdMapsClient() {
    Client client = client("Reader", "reader@example.com");
    ClientFullDto dto = ClientFullDto.builder().name("Reader").build();

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientMapper.toFullDto(client)).thenReturn(dto);

    assertEquals(dto, clientService.getById(1L));
  }

  @Test
  void patchRejectsTakenName() {
    ClientPatchDto dto = new ClientPatchDto();
    dto.setName("Taken");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client("Old", "old@example.com")));
    when(clientRepository.existsByNameIgnoreCaseAndIdNot("Taken", 1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.patch(1L, dto));
  }

  @Test
  void patchRejectsTakenEmail() {
    ClientPatchDto dto = new ClientPatchDto();
    dto.setEmail("taken@example.com");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client("Old", "old@example.com")));
    when(clientRepository.existsByEmailIgnoreCaseAndIdNot("taken@example.com", 1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.patch(1L, dto));
  }

  @Test
  void patchChangesEveryProvidedField() {
    Client client = client("Old", "old@example.com");
    ClientPatchDto dto = new ClientPatchDto();
    dto.setName("New");
    dto.setEmail("NEW@EXAMPLE.COM");
    dto.setCity("Minsk");
    dto.setPassword("secret");
    dto.setRole(UserRole.ADMIN);
    dto.setStatus(UserStatus.BLOCKED);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(passwordEncoder.encode("secret")).thenReturn("encoded-2");
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().build());

    clientService.patch(1L, dto);

    assertEquals("New", client.getName());
    assertEquals("new@example.com", client.getEmail());
    assertEquals("Minsk", client.getCity());
    assertEquals("encoded-2", client.getPassword());
    assertEquals(UserRole.ADMIN, client.getRole());
    assertEquals(UserStatus.BLOCKED, client.getStatus());
  }

  @Test
  void patchIgnoresBlankPassword() {
    Client client = client("Old", "old@example.com");
    ClientPatchDto dto = new ClientPatchDto();
    dto.setPassword(" ");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().build());

    clientService.patch(1L, dto);

    assertEquals("encoded", client.getPassword());
  }

  @Test
  void patchLeavesNullFieldsUntouched() {
    Client client = client("Old", "old@example.com");
    ClientPatchDto dto = new ClientPatchDto();

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().build());

    clientService.patch(1L, dto);

    assertEquals("Old", client.getName());
    assertEquals("old@example.com", client.getEmail());
  }

  @Test
  void putRejectsTakenEmail() {
    ClientPutDto dto = putDto();
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client("Old", "old@example.com")));
    when(clientRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", 1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.put(1L, dto));
  }

  @Test
  void putRejectsTakenName() {
    ClientPutDto dto = putDto();
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client("Old", "old@example.com")));
    when(clientRepository.existsByNameIgnoreCaseAndIdNot("New", 1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> clientService.put(1L, dto));
  }

  @Test
  void putReplacesUserFields() {
    Client client = client("Old", "old@example.com");
    ClientPutDto dto = new ClientPutDto();
    dto.setName("New");
    dto.setEmail("NEW@EXAMPLE.COM");
    dto.setPassword("secret");
    dto.setRole(UserRole.ADMIN);
    dto.setStatus(UserStatus.BLOCKED);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().name("New").build());

    clientService.put(1L, dto);

    assertEquals("New", client.getName());
    assertEquals("new@example.com", client.getEmail());
    assertEquals("encoded", client.getPassword());
    assertEquals(UserRole.ADMIN, client.getRole());
    assertEquals(UserStatus.BLOCKED, client.getStatus());
  }

  @Test
  void putUsesDefaultRoleAndStatusAndSkipsBlankPassword() {
    Client client = client("Old", "old@example.com");
    ClientPutDto dto = putDto();
    dto.setPassword(" ");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().build());

    clientService.put(1L, dto);

    assertEquals(UserRole.CUSTOMER, client.getRole());
    assertEquals(UserStatus.ACTIVE, client.getStatus());
    assertEquals("encoded", client.getPassword());
  }

  @Test
  void putSkipsNullPassword() {
    Client client = client("Old", "old@example.com");
    ClientPutDto dto = putDto();

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(client)).thenReturn(client);
    when(clientMapper.toFullDto(client)).thenReturn(ClientFullDto.builder().build());

    clientService.put(1L, dto);

    assertEquals("encoded", client.getPassword());
  }

  @Test
  void normalizeEmailReturnsNullForNullInput() throws ReflectiveOperationException {
    Method method = ClientService.class.getDeclaredMethod("normalizeEmail", String.class);
    method.setAccessible(true);

    assertEquals(null, method.invoke(clientService, new Object[] { null }));
  }

  @Test
  void getByIdThrowsWhenMissing() {
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> clientService.getById(1L));
  }

  @Test
  void deleteRemovesExistingClient() {
    Client client = client("Reader", "reader@example.com");
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

    clientService.delete(1L);

    verify(clientRepository).delete(client);
  }

  private static Client client(String name, String email) {
    return Client.builder().id(1L).name(name).email(email).password("encoded").build();
  }

  private static ClientPutDto putDto() {
    ClientPutDto dto = new ClientPutDto();
    dto.setName("New");
    dto.setEmail("new@example.com");
    return dto;
  }
}
