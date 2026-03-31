package com.kruosant.bookwalker.dtos.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookPatchDto {
  @NotBlank
  @Schema(description = "Book's name", example = "Book Title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String name;

  @Schema(description = "Book's authors", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Nullable
  private Set<Long> authors;

  @Schema(description = "Book's page count", example = "300", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Nullable
  @Positive(message = "be positive number")
  private Long pageCount;

  @Schema(description = "Book's publish date", example = "2004-01-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @PastOrPresent(message = "cannot be in future")
  @Nullable
  private LocalDate publishDate;

  @Nullable
  @Positive(message = "should be positive number")
  @Schema(description = "Book's publisher", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Long publisher;

  @Schema(description = "Book's price", example = "3.1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @Nullable
  @Positive(message = "should be positive number")
  private Double price;
}
