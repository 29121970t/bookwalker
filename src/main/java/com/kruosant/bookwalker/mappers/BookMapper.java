package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.genre.GenreDto;
import com.kruosant.bookwalker.dtos.tag.TagDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookMapper {
  private final AuthorMapper authorMapper;
  private final PublisherMapper publisherMapper;

  public BookMapper(AuthorMapper authorMapper, PublisherMapper publisherMapper) {
    this.authorMapper = authorMapper;
    this.publisherMapper = publisherMapper;
  }

  public BookBasicInfoDto toBasicInfoDto(Book book) {
    return BookBasicInfoDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .price(book.getPrice())
        .coverUrl(resolveCoverUrl(book))
        .build();
  }

  public BookFullDto toFullDto(Book book) {
    return BookFullDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .authors(book.getAuthors().stream().map(authorMapper::toBasicInfoDto).collect(Collectors.toSet()))
        .genre(GenreDto.builder()
            .id(book.getGenre().getId())
            .name(book.getGenre().getName())
            .description(book.getGenre().getDescription())
            .build())
        .price(book.getPrice())
        .discountPrice(book.getDiscountPrice())
        .format(book.getFormat().name())
        .pages(book.getPages())
        .year(book.getYear())
        .publishDate(book.getPublishDate())
        .publishers(book.getPublishers().stream().map(publisherMapper::toBasicInfoDto).collect(Collectors.toSet()))
        .blurb(book.getBlurb())
        .description(book.getDescription())
        .longDescription(book.getLongDescription())
        .tags(book.getTags().stream()
            .map(tag -> TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .color(tag.getColor())
                .featured(tag.isFeatured())
                .usageCount(tag.getBooks().size())
                .build())
            .collect(Collectors.toSet()))
        .coverUrl(resolveCoverUrl(book))
        .featured(book.isFeatured())
        .popular(book.isPopular())
        .newArrival(book.isNewArrival())
        .build();
  }

  private String resolveCoverUrl(Book book) {
    return book.getCoverPath() == null ? null : "/uploads/" + book.getCoverPath();
  }
}
