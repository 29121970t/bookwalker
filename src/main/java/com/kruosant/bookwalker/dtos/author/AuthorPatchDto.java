package com.kruosant.bookwalker.dtos.author;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Set;


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
  private String middleName;
  @Nullable
  private String surname;
  @Nullable
  private String bio;
  @Nullable
  private Set<Long> books;
}