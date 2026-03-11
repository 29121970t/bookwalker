package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.services.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public final class AuthorController {
  private final AuthorService service;

  @GetMapping("")
  public List<AuthorFullDto> getAll() {
    return service.getAll();
  }

  @ResponseStatus(code = HttpStatus.CREATED)
  @PostMapping("")
  public AuthorFullDto add(@Valid @RequestBody AuthorCreateDto bookDto) {
    return service.create(bookDto);
  }

  @GetMapping("/{id}")
  public AuthorFullDto getById(@PathVariable Long id) {
    return service.getAuthorById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable Long id) {
    service.delete(id);
  }

  @PatchMapping("/{id}")
  public AuthorFullDto patch(@PathVariable Long id, @RequestBody AuthorPatchDto dto) {
    return service.update(id, dto);
  }

  @PutMapping("/{id}")
  public AuthorFullDto patch(@PathVariable Long id, @RequestBody @Valid AuthorPutDto dto) {
    return service.update(id, dto);
  }

  //books endpoints
  @PostMapping("/{authorId}/books/{bookId}")
  public AuthorFullDto addBook(
      @PathVariable(name = "authorId") Long authorId,
      @PathVariable(name = "bookId") Long bookId) {
    return service.addBook(bookId, authorId);
  }

  @PostMapping("/{id}/books")
  public AuthorFullDto addBook(@PathVariable Long id, @RequestBody @Valid Map<String, Long> body) {
    if (!body.containsKey("book")) {
      throw new BadRequestException();
    }
    return service.addBook(id, body.get("book"));
  }

  @DeleteMapping("/{authorId}/books/{bookId}")
  public AuthorFullDto deleteBook(
      @PathVariable(name = "authorId") Long authorId,
      @PathVariable(name = "bookId") Long bookId) {
    return service.deleteBook(bookId, authorId);
  }

}
