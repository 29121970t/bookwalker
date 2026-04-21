package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.services.BookService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "Book management endpoints")
@RestController
@RequestMapping("/books")
@AllArgsConstructor
public final class BookController {

  private final BookService service;

  @Operation(
      summary = "Get all books",
      description = "Returns a list of all books",
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
  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<BookFullDto> getAll(
      @ParameterObject
      @PageableDefault(size = 20, sort = "id") Pageable p) {
    return service.getAll(p);
  }

  @Operation(
      summary = "Create a new book",
      description = "Creates a new book from the provided data",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Book data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = BookCreateDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Book created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = BookFullDto.class)
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
  public BookFullDto add(
      @Parameter(description = "New book data", required = true)
      @Valid @RequestBody BookCreateDto bookDto) {
    return service.create(bookDto);
  }

  @Operation(
      summary = "Get book by ID",
      description = "Returns the book with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Book found",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = BookFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Book not found"
          )
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public BookFullDto getById(
      @Parameter(description = "Book ID", example = "1", required = true)
      @PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(
      summary = "Delete book by ID",
      description = "Deletes the book with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Book deleted successfully"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Book not found"
          )
      }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(
      @Parameter(description = "Book ID", example = "1", required = true)
      @PathVariable Long id) {
    service.deleteById(id);
  }

  @Operation(
      summary = "Partially update a book",
      description = "Updates only the fields provided for the book",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = BookPatchDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Book updated successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = BookFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Book not found"
          )
      }
  )
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public BookFullDto patch(
      @Parameter(description = "Book ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Patch data", required = true)
      @Valid @RequestBody BookPatchDto dto) {
    return service.patch(id, dto);
  }

  @Operation(
      summary = "Replace a book",
      description = "Replaces all fields of the book (ID must match the path)",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Full book data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = BookPutDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Book replaced successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = BookFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Book not found"
          )
      }
  )
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public BookFullDto put(
      @Parameter(description = "Book ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Full book data", required = true)
      @Valid @RequestBody BookPutDto dto) {
    return service.put(id, dto);
  }
}