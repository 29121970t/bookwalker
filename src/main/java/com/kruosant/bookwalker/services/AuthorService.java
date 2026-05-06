package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.AuthorMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final AuthorMapper authorMapper;

  @Transactional(readOnly = true)
  public List<AuthorFullDto> getAll() {
    return authorRepository.findAll().stream().map(authorMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public AuthorFullDto getById(Long id) {
    return authorMapper.toFullDto(getEntity(id));
  }

  @Transactional
  public AuthorFullDto create(AuthorCreateDto dto) {
    Author author = Author.builder()
        .name(dto.getName())
        .bio(dto.getBio())
        .country(dto.getCountry())
        .website(dto.getWebsite())
        .build();
    return authorMapper.toFullDto(authorRepository.save(author));
  }

  @Transactional
  public AuthorFullDto patch(Long id, AuthorPatchDto dto) {
    Author author = getEntity(id);
    if (dto.getName() != null) author.setName(dto.getName());
    if (dto.getBio() != null) author.setBio(dto.getBio());
    if (dto.getCountry() != null) author.setCountry(dto.getCountry());
    if (dto.getWebsite() != null) author.setWebsite(dto.getWebsite());
    return authorMapper.toFullDto(authorRepository.save(author));
  }

  @Transactional
  public AuthorFullDto put(Long id, AuthorPutDto dto) {
    Author author = getEntity(id);
    author.setName(dto.getName());
    author.setBio(dto.getBio());
    author.setCountry(dto.getCountry());
    author.setWebsite(dto.getWebsite());
    return authorMapper.toFullDto(authorRepository.save(author));
  }

  @Transactional
  public void delete(Long id) {
    authorRepository.delete(getEntity(id));
  }

  public Author getEntity(Long id) {
    return authorRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }
}
