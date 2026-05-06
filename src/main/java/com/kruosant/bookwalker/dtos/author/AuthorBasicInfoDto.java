package com.kruosant.bookwalker.dtos.author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorBasicInfoDto {
  private Long id;
  private String name;
  private String bio;
  private String country;
  private String website;
}
