package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "books", ignore = true)
  Publisher toAuthor(PublisherFullDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "books", ignore = true)
  Publisher toAuthor(PublisherCreateDto dto);

  PublisherFullDto toFullDto(Publisher dto);

  @Mapping(target = "id", ignore = true)
  PublisherPatchDto toPatchDto(PublisherPutDto dto);
}
