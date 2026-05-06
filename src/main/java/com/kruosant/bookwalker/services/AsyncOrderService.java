package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.AsyncTask;
import com.kruosant.bookwalker.domains.AsyncTaskStatus;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncOrderService {

  private static final long BULK_ASYNC_DELAY_MS = 20000L;

  private final OrderService orderService;
  private final Map<String, AsyncTask> tasks;
  private final Executor delayedExecutor;

  @Autowired
  public AsyncOrderService(OrderService orderService) {
    this(orderService, new ConcurrentHashMap<>(),
        CompletableFuture.delayedExecutor(BULK_ASYNC_DELAY_MS, TimeUnit.MILLISECONDS));
  }

  AsyncOrderService(OrderService orderService, Map<String, AsyncTask> tasks, Executor delayedExecutor) {
    this.orderService = orderService;
    this.tasks = tasks;
    this.delayedExecutor = delayedExecutor;
  }

  public AsyncTask createBulkAsync(List<OrderCreateDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
      throw new BadRequestException();
    }

    String taskId = UUID.randomUUID().toString();
    AsyncTask task = AsyncTask.builder()
        .taskId(taskId)
        .status(AsyncTaskStatus.PENDING)
        .startTime(LocalDateTime.now())
        .build();

    tasks.put(taskId, task);
    List<OrderCreateDto> payload = new ArrayList<>(dtos);
    processBulkCreation(taskId, payload);
    return task;
  }

  public AsyncTask getTaskStatus(String taskId) {
    AsyncTask task = tasks.get(taskId);
    if (task == null) {
      throw new ResourceNotFoundException();
    }
    return task;
  }

  public Map<String, AsyncTask> getAllTasks() {
    return Map.copyOf(tasks);
  }

  private void processBulkCreation(String taskId, List<OrderCreateDto> dtos) {
    AsyncTask task = tasks.get(taskId);
    if (task == null) {
      return;
    }

    task.setStatus(AsyncTaskStatus.IN_PROGRESS);

    CompletableFuture
        .supplyAsync(() -> orderService.createBulkTransactional(dtos).size(), delayedExecutor)
        .thenAccept(createdOrders -> completeTask(task, createdOrders))
        .exceptionally(ex -> {
          failTask(task, ex);
          return null;
        });
  }

  private void completeTask(AsyncTask task, int createdOrders) {
    task.setStatus(AsyncTaskStatus.COMPLETED);
    task.setResult("Created " + createdOrders + " orders");
    task.setEndTime(LocalDateTime.now());
  }

  private void failTask(AsyncTask task, Throwable ex) {
    task.setStatus(AsyncTaskStatus.FAILED);
    task.setResult(resolveErrorMessage(ex));
    task.setEndTime(LocalDateTime.now());
  }

  private String resolveErrorMessage(Throwable ex) {
    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
    if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
      return cause.getMessage();
    }
    return "Bulk order creation failed";
  }
}
