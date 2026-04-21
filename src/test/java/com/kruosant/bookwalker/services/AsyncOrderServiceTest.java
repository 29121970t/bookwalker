package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.AsyncTask;
import com.kruosant.bookwalker.domains.AsyncTaskStatus;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncOrderServiceTest {

  private static final Executor DIRECT_EXECUTOR = Runnable::run;

  @Mock
  private OrderService orderService;

  @Test
  void createBulkAsyncShouldRejectNullPayload() throws Exception {
    AsyncOrderService service = serviceWith(new ConcurrentHashMap<>());

    assertThrows(BadRequestException.class, () -> service.createBulkAsync(null));
  }

  @Test
  void createBulkAsyncShouldRejectEmptyPayload() throws Exception {
    AsyncOrderService service = serviceWith(new ConcurrentHashMap<>());
    List<OrderCreateDto> emptyPayload = List.of();

    assertThrows(BadRequestException.class, () -> service.createBulkAsync(emptyPayload));
  }

  @Test
  void createBulkAsyncShouldCompleteTaskWhenOrdersCreated() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncOrderService service = serviceWith(tasks);
    List<OrderCreateDto> payload = List.of(new OrderCreateDto(), new OrderCreateDto());

    when(orderService.createBulkTransactional(payload)).thenReturn(List.of());

    AsyncTask task = service.createBulkAsync(payload);

    assertNotNull(task.getTaskId());
    assertEquals(AsyncTaskStatus.COMPLETED, task.getStatus());
    assertEquals("Created 0 orders", task.getResult());
    assertNotNull(task.getStartTime());
    assertNotNull(task.getEndTime());
    assertSame(task, tasks.get(task.getTaskId()));
    verify(orderService).createBulkTransactional(payload);
  }

  @Test
  void createBulkAsyncShouldFailTaskWithCauseMessage() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncOrderService service = serviceWith(tasks);
    List<OrderCreateDto> payload = List.of(new OrderCreateDto());

    when(orderService.createBulkTransactional(payload))
        .thenThrow(new IllegalStateException("Bulk failed"));

    AsyncTask task = service.createBulkAsync(payload);

    assertEquals(AsyncTaskStatus.FAILED, task.getStatus());
    assertEquals("Bulk failed", task.getResult());
    assertNotNull(task.getEndTime());
  }

  @Test
  void createBulkAsyncShouldUseFallbackMessageWhenExceptionMessageBlank() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncOrderService service = serviceWith(tasks);
    List<OrderCreateDto> payload = List.of(new OrderCreateDto());

    when(orderService.createBulkTransactional(payload))
        .thenThrow(new IllegalStateException(" "));

    AsyncTask task = service.createBulkAsync(payload);

    assertEquals(AsyncTaskStatus.FAILED, task.getStatus());
    assertEquals("Bulk order creation failed", task.getResult());
  }

  @Test
  void getTaskStatusShouldReturnExistingTask() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncTask task = AsyncTask.builder().taskId("task-1").status(AsyncTaskStatus.PENDING).build();
    tasks.put("task-1", task);
    AsyncOrderService service = serviceWith(tasks);

    AsyncTask result = service.getTaskStatus("task-1");

    assertSame(task, result);
  }

  @Test
  void getTaskStatusShouldThrowWhenTaskMissing() throws Exception {
    AsyncOrderService service = serviceWith(new ConcurrentHashMap<>());

    assertThrows(ResourceNotFoundException.class, () -> service.getTaskStatus("missing"));
  }

  @Test
  void getAllTasksShouldReturnImmutableSnapshot() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncTask task = AsyncTask.builder().taskId("task-1").status(AsyncTaskStatus.PENDING).build();
    AsyncTask anotherTask = AsyncTask.builder().taskId("task-2").build();
    tasks.put("task-1", task);
    AsyncOrderService service = serviceWith(tasks);

    Map<String, AsyncTask> result = service.getAllTasks();

    assertEquals(1, result.size());
    assertSame(task, result.get("task-1"));
    assertThrows(UnsupportedOperationException.class,
        () -> result.put("task-2", anotherTask));
  }

  private AsyncOrderService serviceWith(Map<String, AsyncTask> tasks) throws Exception {
    AsyncOrderService service = new AsyncOrderService(orderService);
    setField(service, "tasks", tasks);
    setField(service, "delayedExecutor", DIRECT_EXECUTOR);
    return service;
  }

  private static void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
