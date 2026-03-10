package com.kruosant.bookwalker.dtos.publisher;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherFullDto {
  private Long id;
  private String name;
  private List<BookBasicInfoDto> books;
}
