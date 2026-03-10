package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookPatchDto {
  @Nullable
  private String name;
  @Nullable
  private List<Long> authors;
  @Nullable
  private Long pageCount;
  @Nullable
  private LocalDate publishDate;
  @Nullable
  private Long publisher;
  @Nullable
  private Double price;
}
