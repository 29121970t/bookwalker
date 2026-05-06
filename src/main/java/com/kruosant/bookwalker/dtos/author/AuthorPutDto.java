package com.kruosant.bookwalker.dtos.author;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorPutDto {
  @NotBlank
  private String name;
  private String bio;
  private String country;
  private String website;
}
