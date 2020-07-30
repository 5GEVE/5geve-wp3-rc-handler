package com.telcaria.rc.core.events;

import lombok.Data;

@Data
public class InfrastructureEventResponse extends AnsibleEventResponse{

  private String method;

}
