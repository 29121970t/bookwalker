package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Genre;
import com.kruosant.bookwalker.dtos.genre.GenreDto;
import com.kruosant.bookwalker.dtos.genre.GenreUpsertDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.repositories.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {
  @Mock
  private GenreRepository genreRepository;
  @InjectMocks
  private GenreService genreService;

  @Test
  void getAllMapsGenres() {
    when(genreRepository.findAll()).thenReturn(List.of(genre("Fiction")));

    List<GenreDto> result = genreService.getAll();

    assertEquals(1, result.size());
    assertEquals("Fiction", result.getFirst().getName());
  }

  @Test
  void createSavesGenre() {
    GenreUpsertDto dto = dto("Fiction", "Books");
    when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> {
      Genre genre = invocation.getArgument(0);
      genre.setId(1L);
      return genre;
    });

    GenreDto result = genreService.create(dto);

    assertEquals(1L, result.getId());
    assertEquals("Fiction", result.getName());
  }

  @Test
  void updateReplacesGenre() {
    Genre genre = genre("Old");
    when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
    when(genreRepository.save(genre)).thenReturn(genre);

    genreService.update(1L, dto("New", "Updated"));

    assertEquals("New", genre.getName());
    assertEquals("Updated", genre.getDescription());
  }

  @Test
  void deleteRemovesExistingGenre() {
    Genre genre = genre("Fiction");
    when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

    genreService.delete(1L);

    verify(genreRepository).delete(genre);
  }

  @Test
  void getEntityThrowsWhenMissing() {
    when(genreRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> genreService.getEntity(1L));
  }

  private static Genre genre(String name) {
    return Genre.builder().id(1L).name(name).description("Books").build();
  }

  private static GenreUpsertDto dto(String name, String description) {
    GenreUpsertDto dto = new GenreUpsertDto();
    dto.setName(name);
    dto.setDescription(description);
    return dto;
  }
}
