package com.kruosant.bookwalker.dtos.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreUpsertDto {
  @NotBlank
  private String name;
  private String description;
}
