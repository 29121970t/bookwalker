package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.genre.GenreDto;
import com.kruosant.bookwalker.dtos.genre.GenreUpsertDto;
import com.kruosant.bookwalker.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
  private final GenreService genreService;

  @GetMapping
  public List<GenreDto> getAll() {
    return genreService.getAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GenreDto create(@Valid @RequestBody GenreUpsertDto dto) {
    return genreService.create(dto);
  }

  @PutMapping("/{id}")
  public GenreDto update(@PathVariable Long id, @Valid @RequestBody GenreUpsertDto dto) {
    return genreService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    genreService.delete(id);
  }
}
