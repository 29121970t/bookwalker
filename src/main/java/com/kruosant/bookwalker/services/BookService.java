package com.kruosant.bookwalker.services;


import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.BookFullDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class BookService {
  private final BookRepository repo;
  private final BookMapper mapper;

  public BookService(BookRepository repo, BookMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  public List<BookFullDto> getAllByName(String name) {
    List<Book> books = repo.findByName(name);
    if (books.isEmpty()) {
      throw new ResourceNotFoundException("Not found");
    }

    return books.stream().map(mapper::toBookDto).toList();
  }

  public BookFullDto getById(long id) {
    Optional<Book> optionalBook = repo.findFirstById(id);
    if (optionalBook.isEmpty()) {
      throw new ResourceNotFoundException("Not found");
    }
    return mapper.toBookDto(optionalBook.get());
  }

  public BookFullDto create(Book book) {
    return mapper.toBookDto(repo.save(book));
  }

  public List<BookFullDto> getAll() {
    return mapper.toBookDto(repo.getAll());
  }
}
