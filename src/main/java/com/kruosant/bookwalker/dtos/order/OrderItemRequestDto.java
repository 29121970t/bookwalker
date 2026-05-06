package com.kruosant.bookwalker.dtos.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {
  @NotNull
  private Long bookId;
  @NotNull
  @Positive
  private Integer quantity;
}
