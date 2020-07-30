package com.telcaria.rc.core.events;


import lombok.Data;

@Data
public class AnsibleEventResponse {
  String id;
  boolean successful;
  public boolean isSuccessful() {
    return successful;
  }

}
