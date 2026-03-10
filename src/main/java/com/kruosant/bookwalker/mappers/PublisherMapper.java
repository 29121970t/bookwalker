package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

  Publisher toAuthor(PublisherFullDto dto);

  Publisher toAuthor(PublisherCreateDto dto);

  PublisherFullDto toFullDto(Publisher dto);

  PublisherPatchDto toPatchDto(PublisherPutDto dto);
}
