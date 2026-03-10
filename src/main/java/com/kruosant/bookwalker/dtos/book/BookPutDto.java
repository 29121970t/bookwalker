package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPutDto {
  @NotNull
  private String name;
  @NotNull
  private List<Long> authors;
  @NotNull
  private Long pageCount;
  @NotNull
  private LocalDate publishDate;
  @NotNull
  private Long publisher;
  @NotNull
  private Double price;

}