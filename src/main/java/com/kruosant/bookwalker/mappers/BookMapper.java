package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;


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