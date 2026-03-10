package com.kruosant.bookwalker.dtos.author;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorBasicInfoDto {
  private Long id;
  private String name;
  private String bio;
}

