package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.security.AuthenticatedClientService;
import com.kruosant.bookwalker.services.AsyncOrderService;
import com.kruosant.bookwalker.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;
  private final AsyncOrderService asyncOrderService;
  private final AuthenticatedClientService authenticatedClientService;

  @GetMapping
  public List<OrderFullDto> getAll() {
    return orderService.getAll();
  }

  @GetMapping("/me")
  public List<OrderFullDto> getMyOrders() {
    Client client = authenticatedClientService.getCurrentClient();
    return orderService.getForClient(client.getId());
  }

  @GetMapping("/{id}")
  public OrderFullDto getById(@PathVariable Long id) {
    return orderService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderFullDto create(@Valid @RequestBody OrderCreateDto dto) {
    return orderService.create(dto);
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.CREATED)
  public List<OrderFullDto> createBulk(@Valid @RequestBody List<OrderCreateDto> dtos) {
    return orderService.createBulkTransactional(dtos);
  }

  @PatchMapping("/{id}")
  public OrderFullDto patch(@PathVariable Long id, @RequestBody OrderPatchDto dto) {
    return orderService.update(id, dto);
  }

  @PutMapping("/{id}")
  public OrderFullDto put(@PathVariable Long id, @Valid @RequestBody OrderPutDto dto) {
    return orderService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    orderService.delete(id);
  }
}
