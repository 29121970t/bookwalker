package com.kruosant.bookwalker.domains;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsyncTask {
  private String taskId;
  private AsyncTaskStatus status;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String result;
}
