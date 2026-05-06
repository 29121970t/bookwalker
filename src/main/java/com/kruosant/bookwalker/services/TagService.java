package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Tag;
import com.kruosant.bookwalker.dtos.tag.TagDto;
import com.kruosant.bookwalker.dtos.tag.TagUpsertDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  @Transactional(readOnly = true)
  public List<TagDto> getAll() {
    return tagRepository.findAll().stream().map(this::toDto).toList();
  }

  @Transactional
  public TagDto create(TagUpsertDto dto) {
    Tag tag = Tag.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .color(dto.getColor())
        .featured(dto.isFeatured())
        .build();
    return toDto(tagRepository.save(tag));
  }

  @Transactional
  public TagDto update(Long id, TagUpsertDto dto) {
    Tag tag = getEntity(id);
    tag.setName(dto.getName());
    tag.setDescription(dto.getDescription());
    tag.setColor(dto.getColor());
    tag.setFeatured(dto.isFeatured());
    return toDto(tagRepository.save(tag));
  }

  @Transactional
  public void delete(Long id) {
    tagRepository.delete(getEntity(id));
  }

  public Tag getEntity(Long id) {
    return tagRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }

  private TagDto toDto(Tag tag) {
    return TagDto.builder()
        .id(tag.getId())
        .name(tag.getName())
        .description(tag.getDescription())
        .color(tag.getColor())
        .featured(tag.isFeatured())
        .usageCount(tag.getBooks().size())
        .build();
  }
}
