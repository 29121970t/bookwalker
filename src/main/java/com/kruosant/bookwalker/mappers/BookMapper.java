package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import lombok.NonNull;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class BookMapper {
  @Autowired
  protected AuthorRepository authorRepo;
  @Autowired
  protected PublisherRepository publisherRepo;

  @Mapping(target = "id", ignore = true)
  public abstract Book toEntity(BookCreateDto dto);

  public abstract BookFullDto toFullDto(Book book);

  public Set<Author> map(Set<@NonNull Long> value) throws BadRequestException {
    return value.stream().map(id -> authorRepo.findById(id)
        .orElseThrow(BadRequestException::new)).collect(Collectors.toCollection(HashSet::new));
  }

  public Publisher map(@NonNull Long id) {
    return publisherRepo.findById(id).orElseThrow(BadRequestException::new);
  }
}