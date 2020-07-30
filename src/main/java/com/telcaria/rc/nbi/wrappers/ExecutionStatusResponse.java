package com.telcaria.rc.nbi.wrappers;

import com.telcaria.rc.nbi.enums.ExecutionStatus;
import lombok.Data;

@Data
public class ExecutionStatusResponse {

  private ExecutionStatus status;

}
