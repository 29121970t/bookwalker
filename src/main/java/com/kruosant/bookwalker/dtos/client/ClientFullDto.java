package com.kruosant.bookwalker.dtos.client;

import com.kruosant.bookwalker.dtos.order.OrderBasicInfoDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientFullDto {
  private Long id;
  private String userName;
  private List<OrderBasicInfoDto> orders;
}