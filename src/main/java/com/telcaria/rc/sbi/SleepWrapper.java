package com.telcaria.rc.sbi;

import lombok.Data;

@Data
public class SleepWrapper extends AnsibleCommand {

  private String sleepTime;

  public String generateAnsibleCommand(String playbookPath) {
    return String.format("sleep %s", sleepTime);
  }
}
