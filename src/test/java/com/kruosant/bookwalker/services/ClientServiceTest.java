package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.ClientMapper;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

  @Mock
  private ClientRepository clientRepo;
  @Mock
  private OrderRepository orderRepo;
  @Mock
  private ClientMapper mapper;

  @InjectMocks
  private ClientService service;

  @Test
  void getAllShouldReturnMappedClients() {
    Client first = client(1L, "reader-1");
    Client second = client(2L, "reader-2");
    ClientFullDto firstDto = ClientFullDto.builder().id(1L).username("reader-1").build();
    ClientFullDto secondDto = ClientFullDto.builder().id(2L).username("reader-2").build();

    when(clientRepo.findAll()).thenReturn(List.of(first, second));
    when(mapper.toFullDto(first)).thenReturn(firstDto);
    when(mapper.toFullDto(second)).thenReturn(secondDto);

    List<ClientFullDto> result = service.getAll();

    assertEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void getByIdShouldReturnMappedClient() {
    Client client = client(1L, "reader-1");
    ClientFullDto dto = ClientFullDto.builder().id(1L).username("reader-1").build();

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(mapper.toFullDto(client)).thenReturn(dto);

    ClientFullDto result = service.getById(1L);

    assertEquals(dto, result);
  }

  @Test
  void getByIdShouldThrowWhenClientMissing() {
    when(clientRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));

    verifyNoInteractions(mapper);
  }

  @Test
  void createShouldSaveMappedClient() {
    ClientCreateDto dto = ClientCreateDto.builder().username("reader-1").password("secret").build();
    Client client = client(1L, "reader-1");
    ClientFullDto fullDto = ClientFullDto.builder().id(1L).username("reader-1").build();

    when(mapper.toClient(dto)).thenReturn(client);
    when(clientRepo.save(client)).thenReturn(client);
    when(mapper.toFullDto(client)).thenReturn(fullDto);

    ClientFullDto result = service.create(dto);

    assertEquals(fullDto, result);
  }

  @Test
  void deleteShouldRemoveOrdersAndClient() {
    Client client = client(1L, "reader-1");
    Order firstOrder = new Order();
    Order secondOrder = new Order();
    client.setOrders(Set.of(firstOrder, secondOrder));

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

    service.delete(1L);

    verify(orderRepo).delete(firstOrder);
    verify(orderRepo).delete(secondOrder);
    verify(clientRepo).delete(client);
  }

  @Test
  void updatePatchShouldChangeUsernameWhenProvided() {
    Client client = client(1L, "reader-1");
    ClientPatchDto dto = ClientPatchDto.builder().username("reader-updated").build();
    ClientFullDto fullDto = ClientFullDto.builder().id(1L).username("reader-updated").build();

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepo.save(client)).thenReturn(client);
    when(mapper.toFullDto(client)).thenReturn(fullDto);

    ClientFullDto result = service.update(1L, dto);

    assertEquals("reader-updated", client.getUsername());
    assertEquals(fullDto, result);
  }

  @Test
  void updatePatchShouldKeepUsernameWhenMissing() {
    Client client = client(1L, "reader-1");
    ClientPatchDto dto = ClientPatchDto.builder().build();
    ClientFullDto fullDto = ClientFullDto.builder().id(1L).username("reader-1").build();

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepo.save(client)).thenReturn(client);
    when(mapper.toFullDto(client)).thenReturn(fullDto);

    ClientFullDto result = service.update(1L, dto);

    assertEquals("reader-1", client.getUsername());
    assertEquals(fullDto, result);
  }

  @Test
  void updatePutShouldReplaceUsername() {
    Client client = client(1L, "reader-1");
    ClientPutDto dto = ClientPutDto.builder().username("reader-put").orders(List.of()).build();
    ClientFullDto fullDto = ClientFullDto.builder().id(1L).username("reader-put").build();

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepo.save(client)).thenReturn(client);
    when(mapper.toFullDto(client)).thenReturn(fullDto);

    ClientFullDto result = service.update(1L, dto);

    assertEquals("reader-put", client.getUsername());
    assertEquals(fullDto, result);
  }

  private static Client client(Long id, String username) {
    Client client = new Client();
    client.setId(id);
    client.setUsername(username);
    return client;
  }
}
