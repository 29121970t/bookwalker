package com.kruosant.bookwalker.controllers;

import com.kruosant.bookwalker.dtos.counter.RaceDemoDTO;
import com.kruosant.bookwalker.services.CounterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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