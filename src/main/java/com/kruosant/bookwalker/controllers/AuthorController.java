package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping
  public List<AuthorFullDto> getAll() {
    return authorService.getAll();
  }

  @GetMapping("/{id}")
  public AuthorFullDto getById(@PathVariable Long id) {
    return authorService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AuthorFullDto create(@Valid @RequestBody AuthorCreateDto dto) {
    return authorService.create(dto);
  }

  @PatchMapping("/{id}")
  public AuthorFullDto patch(@PathVariable Long id, @RequestBody AuthorPatchDto dto) {
    return authorService.patch(id, dto);
  }

  @PutMapping("/{id}")
  public AuthorFullDto put(@PathVariable Long id, @Valid @RequestBody AuthorPutDto dto) {
    return authorService.put(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    authorService.delete(id);
  }
}
