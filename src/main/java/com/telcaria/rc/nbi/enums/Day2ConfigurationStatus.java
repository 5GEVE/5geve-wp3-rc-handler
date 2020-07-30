package com.telcaria.rc.nbi.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Day2ConfigurationStatus {
  INIT("INIT"), VALIDATING("VALIDATING"),
  CONFIGURING("CONFIGURING"), COMPLETED("COMPLETED"),
  ABORTING("ABORTING"), ABORTED("ABORTED"),
  STOPPING("STOPPING"), STOPPED("STOPPED"),
  FAILED("FAILED"), CLEANING("CLEANING"), CLEANED("CLEANED");
  private final String name;
}
