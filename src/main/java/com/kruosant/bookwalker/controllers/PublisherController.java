package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.services.PublisherService;
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

@Tag(name = "Publishers", description = "Publisher management endpoints")
@RestController
@RequestMapping("/publishers")
@AllArgsConstructor
public class PublisherController {

  private final PublisherService service;

  @Operation(
      summary = "Get all publishers",
      description = "Returns a list of all publishers",
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
  public Page<PublisherFullDto> getAll(
      @ParameterObject
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return service.getAll(pageable);
  }

  @Operation(
      summary = "Create a new publisher",
      description = "Creates a new publisher from the provided data",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Publisher data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PublisherCreateDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Publisher created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = PublisherFullDto.class)
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
  public PublisherFullDto add(
      @Parameter(description = "New publisher data", required = true)
      @Valid @RequestBody PublisherCreateDto bookDto) {
    return service.create(bookDto);
  }

  @Operation(
      summary = "Get publisher by ID",
      description = "Returns the publisher with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Publisher found",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = PublisherFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Publisher not found"
          )
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PublisherFullDto getById(
      @Parameter(description = "Publisher ID", example = "1", required = true)
      @PathVariable Long id) {
    return service.getAuthorById(id);
  }

  @Operation(
      summary = "Delete publisher by ID",
      description = "Deletes the publisher with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Publisher deleted successfully"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Publisher not found"
          )
      }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(
      @Parameter(description = "Publisher ID", example = "1", required = true)
      @PathVariable Long id) {
    service.delete(id);
  }

  @Operation(
      summary = "Partially update a publisher",
      description = "Updates only the fields provided for the publisher",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PublisherPatchDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Publisher updated successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = PublisherFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Publisher not found"
          )
      }
  )
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public PublisherFullDto patch(
      @Parameter(description = "Publisher ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Patch data", required = true)
      @Valid @RequestBody PublisherPatchDto dto) {
    return service.update(id, dto);
  }

  @Operation(
      summary = "Replace a publisher",
      description = "Replaces all fields of the publisher (ID must match the path)",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Full publisher data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PublisherPutDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Publisher replaced successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = PublisherFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Publisher not found"
          )
      }
  )
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public PublisherFullDto put(
      @Parameter(description = "Publisher ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Full publisher data", required = true)
      @Valid @RequestBody PublisherPutDto dto) {
    return service.update(id, dto);
  }
}
