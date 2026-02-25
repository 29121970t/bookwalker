package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.BookCreateDto;
import com.kruosant.bookwalker.dtos.BookFullDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookMapper {
  public Book toBook(BookFullDto bdto) {
    if (bdto == null) {
      return null;
    }

    return Book.builder()
        .id(bdto.getId())
        .name(bdto.getName())
        .author(bdto.getAuthor())
        .pageCount(bdto.getPageCount())
        .publishDate(bdto.getPublishDate())
        .publisher(bdto.getPublisher())
        .build();
  }

  public Book toBook(BookCreateDto bdto) {
    if (bdto == null) {
      return null;
    }

    return Book.builder()
        .id(0)
        .name(bdto.getName())
        .author(bdto.getAuthor())
        .pageCount(bdto.getPageCount())
        .publishDate(bdto.getPublishDate())
        .publisher(bdto.getPublisher())
        .build();
  }

  public BookFullDto toBookDto(Book book) {
    if (book == null) {
      return null;
    }

    return BookFullDto.builder()
        .id(book.getId())
        .name(book.getName())
        .author(book.getAuthor())
        .pageCount(book.getPageCount())
        .publishDate(book.getPublishDate())
        .publisher(book.getPublisher())
        .build();
  }

  public Optional<BookFullDto> toBookDto(Optional<Book> optBook) {
    return optBook.map(this::toBookDto);
  }

  public List<BookFullDto> toBookDto(List<Book> books) {
    return books.stream().map(this::toBookDto).toList();
  }
}