package com.kruosant.bookwalker.dtos.publisher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PublisherPutDto {
  @NotBlank(message = "field is required")
  private String name;
  @NotNull(message = "field is required")
  private List<Long> books;
}
