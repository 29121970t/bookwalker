package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.order.*;
import com.kruosant.bookwalker.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
  private final OrderService service;

  @GetMapping("")
  public List<OrderFullDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}")
  public OrderFullDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public OrderFullDto create(@Valid @RequestBody OrderCreateDto dto) {
    return service.create(dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PatchMapping("/{id}")
  public OrderFullDto patch(@PathVariable Long id, @RequestBody OrderPatchDto dto) {
    return service.update(id, dto);
  }

  @PutMapping("/{id}")
  public OrderFullDto put(@PathVariable Long id, @RequestBody @Valid OrderPutDto dto) {
    return service.update(id, dto);
  }
}