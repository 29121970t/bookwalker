package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.services.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {
  private final PublisherService publisherService;

  @GetMapping
  public List<PublisherFullDto> getAll() {
    return publisherService.getAll();
  }

  @GetMapping("/{id}")
  public PublisherFullDto getById(@PathVariable Long id) {
    return publisherService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PublisherFullDto create(@Valid @RequestBody PublisherCreateDto dto) {
    return publisherService.create(dto);
  }

  @PatchMapping("/{id}")
  public PublisherFullDto patch(@PathVariable Long id, @RequestBody PublisherPatchDto dto) {
    return publisherService.patch(id, dto);
  }

  @PutMapping("/{id}")
  public PublisherFullDto put(@PathVariable Long id, @Valid @RequestBody PublisherPutDto dto) {
    return publisherService.put(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    publisherService.delete(id);
  }
}
