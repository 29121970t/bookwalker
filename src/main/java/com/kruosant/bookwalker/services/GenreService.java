package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Genre;
import com.kruosant.bookwalker.dtos.genre.GenreDto;
import com.kruosant.bookwalker.dtos.genre.GenreUpsertDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
  private final GenreRepository genreRepository;

  @Transactional(readOnly = true)
  public List<GenreDto> getAll() {
    return genreRepository.findAll().stream().map(this::toDto).toList();
  }

  @Transactional
  public GenreDto create(GenreUpsertDto dto) {
    Genre genre = Genre.builder().name(dto.getName()).description(dto.getDescription()).build();
    return toDto(genreRepository.save(genre));
  }

  @Transactional
  public GenreDto update(Long id, GenreUpsertDto dto) {
    Genre genre = getEntity(id);
    genre.setName(dto.getName());
    genre.setDescription(dto.getDescription());
    return toDto(genreRepository.save(genre));
  }

  @Transactional
  public void delete(Long id) {
    genreRepository.delete(getEntity(id));
  }

  public Genre getEntity(Long id) {
    return genreRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }

  private GenreDto toDto(Genre genre) {
    return GenreDto.builder().id(genre.getId()).name(genre.getName()).description(genre.getDescription()).build();
  }
}
