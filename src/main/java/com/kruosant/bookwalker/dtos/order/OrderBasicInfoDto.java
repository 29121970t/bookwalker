package com.kruosant.bookwalker.dtos.order;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBasicInfoDto {
  private Long id;
  @Schema(description = "Order's date", requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDateTime date;
  @Schema(description = "Order's books", requiredMode = Schema.RequiredMode.REQUIRED)
  private Set<BookBasicInfoDto> books;
}
