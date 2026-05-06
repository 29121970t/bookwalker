package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.domains.BookFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class BookPatchDto {
  private String title;
  private Set<Long> authors;
  private Long genreId;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private BookFormat format;
  private Integer pages;
  private Integer year;
  private LocalDate publishDate;
  private Set<Long> publisherIds;
  private String blurb;
  private String description;
  private String longDescription;
  private Set<Long> tagIds;
  private Boolean featured;
  private Boolean popular;
  private Boolean newArrival;
}
