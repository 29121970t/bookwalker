package com.kruosant.bookwalker.dtos.publisher;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherPatchDto {
  @Nullable
  private Long id;
  @Nullable
  private String name;
  @Nullable
  private List<Long> books;
}
