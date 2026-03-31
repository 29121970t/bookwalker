package com.kruosant.bookwalker.dtos.author;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorPatchDto {
  @Nullable
  @Schema(description = "Author's new name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String name;
  @Nullable
  @Schema(description = "Author's new middleName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String middleName;
  @Nullable
  @Schema(description = "Author's new surname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String surname;
  @Nullable
  @Schema(description = "Author's new bio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String bio;
  @Nullable
  @Schema(description = "Author's list of book ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Set<Long> books;
}