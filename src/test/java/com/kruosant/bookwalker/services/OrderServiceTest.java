package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.cashe.OrderSearchCache;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
  void getAllShouldReturnMappedOrders() {
    Order first = new Order();
    first.setId(1L);
    Order second = new Order();
    second.setId(2L);
    OrderFullDto firstDto = OrderFullDto.builder().id(1L).build();
    OrderFullDto secondDto = OrderFullDto.builder().id(2L).build();

    when(orderRepo.findAll()).thenReturn(List.of(first, second));
    when(mapper.toFullDto(first)).thenReturn(firstDto);
    when(mapper.toFullDto(second)).thenReturn(secondDto);

    List<OrderFullDto> result = service.getAll();

    assertEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void getByIdShouldReturnMappedOrder() {
    Order order = new Order();
    order.setId(1L);
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
    when(mapper.toFullDto(order)).thenReturn(dto);

    OrderFullDto result = service.getById(1L);

    assertEquals(dto, result);
  }

  @Test
  void getOrdersWithBooksOfShouldUseCacheHit() {
    PageRequest pageable = PageRequest.of(0, 20);
    Order order = new Order();
    order.setId(1L);
    Page<Order> page = new PageImpl<>(List.of(order));
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(cache.get(any())).thenReturn(Optional.of(page));
    when(mapper.toFullDto(order)).thenReturn(dto);

    Page<OrderFullDto> result = service.getOrdersWithBooksOf("Surname", pageable);

    assertEquals(List.of(dto), result.getContent());
    verify(orderRepo, never()).findByAuthorSurname(any(), any());
  }

  @Test
  void getOrdersWithBooksOfShouldQueryRepositoryOnCacheMiss() {
    PageRequest pageable = PageRequest.of(0, 20);
    Order order = new Order();
    order.setId(1L);
    Page<Order> page = new PageImpl<>(List.of(order));
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(cache.get(any())).thenReturn(Optional.empty());
    when(orderRepo.findByAuthorSurname("Surname", pageable)).thenReturn(page);
    when(mapper.toFullDto(order)).thenReturn(dto);

    Page<OrderFullDto> result = service.getOrdersWithBooksOf("Surname", pageable);

    assertEquals(List.of(dto), result.getContent());
    verify(cache).save(any(), eq(page));
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
    verify(cache).invalidate();
  }

  @Test
  void createShouldUseNowWhenDateIsNullAndAllowNullBooks() {
    Client client = client(1L);
    OrderCreateDto dto = createDto(1L, null, null);
    Order savedOrder = new Order();
    savedOrder.setId(77L);

    when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);
    when(mapper.toFullDto(savedOrder)).thenReturn(OrderFullDto.builder().id(77L).build());

    service.create(dto);

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepo).save(captor.capture());
    assertNotNull(captor.getValue().getDate());
    assertEquals(Set.of(), captor.getValue().getBooks());
  }

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
      return OrderFullDto.builder().id(order.getId()).date(order.getDate()).build();
    });

    List<OrderFullDto> result = service.createBulkTransactional(List.of(firstDto, secondDto));

    assertEquals(2, result.size());
    assertEquals(List.of(100L, 101L), result.stream().map(OrderFullDto::getId).toList());
    verify(orderRepo, times(2)).save(any(Order.class));
    verify(cache).invalidate();
  }

  @Test
  void createBulkTransactionalShouldRejectEmptyPayload() {
    List<OrderCreateDto> emptyOrders = List.of();

    assertThrows(BadRequestException.class, () -> service.createBulkTransactional(emptyOrders));

    verify(cache).invalidate();
    verifyNoInteractions(orderRepo, clientRepo, bookRepo, mapper);
  }

  @Test
  void createBulkNonTransactionalShouldRejectNullPayload() {
    List<OrderCreateDto> orders = null;

    assertThrows(BadRequestException.class, () -> service.createBulkNonTransactional(orders));

    verify(cache).invalidate();
    verifyNoInteractions(orderRepo, clientRepo, bookRepo, mapper);
  }

  @Test
  void createBulkNonTransactionalShouldLeavePreviouslySavedOrdersWhenNextItemFails() {
    Client client = client(1L);
    Book book = book(10L);
    OrderCreateDto validDto = createDto(1L, List.of(10L), LocalDateTime.of(2026, 4, 3, 12, 0));
    OrderCreateDto invalidDto = createDto(1L, List.of(999L), LocalDateTime.of(2026, 4, 3, 12, 5));
    List<OrderCreateDto> orders = List.of(validDto, invalidDto);
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
        () -> service.createBulkNonTransactional(orders));

    verify(orderRepo, times(1)).save(any(Order.class));
    verify(cache).invalidate();
  }

  @Test
  void deleteShouldRemoveOrder() {
    Order order = new Order();
    when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

    service.delete(1L);

    verify(cache).invalidate();
    verify(orderRepo).delete(order);
  }

  @Test
  void updatePatchShouldReplaceBooksWhenProvided() {
    Order order = new Order();
    Book first = book(10L);
    Book second = book(20L);
    OrderPatchDto dto = OrderPatchDto.builder().books(List.of(10L, 20L)).build();
    OrderFullDto fullDto = OrderFullDto.builder().id(1L).build();

    when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
    when(bookRepo.findById(10L)).thenReturn(Optional.of(first));
    when(bookRepo.findById(20L)).thenReturn(Optional.of(second));
    when(orderRepo.save(order)).thenReturn(order);
    when(mapper.toFullDto(order)).thenReturn(fullDto);

    OrderFullDto result = service.update(1L, dto);

    verify(cache).invalidate();
    assertEquals(Set.of(first, second), order.getBooks());
    assertEquals(fullDto, result);
  }

  @Test
  void updatePatchShouldKeepBooksWhenMissing() {
    Order order = new Order();
    Book existing = book(10L);
    order.setBooks(Set.of(existing));
    OrderPatchDto dto = OrderPatchDto.builder().build();
    OrderFullDto fullDto = OrderFullDto.builder().id(1L).build();

    when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepo.save(order)).thenReturn(order);
    when(mapper.toFullDto(order)).thenReturn(fullDto);

    OrderFullDto result = service.update(1L, dto);

    assertEquals(Set.of(existing), order.getBooks());
    assertEquals(fullDto, result);
  }

  @Test
  void updatePutShouldAcceptEmptyBookList() {
    Order order = new Order();
    OrderPutDto dto = OrderPutDto.builder().books(List.of()).build();
    OrderFullDto fullDto = OrderFullDto.builder().id(1L).build();

    when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepo.save(order)).thenReturn(order);
    when(mapper.toFullDto(order)).thenReturn(fullDto);

    OrderFullDto result = service.update(1L, dto);

    assertEquals(Set.of(), order.getBooks());
    assertEquals(fullDto, result);
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
