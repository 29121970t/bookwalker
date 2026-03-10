package com.kruosant.bookwalker.dtos.publisher;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherBasicInfoDto {
  private Long id;
  private String name;
}