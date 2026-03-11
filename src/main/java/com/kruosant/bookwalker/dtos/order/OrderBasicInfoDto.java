package com.kruosant.bookwalker.dtos.order;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBasicInfoDto {
  private Long id;
  private LocalDateTime timeStamp;
  private Set<BookBasicInfoDto> books;
}
