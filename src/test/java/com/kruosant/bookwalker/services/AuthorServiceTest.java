package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.AuthorMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
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
class AuthorServiceTest {
  @Mock
  private AuthorRepository authorRepository;
  @Mock
  private AuthorMapper authorMapper;
  @InjectMocks
  private AuthorService authorService;

  @Test
  void getAllMapsAuthors() {
    Author author = author("Jane");
    AuthorFullDto dto = AuthorFullDto.builder().name("Jane").build();

    when(authorRepository.findAll()).thenReturn(List.of(author));
    when(authorMapper.toFullDto(author)).thenReturn(dto);

    assertEquals(List.of(dto), authorService.getAll());
  }

  @Test
  void createSavesNewAuthor() {
    AuthorCreateDto createDto = new AuthorCreateDto();
    createDto.setName("Jane");
    createDto.setBio("Bio");
    AuthorFullDto dto = AuthorFullDto.builder().name("Jane").build();

    when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(authorMapper.toFullDto(any(Author.class))).thenReturn(dto);

    AuthorFullDto result = authorService.create(createDto);

    assertEquals(dto, result);
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  void getByIdMapsAuthor() {
    Author author = author("Jane");
    AuthorFullDto dto = AuthorFullDto.builder().name("Jane").build();

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(authorMapper.toFullDto(author)).thenReturn(dto);

    assertEquals(dto, authorService.getById(1L));
  }

  @Test
  void patchOnlyChangesProvidedFields() {
    Author author = author("Jane");
    AuthorPatchDto patchDto = new AuthorPatchDto();
    patchDto.setBio("Updated bio");

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepository.save(author)).thenReturn(author);
    when(authorMapper.toFullDto(author)).thenReturn(AuthorFullDto.builder().bio("Updated bio").build());

    authorService.patch(1L, patchDto);

    assertEquals("Jane", author.getName());
    assertEquals("Updated bio", author.getBio());
  }

  @Test
  void patchChangesEveryProvidedField() {
    Author author = author("Jane");
    AuthorPatchDto patchDto = new AuthorPatchDto();
    patchDto.setName("Janet");
    patchDto.setBio("New bio");
    patchDto.setCountry("US");
    patchDto.setWebsite("https://author.example");

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepository.save(author)).thenReturn(author);
    when(authorMapper.toFullDto(author)).thenReturn(AuthorFullDto.builder().build());

    authorService.patch(1L, patchDto);

    assertEquals("Janet", author.getName());
    assertEquals("New bio", author.getBio());
    assertEquals("US", author.getCountry());
    assertEquals("https://author.example", author.getWebsite());
  }

  @Test
  void patchLeavesNullFieldsUntouched() {
    Author author = author("Jane");
    AuthorPatchDto patchDto = new AuthorPatchDto();
    patchDto.setName("Janet");

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepository.save(author)).thenReturn(author);
    when(authorMapper.toFullDto(author)).thenReturn(AuthorFullDto.builder().build());

    authorService.patch(1L, patchDto);

    assertEquals("Bio", author.getBio());
  }

  @Test
  void putReplacesFields() {
    Author author = author("Old");
    AuthorPutDto putDto = new AuthorPutDto();
    putDto.setName("New");
    putDto.setCountry("UK");

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepository.save(author)).thenReturn(author);
    when(authorMapper.toFullDto(author)).thenReturn(AuthorFullDto.builder().name("New").build());

    authorService.put(1L, putDto);

    assertEquals("New", author.getName());
    assertEquals("UK", author.getCountry());
  }

  @Test
  void deleteThrowsWhenMissing() {
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authorService.delete(1L));
  }

  @Test
  void deleteRemovesExistingAuthor() {
    Author author = author("Jane");
    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

    authorService.delete(1L);

    verify(authorRepository).delete(author);
  }

  private static Author author(String name) {
    return Author.builder().id(1L).name(name).bio("Bio").build();
  }
}
