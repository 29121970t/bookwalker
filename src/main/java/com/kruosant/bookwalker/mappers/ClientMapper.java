package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.dtos.client.ClientBasicInfoDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.order.OrderBasicInfoDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClientMapper {
  public ClientBasicInfoDto toBasicInfoDto(Client client) {
    return ClientBasicInfoDto.builder()
        .id(client.getId())
        .name(client.getName())
        .email(client.getEmail())
        .build();
  }

  public ClientFullDto toFullDto(Client client) {
    return ClientFullDto.builder()
        .id(client.getId())
        .name(client.getName())
        .email(client.getEmail())
        .city(client.getCity())
        .role(client.getRole().name())
        .status(client.getStatus().name())
        .joinedAt(client.getJoinedAt())
        .orders(client.getOrders().stream()
            .map(order -> OrderBasicInfoDto.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .date(order.getDate())
                .status(order.getStatus())
                .total(order.getTotal())
                .build())
            .collect(Collectors.toList()))
        .build();
  }
}
