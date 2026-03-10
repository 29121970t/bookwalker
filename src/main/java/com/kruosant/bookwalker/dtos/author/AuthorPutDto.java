package com.kruosant.bookwalker.dtos.author;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorPutDto {
  @NotNull
  private Long id;
  @NotNull
  private String name;
  @NotNull
  private String bio;
  @NotNull
  private List<Long> books;
}