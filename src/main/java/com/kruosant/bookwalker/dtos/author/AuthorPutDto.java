package com.kruosant.bookwalker.dtos.author;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorPutDto {
  @NotNull
  @Schema(description = "Author's new name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String name;
  @NotNull
  @Schema(description = "Author's new middleName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String middleName;
  @NotNull
  @Schema(description = "Author's new surname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String surname;
  @NotNull
  @Schema(description = "Author's new bio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String bio;
  @NotNull
  @Schema(description = "Author's list of book ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Set<Long> books;
}