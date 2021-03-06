package com.telcaria.rc.sbi;

import lombok.Data;

@Data
public class ExecuteCommandWindowsWrapper extends AnsibleCommand {

  private String hostIpAddress;
  private String username;
  private String password;
  private String script;

  public String generateAnsibleCommand(String playbookPath) {
    String ansibleParams =  String.format("script=\"%s\"", script.replace("\"",""));

    return String.format("[ ! -e /tmp/%1$s ] || rm /tmp/%1$s && touch /tmp/%1$s && echo \"server ansible_host=%2$s "
        +"ansible_user=%3$s ansible_password=%4$s ansible_connection=winrm ansible_winrm_server_cert_validation=ignore\" | "
        +"tee -a /tmp/%1$s && export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i /tmp/%1$s %5$s -e '%6$s';\n", id,  hostIpAddress, username, password, playbookPath, ansibleParams);

  }
}
