package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.domains.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientCreateDto {
  @NotBlank
  private String name;
  @Email
  @NotBlank
  private String email;
  @NotBlank
  private String password;
  private String city;
  private UserRole role;
}
