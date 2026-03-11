package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.OrderMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {
  private final OrderRepository orderRepo;
  private final ClientRepository clientRepo;
  private final BookRepository bookRepo;
  private final OrderMapper mapper;
  @Resource
  private final OrderService service;

  public List<OrderFullDto> getAll() {
    return orderRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  public OrderFullDto getById(Long id) {
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    return mapper.toFullDto(order);
  }

  @Transactional
  public OrderFullDto create(OrderCreateDto dto) {
    Client client = clientRepo.findById(dto.getClient())
        .orElseThrow(ResourceNotFoundException::new);
    Set<Book> books = new HashSet<>();
    if (dto.getBooks() != null) {
      dto.getBooks()
          .forEach(bid -> books.add(bookRepo.findById(bid)
              .orElseThrow(ResourceNotFoundException::new)));
    }

    Order order = new Order();
    order.setClient(client);
    order.setBooks(books);
    order.setTimeStamp(LocalDateTime.now());
    return mapper.toFullDto(orderRepo.save(order));
  }

  @Transactional
  public void delete(Long id) {
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    orderRepo.delete(order);
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPatchDto dto) {
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    if (dto.getBookIds() != null) {
      Set<Book> books = new HashSet<>();
      dto.getBookIds().forEach(bid -> books.add(bookRepo.findById(bid)
          .orElseThrow(ResourceNotFoundException::new)));
      order.setBooks(books);
    }
    return mapper.toFullDto(orderRepo.save(order));
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPutDto dto) {
    return service.update(id, mapper.toPatchDto(dto));
  }
}