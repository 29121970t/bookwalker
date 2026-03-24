package com.kruosant.bookwalker.dtos.author;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorCreateDto {
  @NonNull
  private String name;
  @NonNull
  private String middleName;
  @NonNull
  private String surname;
  @NonNull
  private String bio;
}
