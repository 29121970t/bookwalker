package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Tag;
import com.kruosant.bookwalker.dtos.tag.TagDto;
import com.kruosant.bookwalker.dtos.tag.TagUpsertDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
  @Mock
  private TagRepository tagRepository;
  @InjectMocks
  private TagService tagService;

  @Test
  void getAllMapsTagsWithUsageCount() {
    Tag tag = tag("Featured");
    tag.setBooks(Set.of(Book.builder().id(1L).build()));
    when(tagRepository.findAll()).thenReturn(List.of(tag));

    List<TagDto> result = tagService.getAll();

    assertEquals(1, result.size());
    assertEquals(1, result.getFirst().getUsageCount());
  }

  @Test
  void createSavesTag() {
    when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
      Tag tag = invocation.getArgument(0);
      tag.setId(1L);
      return tag;
    });

    TagDto result = tagService.create(dto("Featured", true));

    assertEquals("Featured", result.getName());
    assertEquals(true, result.isFeatured());
  }

  @Test
  void updateReplacesTag() {
    Tag tag = tag("Old");
    when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
    when(tagRepository.save(tag)).thenReturn(tag);

    tagService.update(1L, dto("New", false));

    assertEquals("New", tag.getName());
    assertEquals(false, tag.isFeatured());
  }

  @Test
  void deleteRemovesExistingTag() {
    Tag tag = tag("Featured");
    when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

    tagService.delete(1L);

    verify(tagRepository).delete(tag);
  }

  @Test
  void getEntityThrowsWhenMissing() {
    when(tagRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> tagService.getEntity(1L));
  }

  private static Tag tag(String name) {
    return Tag.builder().id(1L).name(name).description("Books").color("#000000").featured(true).build();
  }

  private static TagUpsertDto dto(String name, boolean featured) {
    TagUpsertDto dto = new TagUpsertDto();
    dto.setName(name);
    dto.setDescription("Books");
    dto.setColor("#ffffff");
    dto.setFeatured(featured);
    return dto;
  }
}
