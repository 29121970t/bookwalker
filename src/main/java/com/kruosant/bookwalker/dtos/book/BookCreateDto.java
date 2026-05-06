package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.domains.BookFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class BookCreateDto {
  @NotBlank
  private String title;
  @NotEmpty
  private Set<Long> authors;
  @NotNull
  private Long genreId;
  @NotNull
  @DecimalMin("0.01")
  private BigDecimal price;
  @DecimalMin("0.00")
  private BigDecimal discountPrice;
  @NotNull
  private BookFormat format;
  @NotNull
  @Positive
  private Integer pages;
  @NotNull
  @Positive
  private Integer year;
  private LocalDate publishDate;
  @NotEmpty
  private Set<Long> publisherIds;
  private String blurb;
  private String description;
  private String longDescription;
  private Set<Long> tagIds;
  private boolean featured;
  private boolean popular;
  private boolean newArrival;
}
