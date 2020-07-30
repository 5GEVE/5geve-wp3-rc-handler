package com.telcaria.rc.core.events;

import lombok.Data;

@Data
public class ApplicationEventResponse extends AnsibleEventResponse{

  private String method;

}
