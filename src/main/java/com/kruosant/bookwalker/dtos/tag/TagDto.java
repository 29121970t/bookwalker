package com.kruosant.bookwalker.dtos.tag;

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
public class TagDto {
  private Long id;
  private String name;
  private String description;
  private String color;
  private boolean featured;
  private Integer usageCount;
}
