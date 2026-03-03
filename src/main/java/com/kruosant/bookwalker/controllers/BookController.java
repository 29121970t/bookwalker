package com.kruosant.bookwalker.controllers;

import org.springframework.web.bind.annotation.*;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.BookCreateDto;
import com.kruosant.bookwalker.dtos.BookFullDto;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/book")
public final class BookController {
  private final BookService service;
  private final BookMapper mapper;

  public BookController(BookService service, BookMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/{id}")
  public BookFullDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping("")
  public List<BookFullDto> getByName(@RequestParam String name) {
    return service.getAllByName(name);
  }

  @GetMapping("/all")
  public List<BookFullDto> getAll() {
    return service.getAll();
  }

  @PostMapping("")
  public BookFullDto addBook(@RequestBody BookCreateDto bookDto) {
    final Book book = mapper.toBook(bookDto);
    return service.create(book);
  }

}
