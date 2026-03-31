package com.kruosant.bookwalker.dtos.author;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorCreateDto {
  @NotNull(message = "field is necessary")
  @Schema(description = "Author's middle name", example = " ", requiredMode = Schema.RequiredMode.REQUIRED)
  private String middleName;
  @NotNull(message = "field is necessary")
  @Schema(description = "Author's name", example = "Jennifer", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  @Schema(description = "Author's surname", example = "Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "field is necessary")
  private String surname;
  @NotNull(message = "field is necessary")
  @Schema(description = "Author's biography", example = "Author bio for Jennifer Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  private String bio;
}
