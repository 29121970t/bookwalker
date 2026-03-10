package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFullDto {
  private Long id;
  private String name;
  private List<AuthorBasicInfoDto> authors;
  private Long pageCount;
  private LocalDate publishDate;
  private PublisherBasicInfoDto publisher;
  private Double price;

}
