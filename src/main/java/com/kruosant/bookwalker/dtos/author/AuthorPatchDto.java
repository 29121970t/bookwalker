package com.kruosant.bookwalker.dtos.author;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorPatchDto {
  private String name;
  private String bio;
  private String country;
  private String website;
}
