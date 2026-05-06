package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.domains.OrderItem;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final ClientRepository clientRepository;
  private final BookRepository bookRepository;
  private final OrderMapper orderMapper;

  @Transactional(readOnly = true)
  public List<OrderFullDto> getAll() {
    return orderRepository.findAll().stream().map(orderMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderFullDto> getForClient(Long clientId) {
    return orderRepository.findAllByClientId(clientId).stream().map(orderMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public OrderFullDto getById(Long id) {
    return orderMapper.toFullDto(getEntity(id));
  }

  @Transactional
  public OrderFullDto create(OrderCreateDto dto) {
    return orderMapper.toFullDto(orderRepository.save(buildOrder(dto)));
  }

  @Transactional
  public List<OrderFullDto> createBulkTransactional(List<OrderCreateDto> dtos) {
    return dtos.stream().map(this::create).toList();
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPatchDto dto) {
    Order order = getEntity(id);
    if (dto.getDate() != null) order.setDate(dto.getDate());
    if (dto.getStatus() != null) order.setStatus(dto.getStatus());
    if (dto.getPaymentMethod() != null) order.setPaymentMethod(dto.getPaymentMethod());
    if (dto.getDeliveryCity() != null) order.setDeliveryCity(dto.getDeliveryCity());
    if (dto.getItems() != null) replaceItems(order, dto.getItems());
    recalculateTotal(order);
    return orderMapper.toFullDto(orderRepository.save(order));
  }

  @Transactional
  public OrderFullDto update(Long id, OrderPutDto dto) {
    Order order = getEntity(id);
    order.setDate(dto.getDate() != null ? dto.getDate() : order.getDate());
    order.setStatus(dto.getStatus());
    order.setPaymentMethod(dto.getPaymentMethod());
    order.setDeliveryCity(dto.getDeliveryCity());
    replaceItems(order, dto.getItems());
    recalculateTotal(order);
    return orderMapper.toFullDto(orderRepository.save(order));
  }

  @Transactional
  public void delete(Long id) {
    orderRepository.delete(getEntity(id));
  }

  private Order buildOrder(OrderCreateDto dto) {
    Client client = clientRepository.findById(dto.getClientId()).orElseThrow(ResourceNotFoundException::new);
    Order order = Order.builder()
        .client(client)
        .date(dto.getDate() != null ? dto.getDate() : LocalDateTime.now())
        .status(dto.getStatus())
        .paymentMethod(dto.getPaymentMethod())
        .deliveryCity(dto.getDeliveryCity())
        .orderCode("BW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
        .build();
    replaceItems(order, dto.getItems());
    recalculateTotal(order);
    return order;
  }

  private void replaceItems(Order order, List<OrderItemRequestDto> requests) {
    order.getItems().clear();
    for (OrderItemRequestDto request : requests) {
      Book book = bookRepository.findById(request.getBookId()).orElseThrow(ResourceNotFoundException::new);
      order.getItems().add(OrderItem.builder()
          .order(order)
          .book(book)
          .quantity(request.getQuantity())
          .unitPrice(book.getDiscountPrice() != null ? book.getDiscountPrice() : book.getPrice())
          .build());
    }
  }

  private void recalculateTotal(Order order) {
    BigDecimal total = order.getItems().stream()
        .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotal(total);
  }

  private Order getEntity(Long id) {
    return orderRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }
}
