package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

  Author toAuthor(AuthorFullDto dto);

  Author toAuthor(AuthorCreateDto dto);

  AuthorFullDto toFullDto(Author dto);

  AuthorPatchDto toPatchDto(AuthorPutDto dto);
}
