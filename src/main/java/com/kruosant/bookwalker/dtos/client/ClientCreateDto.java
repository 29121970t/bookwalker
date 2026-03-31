package com.kruosant.bookwalker.dtos.client;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateDto {
  @NotBlank(message = "field is required")
  @Schema(description = "Clients's username", example = "abova", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;
  @Schema(description = "Clients's password", example = "KLHhIY899", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "field is required")
  private String password;
}