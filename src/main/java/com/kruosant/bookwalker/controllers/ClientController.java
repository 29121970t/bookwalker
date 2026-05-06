package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;

  @GetMapping
  public List<ClientFullDto> getAll() {
    return clientService.getAll();
  }

  @GetMapping("/{id}")
  public ClientFullDto getById(@PathVariable Long id) {
    return clientService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientFullDto create(@Valid @RequestBody ClientCreateDto dto) {
    return clientService.create(dto);
  }

  @PatchMapping("/{id}")
  public ClientFullDto patch(@PathVariable Long id, @RequestBody ClientPatchDto dto) {
    return clientService.patch(id, dto);
  }

  @PutMapping("/{id}")
  public ClientFullDto put(@PathVariable Long id, @Valid @RequestBody ClientPutDto dto) {
    return clientService.put(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    clientService.delete(id);
  }
}
