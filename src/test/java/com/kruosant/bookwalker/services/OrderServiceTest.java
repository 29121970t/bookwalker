package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderItemRequestDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.OrderMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private ClientRepository clientRepository;
  @Mock
  private BookRepository bookRepository;
  @Mock
  private OrderMapper orderMapper;
  @InjectMocks
  private OrderService orderService;

  @Test
  void getForClientMapsOrders() {
    Order order = Order.builder().id(1L).build();
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(orderRepository.findAllByClientId(1L)).thenReturn(List.of(order));
    when(orderMapper.toFullDto(order)).thenReturn(dto);

    assertEquals(List.of(dto), orderService.getForClient(1L));
  }

  @Test
  void getAllMapsOrders() {
    Order order = Order.builder().id(1L).build();
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(orderRepository.findAll()).thenReturn(List.of(order));
    when(orderMapper.toFullDto(order)).thenReturn(dto);

    assertEquals(List.of(dto), orderService.getAll());
  }

  @Test
  void getByIdMapsOrder() {
    Order order = Order.builder().id(1L).build();
    OrderFullDto dto = OrderFullDto.builder().id(1L).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toFullDto(order)).thenReturn(dto);

    assertEquals(dto, orderService.getById(1L));
  }

  @Test
  void createBuildsItemsAndTotal() {
    OrderCreateDto dto = createDto();
    Book book = Book.builder().id(1L).price(BigDecimal.TEN).build();
    OrderFullDto fullDto = OrderFullDto.builder().total(BigDecimal.valueOf(20)).build();

    when(clientRepository.findById(1L)).thenReturn(Optional.of(Client.builder().id(1L).build()));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderMapper.toFullDto(any(Order.class))).thenReturn(fullDto);

    assertEquals(fullDto, orderService.create(dto));
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createUsesProvidedDate() {
    OrderCreateDto dto = createDto();
    LocalDateTime date = LocalDateTime.of(2026, 5, 6, 7, 0);
    dto.setDate(date);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(Client.builder().id(1L).build()));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).price(BigDecimal.ONE).build()));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderMapper.toFullDto(any(Order.class))).thenReturn(OrderFullDto.builder().build());

    orderService.create(dto);

    verify(orderRepository).save(org.mockito.ArgumentMatchers.argThat(order -> date.equals(order.getDate())));
  }

  @Test
  void createBulkCreatesEveryOrder() {
    OrderCreateDto dto = createDto();
    when(clientRepository.findById(1L)).thenReturn(Optional.of(Client.builder().id(1L).build()));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).price(BigDecimal.ONE).build()));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderMapper.toFullDto(any(Order.class))).thenReturn(OrderFullDto.builder().build());

    assertEquals(2, orderService.createBulkTransactional(List.of(dto, dto)).size());
  }

  @Test
  void patchReplacesItemsAndRecalculatesTotal() {
    Order order = Order.builder().id(1L).items(new java.util.ArrayList<>()).build();
    OrderPatchDto dto = new OrderPatchDto();
    dto.setStatus("Paid");
    dto.setItems(List.of(item(1L, 3)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).price(BigDecimal.valueOf(5)).build()));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toFullDto(order)).thenReturn(OrderFullDto.builder().status("Paid").build());

    orderService.update(1L, dto);

    assertEquals("Paid", order.getStatus());
    assertEquals(BigDecimal.valueOf(15), order.getTotal());
    assertEquals(1, order.getItems().size());
  }

  @Test
  void patchChangesEveryProvidedField() {
    LocalDateTime date = LocalDateTime.of(2026, 5, 6, 5, 0);
    Order order = Order.builder().id(1L).items(new java.util.ArrayList<>()).build();
    OrderPatchDto dto = new OrderPatchDto();
    dto.setDate(date);
    dto.setStatus("Paid");
    dto.setPaymentMethod("CARD");
    dto.setDeliveryCity("Minsk");
    dto.setItems(List.of(item(1L, 1)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).price(BigDecimal.ONE).build()));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toFullDto(order)).thenReturn(OrderFullDto.builder().build());

    orderService.update(1L, dto);

    assertEquals(date, order.getDate());
    assertEquals("CARD", order.getPaymentMethod());
    assertEquals("Minsk", order.getDeliveryCity());
  }

  @Test
  void patchLeavesNullFieldsUntouched() {
    Order order = Order.builder().id(1L).status("Processing").items(new java.util.ArrayList<>()).build();
    OrderPatchDto dto = new OrderPatchDto();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toFullDto(order)).thenReturn(OrderFullDto.builder().build());

    orderService.update(1L, dto);

    assertEquals("Processing", order.getStatus());
    assertTrue(order.getItems().isEmpty());
  }

  @Test
  void putKeepsExistingDateWhenMissing() {
    LocalDateTime date = LocalDateTime.of(2026, 5, 6, 4, 0);
    Order order = Order.builder().id(1L).date(date).items(new java.util.ArrayList<>()).build();
    OrderPutDto dto = new OrderPutDto();
    dto.setStatus("Delivered");
    dto.setItems(List.of(item(1L, 1)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).discountPrice(BigDecimal.valueOf(7)).price(BigDecimal.TEN).build()));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toFullDto(order)).thenReturn(OrderFullDto.builder().build());

    orderService.update(1L, dto);

    assertEquals(date, order.getDate());
    assertEquals(BigDecimal.valueOf(7), order.getTotal());
  }

  @Test
  void putUsesProvidedDate() {
    LocalDateTime date = LocalDateTime.of(2026, 5, 6, 6, 0);
    Order order = Order.builder().id(1L).items(new java.util.ArrayList<>()).build();
    OrderPutDto dto = new OrderPutDto();
    dto.setDate(date);
    dto.setStatus("Delivered");
    dto.setPaymentMethod("CASH");
    dto.setDeliveryCity("Minsk");
    dto.setItems(List.of(item(1L, 1)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(Book.builder().id(1L).price(BigDecimal.ONE).build()));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderMapper.toFullDto(order)).thenReturn(OrderFullDto.builder().build());

    orderService.update(1L, dto);

    assertEquals(date, order.getDate());
    assertEquals("CASH", order.getPaymentMethod());
  }

  @Test
  void deleteRemovesExistingOrder() {
    Order order = Order.builder().id(1L).build();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    orderService.delete(1L);

    verify(orderRepository).delete(order);
  }

  @Test
  void getByIdThrowsWhenMissing() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> orderService.getById(1L));
  }

  @Test
  void createThrowsWhenClientMissing() {
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> orderService.create(createDto()));
  }

  private static OrderCreateDto createDto() {
    OrderCreateDto dto = new OrderCreateDto();
    dto.setClientId(1L);
    dto.setStatus("Processing");
    dto.setItems(List.of(item(1L, 2)));
    return dto;
  }

  private static OrderItemRequestDto item(Long bookId, int quantity) {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setBookId(bookId);
    item.setQuantity(quantity);
    return item;
  }
}
