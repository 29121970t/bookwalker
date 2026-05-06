package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.PublisherMapper;
import com.kruosant.bookwalker.repositories.PublisherRepository;
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
class PublisherServiceTest {
  @Mock
  private PublisherRepository publisherRepository;
  @Mock
  private PublisherMapper publisherMapper;
  @InjectMocks
  private PublisherService publisherService;

  @Test
  void getAllMapsPublishers() {
    Publisher publisher = publisher("Press");
    PublisherFullDto dto = PublisherFullDto.builder().name("Press").build();

    when(publisherRepository.findAll()).thenReturn(List.of(publisher));
    when(publisherMapper.toFullDto(publisher)).thenReturn(dto);

    assertEquals(List.of(dto), publisherService.getAll());
  }

  @Test
  void createSavesPublisher() {
    PublisherCreateDto createDto = new PublisherCreateDto();
    createDto.setName("Press");
    createDto.setDescription("Books");
    PublisherFullDto dto = PublisherFullDto.builder().name("Press").build();

    when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(publisherMapper.toFullDto(any(Publisher.class))).thenReturn(dto);

    assertEquals(dto, publisherService.create(createDto));
    verify(publisherRepository).save(any(Publisher.class));
  }

  @Test
  void getByIdMapsPublisher() {
    Publisher publisher = publisher("Press");
    PublisherFullDto dto = PublisherFullDto.builder().name("Press").build();

    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherMapper.toFullDto(publisher)).thenReturn(dto);

    assertEquals(dto, publisherService.getById(1L));
  }

  @Test
  void patchOnlyChangesProvidedFields() {
    Publisher publisher = publisher("Press");
    PublisherPatchDto patchDto = new PublisherPatchDto();
    patchDto.setWebsite("https://press.example");

    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherRepository.save(publisher)).thenReturn(publisher);
    when(publisherMapper.toFullDto(publisher)).thenReturn(PublisherFullDto.builder().build());

    publisherService.patch(1L, patchDto);

    assertEquals("Press", publisher.getName());
    assertEquals("https://press.example", publisher.getWebsite());
  }

  @Test
  void patchChangesEveryProvidedField() {
    Publisher publisher = publisher("Press");
    PublisherPatchDto patchDto = new PublisherPatchDto();
    patchDto.setName("New");
    patchDto.setDescription("New description");
    patchDto.setCountry("US");
    patchDto.setWebsite("https://press.example");

    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherRepository.save(publisher)).thenReturn(publisher);
    when(publisherMapper.toFullDto(publisher)).thenReturn(PublisherFullDto.builder().build());

    publisherService.patch(1L, patchDto);

    assertEquals("New", publisher.getName());
    assertEquals("New description", publisher.getDescription());
    assertEquals("US", publisher.getCountry());
    assertEquals("https://press.example", publisher.getWebsite());
  }

  @Test
  void patchLeavesNullFieldsUntouched() {
    Publisher publisher = publisher("Press");
    PublisherPatchDto patchDto = new PublisherPatchDto();
    patchDto.setName("New");

    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherRepository.save(publisher)).thenReturn(publisher);
    when(publisherMapper.toFullDto(publisher)).thenReturn(PublisherFullDto.builder().build());

    publisherService.patch(1L, patchDto);

    assertEquals("Books", publisher.getDescription());
  }

  @Test
  void putReplacesFields() {
    Publisher publisher = publisher("Old");
    PublisherPutDto putDto = new PublisherPutDto();
    putDto.setName("New");
    putDto.setCountry("US");

    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherRepository.save(publisher)).thenReturn(publisher);
    when(publisherMapper.toFullDto(publisher)).thenReturn(PublisherFullDto.builder().build());

    publisherService.put(1L, putDto);

    assertEquals("New", publisher.getName());
    assertEquals("US", publisher.getCountry());
  }

  @Test
  void getByIdThrowsWhenMissing() {
    when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> publisherService.getById(1L));
  }

  @Test
  void deleteRemovesExistingPublisher() {
    Publisher publisher = publisher("Press");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.delete(1L);

    verify(publisherRepository).delete(publisher);
  }

  private static Publisher publisher(String name) {
    return Publisher.builder().id(1L).name(name).description("Books").build();
  }
}
