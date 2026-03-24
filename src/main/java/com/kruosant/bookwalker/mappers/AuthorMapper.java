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

  Author toAuthor(AuthorFullDto dto);

  Author toAuthor(AuthorCreateDto dto);

  AuthorFullDto toFullDto(Author dto);

  AuthorPatchDto toPatchDto(AuthorPutDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patch(@MappingTarget Author author, AuthorPatchDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void put(@MappingTarget Author author, AuthorPutDto dto);

  Author toAuthor(AuthorPutDto dto);

  default Set<Book> map(Set<Long> value) {
    return HashSet.newHashSet(0);
  }
}
