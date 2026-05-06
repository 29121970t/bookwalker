package com.kruosant.bookwalker.dtos.book;

import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.genre.GenreDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
import com.kruosant.bookwalker.dtos.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFullDto {
  private Long id;
  private String title;
  private Set<AuthorBasicInfoDto> authors;
  private GenreDto genre;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String format;
  private Integer pages;
  private Integer year;
  private LocalDate publishDate;
  private Set<PublisherBasicInfoDto> publishers;
  private String blurb;
  private String description;
  private String longDescription;
  private Set<TagDto> tags;
  private String coverUrl;
  private boolean featured;
  private boolean popular;
  private boolean newArrival;
}
