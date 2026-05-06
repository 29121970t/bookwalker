package com.kruosant.bookwalker.dtos.order;

import com.kruosant.bookwalker.dtos.client.ClientBasicInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFullDto {
  private Long id;
  private String orderCode;
  private LocalDateTime date;
  private String status;
  private BigDecimal total;
  private String paymentMethod;
  private String deliveryCity;
  private ClientBasicInfoDto client;
  private List<OrderItemDto> items;
}
