package com.telcaria.rc.core.events;

import lombok.Data;

@Data
public class ApplicationEventResponse {

  private boolean successful;
  private String configId;
  private String method;

  public boolean isSuccessful() {
    return successful;
  }

  public String getConfigId() {
    return configId;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
