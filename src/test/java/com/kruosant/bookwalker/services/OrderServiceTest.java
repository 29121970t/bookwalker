package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.cashe.OrderSearchCache;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.OrderMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepo;
  @Mock
  private ClientRepository clientRepo;
  @Mock
  private BookRepository bookRepo;
  @Mock
  private OrderMapper mapper;
  @Mock
  private OrderSearchCache cache;

  @InjectMocks
  private OrderService service;

  @Test
  void createBulkTransactionalShouldSaveAllOrders() {
    Client client = client(1L);
    Book firstBook = book(10L);
    Book secondBook = book(20L);
    OrderCreateDto firstDto = createDto(1L, List.of(10L), LocalDateTime.of(2026, 4, 1, 10, 0));
    OrderCreateDto secondDto = createDto(1L, List.of(10L, 20L), LocalDateTime.of(2026, 4, 1, 11, 0));
    AtomicLong ids = new AtomicLong(100L);

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(bookRepo.findById(10L)).thenReturn(Optional.of(firstBook));
    when(bookRepo.findById(20L)).thenReturn(Optional.of(secondBook));
    when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      order.setId(ids.getAndIncrement());
      return order;
    });
    when(mapper.toFullDto(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      return OrderFullDto.builder()
          .id(order.getId())
          .date(order.getDate())
          .build();
    });

    List<OrderFullDto> result = service.createBulkTransactional(List.of(firstDto, secondDto));

    assertEquals(2, result.size());
    assertEquals(List.of(100L, 101L), result.stream().map(OrderFullDto::getId).toList());
    verify(orderRepo, times(2)).save(any(Order.class));
    verify(cache).invalidate();
  }

  @Test
  void createShouldUseDateFromDto() {
    Client client = client(1L);
    Book book = book(10L);
    LocalDateTime date = LocalDateTime.of(2026, 4, 2, 9, 30);
    OrderCreateDto dto = createDto(1L, List.of(10L), date);
    Order savedOrder = new Order();
    savedOrder.setId(77L);
    savedOrder.setDate(date);

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
    when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);
    when(mapper.toFullDto(savedOrder)).thenReturn(OrderFullDto.builder().id(77L).date(date).build());

    service.create(dto);

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepo).save(captor.capture());
    assertEquals(date, captor.getValue().getDate());
    assertEquals(1, captor.getValue().getBooks().size());
  }

  @Test
  void createBulkTransactionalShouldRejectEmptyPayload() {
    assertThrows(BadRequestException.class, () -> service.createBulkTransactional(List.of()));

    verify(cache).invalidate();
    verifyNoInteractions(orderRepo, clientRepo, bookRepo, mapper);
  }

  @Test
  void createBulkNonTransactionalShouldLeavePreviouslySavedOrdersWhenNextItemFails() {
    Client client = client(1L);
    Book book = book(10L);
    OrderCreateDto validDto = createDto(1L, List.of(10L), LocalDateTime.of(2026, 4, 3, 12, 0));
    OrderCreateDto invalidDto = createDto(1L, List.of(999L), LocalDateTime.of(2026, 4, 3, 12, 5));
    AtomicLong ids = new AtomicLong(500L);

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
    when(bookRepo.findById(999L)).thenReturn(Optional.empty());
    when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      order.setId(ids.getAndIncrement());
      return order;
    });
    when(mapper.toFullDto(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      return OrderFullDto.builder().id(order.getId()).build();
    });

    assertThrows(ResourceNotFoundException.class,
        () -> service.createBulkNonTransactional(List.of(validDto, invalidDto)));

    verify(orderRepo, times(1)).save(any(Order.class));
    verify(cache).invalidate();
  }

  private static Client client(Long id) {
    Client client = new Client();
    client.setId(id);
    client.setUsername("reader-" + id);
    return client;
  }

  private static Book book(Long id) {
    Book book = new Book();
    book.setId(id);
    book.setName("Book " + id);
    return book;
  }

  private static OrderCreateDto createDto(Long clientId, List<Long> books, LocalDateTime date) {
    return OrderCreateDto.builder()
        .client(clientId)
        .books(books)
        .date(date)
        .build();
  }
}
