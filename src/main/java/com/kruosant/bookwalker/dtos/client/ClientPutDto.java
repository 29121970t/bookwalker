package com.kruosant.bookwalker.dtos.client;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPutDto {
  @NotNull
  private String userName;
  @NotNull
  private String password;
}
