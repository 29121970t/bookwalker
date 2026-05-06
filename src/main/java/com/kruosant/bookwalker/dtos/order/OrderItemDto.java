package com.kruosant.bookwalker.dtos.order;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
  private Long id;
  private BookBasicInfoDto book;
  private Integer quantity;
  private BigDecimal unitPrice;
}
