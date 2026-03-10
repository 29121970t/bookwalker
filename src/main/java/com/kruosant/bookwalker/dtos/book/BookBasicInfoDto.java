package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
