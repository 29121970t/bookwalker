package com.kruosant.bookwalker.dtos.publisher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublisherPatchDto {
  private String name;
  private String description;
  private String country;
  private String website;
}
