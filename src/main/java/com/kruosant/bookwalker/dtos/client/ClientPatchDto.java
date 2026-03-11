package com.kruosant.bookwalker.dtos.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPatchDto {
  private String userName;
}