package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.dtos.counter.RaceDemoDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterService {
  private Long counter = 0L;
  private final AtomicLong atomicCounter = new AtomicLong(0);
  private static final Integer POOL_SIZE = 80;
  private static final Integer THREAD_INCREMENT = 300;

  public void incrementSafe() {
    atomicCounter.incrementAndGet();
  }

  public void increment() {
    counter++;
  }

  public RaceDemoDTO runDemo() {

    Runnable syncTask = () -> {
      for (long j = 0L; j < THREAD_INCREMENT; j++) {
        incrementSafe();
      }
    };

    Runnable task = () -> {
      for (long j = 0L; j < THREAD_INCREMENT; j++) {
        increment();
      }
    };

    try (ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE)) {
      counter = 0L;
      atomicCounter.getAndSet(0);
      for (long i = 0L; i < POOL_SIZE; i++) {
        pool.submit(task);
        pool.submit(syncTask);
      }
    }
    return new RaceDemoDTO(POOL_SIZE.longValue() * THREAD_INCREMENT, counter, atomicCounter.longValue());
  }

}
