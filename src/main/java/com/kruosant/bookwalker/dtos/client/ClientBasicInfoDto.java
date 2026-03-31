package com.kruosant.bookwalker.dtos.client;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientBasicInfoDto {
  private Long id;
  @Schema(description = "Clients's username", example = "abova", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;
}
