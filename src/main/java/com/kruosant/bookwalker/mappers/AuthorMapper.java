package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
  public AuthorBasicInfoDto toBasicInfoDto(Author author) {
    return AuthorBasicInfoDto.builder()
        .id(author.getId())
        .name(author.getName())
        .bio(author.getBio())
        .country(author.getCountry())
        .website(author.getWebsite())
        .build();
  }

  public AuthorFullDto toFullDto(Author author) {
    return AuthorFullDto.builder()
        .id(author.getId())
        .name(author.getName())
        .bio(author.getBio())
        .country(author.getCountry())
        .website(author.getWebsite())
        .booksCount(author.getBooks().size())
        .build();
  }
}
