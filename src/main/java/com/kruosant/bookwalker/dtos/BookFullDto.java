package com.kruosant.bookwalker.dtos;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFullDto {
  private long id;
  private String name;
  private String author;
  private long pageCount;
  private Date publishDate;
  private String publisher;
}
