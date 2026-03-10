package com.kruosant.bookwalker.dtos.publisher;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PublisherPutDto {
  @NotNull
  private Long id;
  @NotNull
  private String name;
  @NotNull
  private List<Long> books;
}
