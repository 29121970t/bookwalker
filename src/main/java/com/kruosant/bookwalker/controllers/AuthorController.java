package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.services.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authors", description = "Author management endpoints")
@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public final class AuthorController {

  private final AuthorService service;

  @Operation(
      summary = "Get all authors",
      description = "Returns a list of all authors",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Success",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Page.class)
              )
          )
      }
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<AuthorFullDto> getAll(
      @ParameterObject
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return service.getAllFullDto(pageable);
  }

  @Operation(
      summary = "Create a new author",
      description = "Creates a new author from the provided data",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Author data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = AuthorCreateDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Author created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = AuthorFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          )
      }
  )
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public AuthorFullDto add(
      @Parameter(description = "New author data", required = true)
      @Valid @RequestBody AuthorCreateDto authorDto) {
    return service.create(authorDto);
  }

  @Operation(
      summary = "Get author by ID",
      description = "Returns the author with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Author found",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = AuthorFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Author not found"
          )
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public AuthorFullDto getById(
      @Parameter(description = "Author ID", example = "1", required = true)
      @PathVariable Long id) {
    return service.getFullDtoById(id);
  }

  @Operation(
      summary = "Delete author by ID",
      description = "Deletes the author with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Author deleted successfully"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Author not found"
          )
      }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(
      @Parameter(description = "Author ID", example = "1", required = true)
      @PathVariable Long id) {
    service.delete(id);
  }

  @Operation(
      summary = "Partially update an author",
      description = "Updates only the fields provided for the author",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = AuthorPatchDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Author updated successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = AuthorFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Author not found"
          )
      }
  )
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public AuthorFullDto patch(
      @Parameter(description = "Author ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Patch data", required = true)
      @Valid @RequestBody AuthorPatchDto dto) {
    return service.patch(id, dto);
  }

  @Operation(
      summary = "Replace an author",
      description = "Replaces all fields of the author (ID must match the path)",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Full author data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = AuthorPutDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Author replaced successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = AuthorFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Author not found"
          )
      }
  )
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public AuthorFullDto put(
      @Parameter(description = "Author ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Full author data", required = true)
      @Valid @RequestBody AuthorPutDto dto) {
    return service.put(id, dto);
  }
}
