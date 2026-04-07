package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "books", ignore = true)
  Author toAuthor(AuthorFullDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "books", ignore = true)
  Author toAuthor(AuthorCreateDto dto);

  AuthorFullDto toFullDto(Author dto);

  AuthorPatchDto toPatchDto(AuthorPutDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void patch(@MappingTarget Author author, AuthorPatchDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void put(@MappingTarget Author author, AuthorPutDto dto);

  @Mapping(target = "id", ignore = true)
  Author toAuthor(AuthorPutDto dto);

  default Set<Book> map(Set<Long> value) {
    return HashSet.newHashSet(16);
  }
}
