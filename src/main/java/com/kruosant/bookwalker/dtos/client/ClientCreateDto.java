package com.kruosant.bookwalker.dtos.client;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateDto {
  private String userName;
  private String password;
}