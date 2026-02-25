package com.kruosant.bookwalker.controllers;

import org.springframework.web.bind.annotation.*;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.BookCreateDto;
import com.kruosant.bookwalker.dtos.BookFullDto;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.services.BookService;

import java.util.List;

@RestController
public final class BookController {
  private final BookService service;
  private final BookMapper mapper;

  public BookController(BookService service, BookMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/book/{id}")
  public BookFullDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping("/book")
  public List<BookFullDto> getByName(@RequestParam String name) {
    return service.getAllByName(name);
  }

  @GetMapping("/books")
  public List<BookFullDto> getAll() {
    return service.getAll();
  }

  @PostMapping("/book")
  public BookFullDto addBook(@RequestBody BookCreateDto bookd) {
    final Book book = mapper.toBook(bookd);
    return service.create(book);
  }

}
