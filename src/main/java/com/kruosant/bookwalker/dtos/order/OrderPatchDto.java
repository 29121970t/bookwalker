package com.kruosant.bookwalker.dtos.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPatchDto {
  @Schema(description = "Order's books", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<Long> books;
}