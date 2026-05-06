package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.dtos.counter.RaceDemoDTO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterServiceTest {

  @Test
  void incrementShouldIncreaseUnsafeCounter() throws ReflectiveOperationException {
    CounterService service = new CounterService();

    service.increment();
    service.increment();

    Field counterField = CounterService.class.getDeclaredField("counter");
    counterField.setAccessible(true);

    assertEquals(2L, counterField.get(service));
  }

  @Test
  void incrementSafeShouldIncreaseAtomicCounter() throws ReflectiveOperationException {
    CounterService service = new CounterService();

    service.incrementSafe();
    service.incrementSafe();

    Field atomicCounterField = CounterService.class.getDeclaredField("atomicCounter");
    atomicCounterField.setAccessible(true);

    Object atomicCounter = atomicCounterField.get(service);
    long currentValue = ((java.util.concurrent.atomic.AtomicLong) atomicCounter).get();

    assertEquals(2L, currentValue);
  }

  @Test
  void runDemoShouldReturnExpectedSafeResult() {
    CounterService service = new CounterService();

    RaceDemoDTO result = service.runDemo();

    assertEquals(24_000L, result.expected());
    assertEquals(result.expected(), result.safeResult());
    assertTrue(result.unsafeResult() >= 0L);
    assertTrue(result.unsafeResult() <= result.expected());
  }

  @Test
  void runDemoShouldResetCountersBetweenRuns() {
    CounterService service = new CounterService();

    RaceDemoDTO firstRun = service.runDemo();
    RaceDemoDTO secondRun = service.runDemo();

    assertEquals(24_000L, firstRun.expected());
    assertEquals(24_000L, secondRun.expected());
    assertEquals(firstRun.expected(), firstRun.safeResult());
    assertEquals(secondRun.expected(), secondRun.safeResult());
    assertTrue(secondRun.unsafeResult() <= secondRun.expected());
  }
}
