package com.kruosant.bookwalker.dtos.publisher;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherCreateDto {
  @NotBlank(message = "field is required")
  private String name;
}
