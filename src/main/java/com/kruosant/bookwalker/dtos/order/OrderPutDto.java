package com.kruosant.bookwalker.dtos.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPutDto {
  @NotNull
  private Long client;
  @NotNull
  private List<Long> books;
}
