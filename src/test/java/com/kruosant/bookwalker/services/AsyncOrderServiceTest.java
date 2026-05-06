package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.AsyncTask;
import com.kruosant.bookwalker.domains.AsyncTaskStatus;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncOrderServiceTest {
  @Mock
  private OrderService orderService;

  @Test
  void createBulkAsyncRejectsEmptyPayload() {
    AsyncOrderService service = service();

    assertThrows(BadRequestException.class, () -> service.createBulkAsync(List.of()));
  }

  @Test
  void publicConstructorUsesDelayedExecutor() {
    AsyncOrderService service = new AsyncOrderService(orderService);

    assertTrue(service.getAllTasks().isEmpty());
  }

  @Test
  void createBulkAsyncRejectsNullPayload() {
    AsyncOrderService service = service();

    assertThrows(BadRequestException.class, () -> service.createBulkAsync(null));
  }

  @Test
  void createBulkAsyncCompletesTask() {
    AsyncOrderService service = service();
    OrderCreateDto dto = new OrderCreateDto();
    when(orderService.createBulkTransactional(List.of(dto))).thenReturn(List.of(OrderFullDto.builder().build()));

    AsyncTask task = service.createBulkAsync(List.of(dto));

    assertEquals(AsyncTaskStatus.COMPLETED, service.getTaskStatus(task.getTaskId()).getStatus());
    assertEquals("Created 1 orders", task.getResult());
    assertTrue(task.getEndTime() != null);
    assertTrue(service.getAllTasks().containsKey(task.getTaskId()));
  }

  @Test
  void createBulkAsyncFailsTaskWithCauseMessage() {
    AsyncOrderService service = service();
    OrderCreateDto dto = new OrderCreateDto();
    when(orderService.createBulkTransactional(List.of(dto))).thenThrow(new IllegalStateException("database down"));

    AsyncTask task = service.createBulkAsync(List.of(dto));

    assertEquals(AsyncTaskStatus.FAILED, task.getStatus());
    assertEquals("database down", task.getResult());
    assertTrue(task.getEndTime() != null);
  }

  @Test
  void getTaskStatusThrowsWhenMissing() {
    AsyncOrderService service = service();

    assertThrows(ResourceNotFoundException.class, () -> service.getTaskStatus("missing"));
  }

  @Test
  void processBulkCreationReturnsWhenTaskWasRemoved() throws Exception {
    Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();
    AsyncOrderService service = new AsyncOrderService(orderService, tasks, Runnable::run);

    Method method = AsyncOrderService.class.getDeclaredMethod("processBulkCreation", String.class, List.class);
    method.setAccessible(true);
    method.invoke(service, "missing", List.of(new OrderCreateDto()));

    assertTrue(tasks.isEmpty());
  }

  @Test
  void resolveErrorMessageFallsBackWhenMessageBlank() throws Exception {
    AsyncOrderService service = service();

    Method method = AsyncOrderService.class.getDeclaredMethod("resolveErrorMessage", Throwable.class);
    method.setAccessible(true);

    assertEquals("Bulk order creation failed", method.invoke(service, new IllegalStateException("")));
  }

  @Test
  void resolveErrorMessageUsesCauseMessage() throws Exception {
    AsyncOrderService service = service();

    Method method = AsyncOrderService.class.getDeclaredMethod("resolveErrorMessage", Throwable.class);
    method.setAccessible(true);

    assertEquals("cause", method.invoke(service, new InvocationTargetException(new IllegalArgumentException("cause"))));
  }

  @Test
  void resolveErrorMessageFallsBackWhenCauseMessageIsNull() throws Exception {
    AsyncOrderService service = service();

    Method method = AsyncOrderService.class.getDeclaredMethod("resolveErrorMessage", Throwable.class);
    method.setAccessible(true);

    assertEquals("Bulk order creation failed", method.invoke(service, new RuntimeException((String) null)));
  }

  private AsyncOrderService service() {
    return new AsyncOrderService(orderService, new ConcurrentHashMap<>(), Runnable::run);
  }
}
