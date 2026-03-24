package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.book.*;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import lombok.NonNull;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

  BookFullDto toFullDto(Book book);

  List<BookFullDto> toFullDto(List<Book> book);

  Set<BookFullDto> toFullDto(Set<Book> book);


  BookBasicInfoDto toBasicInfoDto(Book book);

  List<BookBasicInfoDto> toBasicInfoDto(List<Book> book);

  Set<BookBasicInfoDto> toBasicInfoDto(Set<Book> book);

  BookPatchDto toPatchDto(BookPutDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authors", ignore = true)
  @Mapping(target = "publisher", ignore = true)
  void updateBook(@MappingTarget Book book, BookPatchDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authors", ignore = true)
  @Mapping(target = "publisher", ignore = true)
  void updateBook(@MappingTarget Book book, BookPutDto dto);

}