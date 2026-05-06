package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPutDto {
  @NotBlank
  private String name;
  @Email
  @NotBlank
  private String email;
  private String city;
  private String password;
  private UserRole role;
  private UserStatus status;
}
