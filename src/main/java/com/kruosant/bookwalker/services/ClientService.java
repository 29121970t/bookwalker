package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.ClientMapper;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {
  private final ClientRepository clientRepo;
  private final OrderRepository orderRepo;
  private final ClientMapper mapper;
  @Resource
  private final ClientService service;

  public List<ClientFullDto> getAll() {
    return clientRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  public ClientFullDto getById(Long id) {
    Client client = clientRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    return mapper.toFullDto(client);
  }

  @Transactional
  public ClientFullDto create(ClientCreateDto dto) {
    return mapper.toFullDto(clientRepo.save(mapper.toClient(dto)));
  }

  @Transactional
  public void delete(Long id) {
    Client client = clientRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    client.getOrders().forEach(orderRepo::delete);
    clientRepo.delete(client);
  }

  @Transactional
  public ClientFullDto update(Long id, ClientPatchDto dto) {
    Client client = clientRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    if (dto.getUserName() != null) {
      client.setUserName(dto.getUserName());
    }

    return mapper.toFullDto(clientRepo.save(client));
  }

  @Transactional
  public ClientFullDto update(Long id, ClientPutDto dto) {
    return service.update(id, mapper.toPatchDto(dto));
  }
}