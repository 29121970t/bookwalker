package com.kruosant.bookwalker.dtos.client;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientBasicInfoDto {
  private Long id;
  private String userName;
}
