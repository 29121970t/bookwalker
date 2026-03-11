package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.services.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientController {
  private final ClientService service;

  @GetMapping("")
  public List<ClientFullDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}")
  public ClientFullDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public ClientFullDto create(@Valid @RequestBody ClientCreateDto dto) {
    return service.create(dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PatchMapping("/{id}")
  public ClientFullDto patch(@PathVariable Long id, @RequestBody ClientPatchDto dto) {
    return service.update(id, dto);
  }

  @PutMapping("/{id}")
  public ClientFullDto put(@PathVariable Long id, @RequestBody @Valid ClientPutDto dto) {
    return service.update(id, dto);
  }
}