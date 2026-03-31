package com.kruosant.bookwalker.dtos.order;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDto {
  @NotNull(message = "field is required")
  @Schema(description = "Order's client", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long client;
  @NotNull(message = "field is required")
  @Schema(description = "Order's books", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> books;
  @Schema(description = "Order's date", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is required")
  @PastOrPresent(message = "should not be in future")
  private LocalDateTime date;
}
