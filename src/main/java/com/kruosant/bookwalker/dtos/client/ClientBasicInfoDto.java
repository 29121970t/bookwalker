package com.kruosant.bookwalker.dtos.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientBasicInfoDto {
  private Long id;
  private String name;
  private String email;
}
