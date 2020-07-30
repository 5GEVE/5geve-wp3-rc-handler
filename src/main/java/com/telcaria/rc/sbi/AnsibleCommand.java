package com.telcaria.rc.sbi;

import lombok.Data;

@Data
public abstract class AnsibleCommand {

  String id; // <config/executionId>-<number>

  public abstract String generateAnsibleCommand(String playbookPath);

}
