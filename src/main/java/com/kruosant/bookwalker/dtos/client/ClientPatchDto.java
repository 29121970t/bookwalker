package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPatchDto {
  private String name;
  private String email;
  private String city;
  private String password;
  private UserRole role;
  private UserStatus status;
}
