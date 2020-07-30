package com.telcaria.rc.core.events;

import lombok.Data;

@Data
public class ExecutionEventResponse extends AnsibleEventResponse{

  private String method;

}
