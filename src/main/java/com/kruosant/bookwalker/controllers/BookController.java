package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.services.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public final class BookController {
  private final BookService service;

  @GetMapping("")
  public List<BookFullDto> getAll() {
    return service.getAll();
  }

  @ResponseStatus(code = HttpStatus.CREATED)
  @PostMapping("")
  public BookFullDto add(@Valid @RequestBody BookCreateDto bookDto) {
    return service.create(bookDto);
  }

  @GetMapping("/{id}")
  public BookFullDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable Long id) {
    service.deleteById(id);
  }

  @PatchMapping("/{id}")
  public BookFullDto patch(@PathVariable Long id, @RequestBody BookPatchDto dto) {
    return service.update(id, dto);
  }

  @PutMapping("/{id}")
  public BookFullDto patch(@PathVariable Long id, @RequestBody @Valid BookPutDto dto) {
    return service.update(id, dto);
  }

  //author endpoints
  @PostMapping("/{bookId}/authors/{authorId}")
  public BookFullDto addAuthor(@PathVariable(name = "bookId") Long bookId, @PathVariable(name = "authorId") Long authorId) {
    return service.addAuthor(bookId, authorId);
  }

  @PostMapping("/{id}/authors")
  public BookFullDto addAuthor(@PathVariable Long id, @RequestBody @Valid Map<String, Long> body) {
    if (!body.containsKey("author")) {
      throw new BadRequestException();
    }
    return service.addAuthor(id, body.get("author"));
  }

  @DeleteMapping("/{bookId}/authors/{authorId}")
  public BookFullDto deleteAuthor(@PathVariable(name = "bookId") Long bookId, @PathVariable(name = "authorId") Long authorId) {
    return service.deleteAuthor(bookId, authorId);
  }

  //publisher endpoints
  @PostMapping("/{bookId}/publisher/{publisherId}")
  public BookFullDto setPublisher(@PathVariable(name = "bookId") Long bookId, @PathVariable(name = "publisherId") Long publisherId) {
    return service.setPublisher(bookId, publisherId);
  }

  @PostMapping("/{id}/publisher")
  public BookFullDto setPublisher(@PathVariable Long id, @RequestBody @Valid Map<String, Long> body) {
    if (!body.containsKey("publisher")) {
      throw new BadRequestException();
    }
    return service.setPublisher(id, body.get("publisher"));
  }

  @GetMapping("/search")
  public List<BookFullDto> getByName(@RequestParam String name) {
    return service.getAllByName(name);
  }


}
