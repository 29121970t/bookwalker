package com.kruosant.bookwalker.dtos.order;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.client.ClientBasicInfoDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFullDto {
  private Long id;
  private ClientBasicInfoDto client;
  private LocalDateTime date;
  private List<BookBasicInfoDto> books;
}
