package com.kruosant.bookwalker.dtos.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderCreateDto {
  @NotNull
  private Long clientId;
  @NotBlank
  private String status;
  private LocalDateTime date;
  private String paymentMethod;
  private String deliveryCity;
  @NotEmpty
  private List<OrderItemRequestDto> items;
}
