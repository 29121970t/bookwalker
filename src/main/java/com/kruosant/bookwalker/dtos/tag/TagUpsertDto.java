package com.kruosant.bookwalker.dtos.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagUpsertDto {
  @NotBlank
  private String name;
  private String description;
  private String color;
  private boolean featured;
}
