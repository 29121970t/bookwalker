package com.kruosant.bookwalker.dtos.auth;

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
public class MeDto {
  private Long id;
  private String name;
  private String email;
  private String city;
  private String role;
}
