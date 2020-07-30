package com.telcaria.rc.core.events;

import lombok.Data;

@Data
public class ExecutionEventResponse {

  private boolean successful;
  private String executionId;
  private String method;

  public boolean isSuccessful() {
    return successful;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
