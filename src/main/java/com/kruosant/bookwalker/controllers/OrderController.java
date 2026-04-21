package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.domains.AsyncTask;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.services.AsyncOrderService;
import com.kruosant.bookwalker.services.OrderService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Orders", description = "Order management endpoints")
@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

  private final OrderService service;
  private final AsyncOrderService asyncOrderService;

  @Operation(
      summary = "Get all orders",
      description = "Returns a list of all orders",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Success",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @ArraySchema(schema = @Schema(implementation = OrderFullDto.class))
              )
          )
      }
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<OrderFullDto> getAll() {
    return service.getAll();
  }

  @Operation(
      summary = "Search orders by author surname",
      description = "Returns a paginated list of orders that contain books by the specified author",
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
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<OrderFullDto> search(
      @Parameter(description = "Author surname", required = true, example = "Tolkien")
      @RequestParam(name = "authorSurname") String authorSurname,
      @ParameterObject
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable p) {
    return service.getOrdersWithBooksOf(authorSurname, p);
  }

  @Operation(
      summary = "Get order by ID",
      description = "Returns the order with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Order found",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = OrderFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Order not found"
          )
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public OrderFullDto getById(
      @Parameter(description = "Order ID", example = "1", required = true)
      @PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(
      summary = "Create a new order",
      description = "Creates a new order from the provided data",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Order data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = OrderCreateDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Order created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = OrderFullDto.class)
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
  public OrderFullDto create(
      @Parameter(description = "New order data", required = true)
      @Valid @RequestBody OrderCreateDto dto) {
    return service.create(dto);
  }

  @Operation(
      summary = "Create orders in bulk with a transaction",
      description = "Creates multiple orders atomically. If one item is invalid, none of the orders are saved.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Array of orders to create",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = OrderCreateDto.class))
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Orders created successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @ArraySchema(schema = @Schema(implementation = OrderFullDto.class))
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
      value = "/bulk",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<OrderFullDto> createBulkTransactional(
      @Parameter(description = "Array of new orders", required = true)
      @Valid @RequestBody List<@Valid OrderCreateDto> dtos) {
    return service.createBulkTransactional(dtos);
  }

  @Operation(
      summary = "Delete order by ID",
      description = "Deletes the order with the given identifier",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "Order deleted successfully"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Order not found"
          )
      }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "Order ID", example = "1", required = true)
      @PathVariable Long id) {
    service.delete(id);
  }

  @Operation(
      summary = "Partially update an order",
      description = "Updates only the fields provided for the order",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Fields to update",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = OrderPatchDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Order updated successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = OrderFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Order not found"
          )
      }
  )
  @PatchMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public OrderFullDto patch(
      @Parameter(description = "Order ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Patch data", required = true)
      @Valid @RequestBody OrderPatchDto dto) {
    return service.update(id, dto);
  }

  @Operation(
      summary = "Replace an order",
      description = "Replaces all fields of the order (ID must match the path)",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Full order data",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = OrderPutDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Order replaced successfully",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = OrderFullDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Order not found"
          )
      }
  )
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public OrderFullDto put(
      @Parameter(description = "Order ID", example = "1", required = true)
      @PathVariable Long id,
      @Parameter(description = "Full order data", required = true)
      @Valid @RequestBody OrderPutDto dto) {
    return service.update(id, dto);
  }




  @ResponseStatus(HttpStatus.ACCEPTED)
  @PostMapping(
      value = "/bulk-async",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public AsyncTask createBulkAsync(
      @Parameter(description = "Array of new orders", required = true)
      @Valid @RequestBody List<@Valid OrderCreateDto> dtos) {
    return asyncOrderService.createBulkAsync(dtos);
  }

  @GetMapping("/bulk-async/{taskId}")
  public ResponseEntity<AsyncTask> getTaskStatus(@PathVariable String taskId) {
    return ResponseEntity.ok(asyncOrderService.getTaskStatus(taskId));
  }

  @GetMapping("/bulk-async/tasks")
  public ResponseEntity<Map<String, AsyncTask>> getAllAsyncTasks() {
    return ResponseEntity.ok(asyncOrderService.getAllTasks());
  }


}
