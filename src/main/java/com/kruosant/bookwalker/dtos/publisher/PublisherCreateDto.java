package com.kruosant.bookwalker.dtos.publisher;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublisherCreateDto {
  @NotBlank
  private String name;
  private String description;
  private String country;
  private String website;
}
