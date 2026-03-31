package com.kruosant.bookwalker.dtos.author;

import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorFullDto {
  private Long id;
  @NotNull(message = "field is necessary")
  @Schema(description = "Author's name", example = "Jennifer", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  @Schema(description = "Author's middle name", example = " ", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  private String middleName;
  @Schema(description = "Author's surname", example = "Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  private String surname;
  @Schema(description = "Author's biography", example = "Author bio for Jennifer Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  private String bio;
  @Schema(description = "Author's list of books", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<BookBasicInfoDto> books = new ArrayList<>();
}
