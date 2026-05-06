package com.kruosant.bookwalker.dtos.book;

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
public class BookBasicInfoDto {
  private Long id;
  private String title;
  private BigDecimal price;
  private String coverUrl;
}
