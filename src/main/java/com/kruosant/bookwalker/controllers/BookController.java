package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping
  public List<BookFullDto> getAll() {
    return bookService.getAll();
  }

  @GetMapping("/{id}")
  public BookFullDto getById(@PathVariable Long id) {
    return bookService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookFullDto create(@Valid @RequestBody BookCreateDto dto) {
    return bookService.create(dto);
  }

  @PatchMapping("/{id}")
  public BookFullDto patch(@PathVariable Long id, @RequestBody BookPatchDto dto) {
    return bookService.patch(id, dto);
  }

  @PutMapping("/{id}")
  public BookFullDto put(@PathVariable Long id, @Valid @RequestBody BookPutDto dto) {
    return bookService.put(id, dto);
  }

  @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BookFullDto uploadCover(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
    return bookService.uploadCover(id, file);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    bookService.delete(id);
  }
}
