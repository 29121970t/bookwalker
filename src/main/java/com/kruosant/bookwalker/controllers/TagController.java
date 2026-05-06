package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.tag.TagDto;
import com.kruosant.bookwalker.dtos.tag.TagUpsertDto;
import com.kruosant.bookwalker.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
  private final TagService tagService;

  @GetMapping
  public List<TagDto> getAll() {
    return tagService.getAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TagDto create(@Valid @RequestBody TagUpsertDto dto) {
    return tagService.create(dto);
  }

  @PutMapping("/{id}")
  public TagDto update(@PathVariable Long id, @Valid @RequestBody TagUpsertDto dto) {
    return tagService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    tagService.delete(id);
  }
}
