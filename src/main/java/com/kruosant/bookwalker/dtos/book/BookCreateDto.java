package com.kruosant.bookwalker.dtos.book;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreateDto {
  @NotNull
  private String name;
  @NotNull
  private Set<Long> authors; //rename to author_ids?
  @NotNull
  private Long pageCount;
  @NotNull
  private LocalDate publishDate;
  @NotNull
  private Long publisher;
  @NotNull(message = "No price provided")
  private Double price;

}


