package com.kruosant.bookwalker.dtos.author;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorFullDto {
  private Long id;
  private String name;
  private String middleName;
  private String surname;
  private String bio;
  private List<BookBasicInfoDto> books = new ArrayList<>();
}
