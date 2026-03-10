package com.kruosant.bookwalker.dtos.author;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorCreateDto {
  private String name;
  private String bio;
}
