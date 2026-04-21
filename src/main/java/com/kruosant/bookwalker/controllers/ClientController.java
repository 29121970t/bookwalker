package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name = "Clients", description = "Client management endpoints")
@RestController
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientController {

  private final ClientService service;

  @Operation(
      summary = "Get all clients",
      description = "Returns a list of all clients",
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
  public Page<ClientFullDto> getAll(
      @ParameterObject
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return service.getAll(pageable);
  }

  @Operation(
      summary = "Get client by ID",
      description = "Returns the client with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Client found",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ClientFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Client not found"
          )
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ClientFullDto getById(
      @Parameter(description = "Client ID", example = "1", required = true)
      @PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(
      summary = "Create a new client",
      description = "Creates a new client from the provided data",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Client data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ClientCreateDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Client created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ClientFullDto.class)
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
  public ClientFullDto create(
      @Parameter(description = "New client data", required = true)
      @Valid @RequestBody ClientCreateDto dto) {
    return service.create(dto);
  }

  @Operation(
      summary = "Delete client by ID",
      description = "Deletes the client with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Client deleted successfully"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Client not found"
          )
      }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "Client ID", example = "1", required = true)
      @PathVariable Long id) {
    service.delete(id);
  }

  @Operation(
      summary = "Partially update a client",
      description = "Updates only the fields provided for the client",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ClientPatchDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Client updated successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ClientFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Client not found"
          )
      }
  )
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ClientFullDto patch(
      @Parameter(description = "Client ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Patch data", required = true)
      @Valid @RequestBody ClientPatchDto dto) {
    return service.update(id, dto);
  }

  @Operation(
      summary = "Replace a client",
      description = "Replaces all fields of the client (ID must match the path)",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Full client data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ClientPutDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Client replaced successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ClientFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Client not found"
          )
      }
  )
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ClientFullDto put(
      @Parameter(description = "Client ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Full client data", required = true)
      @Valid @RequestBody ClientPutDto dto) {
    return service.update(id, dto);
  }
}
