package com.kruosant.bookwalker.dtos.author;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

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
