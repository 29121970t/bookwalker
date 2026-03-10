package com.kruosant.bookwalker.dtos.book;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookBasicInfoDto {
  private Long id;
  private String name;
  private Long pageCount;
  private LocalDate publishDate;
  private Double price;

}
