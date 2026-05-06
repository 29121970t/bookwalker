package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderItemDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
  private final ClientMapper clientMapper;
  private final BookMapper bookMapper;

  public OrderMapper(ClientMapper clientMapper, BookMapper bookMapper) {
    this.clientMapper = clientMapper;
    this.bookMapper = bookMapper;
  }

  public OrderFullDto toFullDto(Order order) {
    return OrderFullDto.builder()
        .id(order.getId())
        .orderCode(order.getOrderCode())
        .date(order.getDate())
        .status(order.getStatus())
        .total(order.getTotal())
        .paymentMethod(order.getPaymentMethod())
        .deliveryCity(order.getDeliveryCity())
        .client(clientMapper.toBasicInfoDto(order.getClient()))
        .items(order.getItems().stream()
            .map(item -> OrderItemDto.builder()
                .id(item.getId())
                .book(bookMapper.toBasicInfoDto(item.getBook()))
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build())
            .collect(Collectors.toList()))
        .build();
  }
}
