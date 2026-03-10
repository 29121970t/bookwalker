package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.services.PublisherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publishers")
@AllArgsConstructor
public class PublisherController {
  private final PublisherService service;

  @GetMapping("")
  public List<PublisherFullDto> getAll() {
    return service.getAll();
  }

  @ResponseStatus(code = HttpStatus.CREATED)
  @PostMapping("")
  public PublisherFullDto add(@Valid @RequestBody PublisherCreateDto bookDto) {
    return service.create(bookDto);
  }

  @GetMapping("/{id}")
  public PublisherFullDto getById(@PathVariable Long id) {
    return service.getAuthorById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable Long id) {
    service.delete(id);
  }

  @PatchMapping("/{id}")
  public PublisherFullDto patch(@PathVariable Long id, @RequestBody PublisherPatchDto dto) {
    return service.update(id, dto);
  }

  @PutMapping("/{id}")
  public PublisherFullDto patch(@PathVariable Long id, @RequestBody @Valid PublisherPutDto dto) {
    return service.update(id, dto);
  }

  //books endpoints
  @PostMapping("/{publisherId}/books/{bookId}")
  public PublisherFullDto addBook(@PathVariable(name = "publisherId") Long publisherId, @PathVariable(name = "bookId") Long bookId) {
    return service.addBook(bookId, publisherId);
  }

  @PostMapping("/{id}/books")
  public PublisherFullDto addBook(@PathVariable Long id, @RequestBody @Valid Map<String, Long> body) {
    if (!body.containsKey("book")) {
      throw new BadRequestException();
    }
    return service.addBook(body.get("book"), id);
  }

  @DeleteMapping("/{publisherId}/books/{bookId}")
  public PublisherFullDto deleteBook(@PathVariable(name = "publisherId") Long publisherId, @PathVariable(name = "bookId") Long bookId) {
    return service.deleteBook(bookId, publisherId);
  }
}
