package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.PublisherMapper;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
  private final PublisherRepository publisherRepository;
  private final PublisherMapper publisherMapper;

  @Transactional(readOnly = true)
  public List<PublisherFullDto> getAll() {
    return publisherRepository.findAll().stream().map(publisherMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public PublisherFullDto getById(Long id) {
    return publisherMapper.toFullDto(getEntity(id));
  }

  @Transactional
  public PublisherFullDto create(PublisherCreateDto dto) {
    Publisher publisher = Publisher.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .country(dto.getCountry())
        .website(dto.getWebsite())
        .build();
    return publisherMapper.toFullDto(publisherRepository.save(publisher));
  }

  @Transactional
  public PublisherFullDto patch(Long id, PublisherPatchDto dto) {
    Publisher publisher = getEntity(id);
    if (dto.getName() != null) publisher.setName(dto.getName());
    if (dto.getDescription() != null) publisher.setDescription(dto.getDescription());
    if (dto.getCountry() != null) publisher.setCountry(dto.getCountry());
    if (dto.getWebsite() != null) publisher.setWebsite(dto.getWebsite());
    return publisherMapper.toFullDto(publisherRepository.save(publisher));
  }

  @Transactional
  public PublisherFullDto put(Long id, PublisherPutDto dto) {
    Publisher publisher = getEntity(id);
    publisher.setName(dto.getName());
    publisher.setDescription(dto.getDescription());
    publisher.setCountry(dto.getCountry());
    publisher.setWebsite(dto.getWebsite());
    return publisherMapper.toFullDto(publisherRepository.save(publisher));
  }

  @Transactional
  public void delete(Long id) {
    publisherRepository.delete(getEntity(id));
  }

  public Publisher getEntity(Long id) {
    return publisherRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }
}
