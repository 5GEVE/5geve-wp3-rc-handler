package com.telcaria.rc.nbi.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExecutionStatus {
  INIT("INIT"), RUNNING("RUNNING"), COMPLETED("COMPLETED"),
  ABORTING("ABORTING"), ABORTED("ABORTED"), FAILED("FAILED");
  private final String name;

}
