package com.kruosant.bookwalker.dtos.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPutDto {
  @NotBlank(message = "field is required")
  @Schema(description = "Clients's username", example = "abova", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;
  @NotNull(message = "field is required")
  @Schema(description = "Clients's orders", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> orders;

}
