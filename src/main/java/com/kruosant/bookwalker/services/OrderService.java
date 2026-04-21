package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.cashe.OrderSearchCache;
import com.kruosant.bookwalker.cashe.OrderSearchCacheKey;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.OrderMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
  private final OrderRepository orderRepo;
  private final ClientRepository clientRepo;
  private final BookRepository bookRepo;
  private final OrderMapper mapper;
  private final OrderSearchCache cache;

  @Transactional(readOnly = true)
  public Page<OrderFullDto> getAll(Pageable pageable) {
    return orderRepo.findAll(pageable).map(mapper::toFullDto);
  }

  @Transactional(readOnly = true)
  public OrderFullDto getById(Long id) {
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    return mapper.toFullDto(order);
  }

  @Transactional(readOnly = true)
  public Page<OrderFullDto> getOrdersWithBooksOf(String surname, Pageable p) {
    OrderSearchCacheKey key = OrderSearchCacheKey.create(surname, p);
    Optional<Page<Order>> pageOpt = cache.get(key);
    if (pageOpt.isPresent()) {
      return pageOpt.get().map(mapper::toFullDto);
    }
    Page<Order> newRequest = orderRepo.findByAuthorSurname(surname, p);
    cache.save(key, newRequest);
    return newRequest.map(mapper::toFullDto);
  }

  @Transactional
  public OrderFullDto create(OrderCreateDto dto) {
    cache.invalidate();
    return saveAndMap(dto);
  }

  @Transactional
  public List<OrderFullDto> createBulkTransactional(List<OrderCreateDto> dtos) {
    cache.invalidate();
    return requireBulkPayload(dtos).stream().map(this::saveAndMap).toList();
  }

  @Transactional
  public void delete(Long id) {
    cache.invalidate();
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    orderRepo.delete(order);
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPatchDto dto) {
    cache.invalidate();
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    if (dto.getBooks() != null) {
      order.setBooks(fetchBooks(dto.getBooks()));
    }
    return mapper.toFullDto(orderRepo.save(order));
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPutDto dto) {
    cache.invalidate();
    Order order = orderRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    order.setBooks(fetchBooks(dto.getBooks()));

    return mapper.toFullDto(orderRepo.save(order));
  }

  private OrderFullDto saveAndMap(OrderCreateDto dto) {
    return mapper.toFullDto(orderRepo.save(buildOrder(dto)));
  }

  private Order buildOrder(OrderCreateDto dto) {
    Order order = new Order();
    order.setClient(fetchClient(dto.getClient()));
    order.setBooks(fetchBooks(dto.getBooks()));
    order.setDate(Optional.ofNullable(dto.getDate()).orElseGet(LocalDateTime::now));
    return order;
  }

  private Client fetchClient(Long clientId) {
    return clientRepo.findById(clientId).orElseThrow(ResourceNotFoundException::new);
  }

  private Set<Book> fetchBooks(List<Long> bookIds) {
    return Optional.ofNullable(bookIds)
        .filter(ids -> !ids.isEmpty())
        .map(ids -> ids.stream()
            .map(this::fetchBook)
            .collect(Collectors.toCollection(HashSet::new)))
        .orElseGet(HashSet::new);
  }

  private Book fetchBook(Long bookId) {
    return bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
  }

  private List<OrderCreateDto> requireBulkPayload(List<OrderCreateDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
      throw new BadRequestException();
    }
    return dtos;
  }
}
