package com.kruosant.bookwalker.dtos.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPutDto {
  @Schema(description = "Book's name", example = "Book Title", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "field is necessary")
  private String name;

  @Schema(description = "Book's publish date", example = "2004-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "should be positive number")
  @PastOrPresent(message = "cannot be in future")
  private LocalDate publishDate;

  @Schema(description = "Book's authors", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  private Set<Long> authors;


  @NotNull(message = "is necessary")
  @Schema(description = "Book's page count", example = "300", requiredMode = Schema.RequiredMode.REQUIRED)
  @Positive(message = "be positive number")
  private Long pageCount;

  @NotNull(message = "field is necessary")
  @Positive(message = "should be positive number")
  @Schema(description = "Book's publisher", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long publisher;

  @Schema(description = "Book's price", example = "3.1", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  @Positive(message = "should be positive number")
  private Double price;

}