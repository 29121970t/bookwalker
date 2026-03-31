package com.kruosant.bookwalker.dtos.author;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorBasicInfoDto {
  private Long id;
  @Schema(description = "Author's name", example = "Jennifer", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  @Schema(description = "Author's middle name", example = " ", requiredMode = Schema.RequiredMode.REQUIRED)
  private String middleName;
  @Schema(description = "Author's surname", example = "Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  private String surname;
  @Schema(description = "Author's biography", example = "Author bio for Jennifer Garcia", requiredMode = Schema.RequiredMode.REQUIRED)
  private String bio;
}

