package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.dtos.order.OrderBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientFullDto {
  private Long id;
  @Schema(description = "Clients's username", example = "abova", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;
  @Schema(description = "Clients's name", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<OrderBasicInfoDto> orders;
}