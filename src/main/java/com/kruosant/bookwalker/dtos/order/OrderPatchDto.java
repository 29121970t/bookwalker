package com.kruosant.bookwalker.dtos.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPatchDto {
  private List<Long> books;
}