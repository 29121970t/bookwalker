package com.kruosant.bookwalker.dtos.publisher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherFullDto {
  private Long id;
  private String name;
  private String description;
  private String country;
  private String website;
  private Integer booksCount;
}
