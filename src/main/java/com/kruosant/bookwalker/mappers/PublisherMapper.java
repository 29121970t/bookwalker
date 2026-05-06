package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {
  public PublisherBasicInfoDto toBasicInfoDto(Publisher publisher) {
    return PublisherBasicInfoDto.builder()
        .id(publisher.getId())
        .name(publisher.getName())
        .description(publisher.getDescription())
        .country(publisher.getCountry())
        .website(publisher.getWebsite())
        .build();
  }

  public PublisherFullDto toFullDto(Publisher publisher) {
    return PublisherFullDto.builder()
        .id(publisher.getId())
        .name(publisher.getName())
        .description(publisher.getDescription())
        .country(publisher.getCountry())
        .website(publisher.getWebsite())
        .booksCount(publisher.getBooks().size())
        .build();
  }
}
