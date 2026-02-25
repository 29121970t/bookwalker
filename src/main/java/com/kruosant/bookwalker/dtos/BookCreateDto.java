package com.kruosant.bookwalker.dtos;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreateDto {

  private String name;
  private String author;
  private long pageCount;
  private Date publishDate;
  private String publisher;

}


