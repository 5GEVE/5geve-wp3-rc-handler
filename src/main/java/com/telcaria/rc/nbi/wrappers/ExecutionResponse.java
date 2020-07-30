package com.telcaria.rc.nbi.wrappers;

import com.telcaria.rc.nbi.enums.ExecutionStatus;
import lombok.Data;

@Data
public class ExecutionResponse {

  private ExecutionStatus status;
  private String executionId;

}
