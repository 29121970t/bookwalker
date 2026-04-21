package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.counter.Counter;
import com.kruosant.bookwalker.dtos.counter.RaceDemoDTO;
import com.kruosant.bookwalker.services.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CounterController", description = "Counter management endpoints")
@RestController
@RequestMapping("/counter")
@AllArgsConstructor
public class CounterController {
  private final CounterService counterService;


  @GetMapping("/raceDemo")
  public RaceDemoDTO demo() {
    return counterService.runDemo();
  }

}