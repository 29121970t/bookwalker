package com.kruosant.bookwalker.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBasicInfoDto {
  private Long id;
  private String orderCode;
  private LocalDateTime date;
  private String status;
  private BigDecimal total;
}
