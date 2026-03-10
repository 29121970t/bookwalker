package com.kruosant.bookwalker.dtos.publisher;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherBasicInfoDto {
  private Long id;
  private String name;
}