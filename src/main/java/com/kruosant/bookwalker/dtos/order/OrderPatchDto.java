package com.kruosant.bookwalker.dtos.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderPatchDto {
  private String status;
  private LocalDateTime date;
  private String paymentMethod;
  private String deliveryCity;
  private List<OrderItemRequestDto> items;
}
