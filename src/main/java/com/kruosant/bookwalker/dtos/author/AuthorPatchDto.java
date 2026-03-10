package com.kruosant.bookwalker.dtos.author;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorPatchDto {
  @Nullable
  private Long id;
  @Nullable
  private String name;
  @Nullable
  private String bio;
  @Nullable
  private List<Long> books;
}