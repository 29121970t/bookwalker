package com.kruosant.bookwalker.dtos.author;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorPutDto {
  @NotNull
  private String name;
  @NotNull
  private String middleName;
  @NotNull
  private String surname;
  @NotNull
  private String bio;
  @NotNull
  private Set<Long> books;
}