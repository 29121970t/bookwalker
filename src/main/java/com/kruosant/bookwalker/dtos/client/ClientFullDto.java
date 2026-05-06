package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.dtos.order.OrderBasicInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientFullDto {
  private Long id;
  private String name;
  private String email;
  private String city;
  private String role;
  private String status;
  private LocalDate joinedAt;
  private List<OrderBasicInfoDto> orders;
}
