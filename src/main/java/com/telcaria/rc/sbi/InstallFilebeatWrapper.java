package com.telcaria.rc.sbi;

import lombok.Data;

@Data
public class InstallFilebeatWrapper extends AnsibleCommand {

  private String hostIpAddress;
  private String username;
  private String password;
  private String metric_id;
  private String topic_name;
  private String site;
  private String unit;
  private String interval;
  private String device_id;
  private String monitored_file_path;
  private String broker_ip_address;


  public String generateAnsibleCommand(String playbookPath) {
    String ansibleParams = String.format("metric_id=\"%s\" "
            + "topic_name=\"%s\" "
            + "broker_ip_address=\"%s\" "
            + "unit=\"%s\" "
            + "interval=\"%s\" "
            + "device_id=\"%s\" "
            + "monitored_file_path=\"%s\"",
        metric_id, topic_name, broker_ip_address, unit, interval, device_id, monitored_file_path);


    return String.format("[ ! -e /tmp/%1$s ] || rm /tmp/%1$s && touch /tmp/%1$s && echo \"server ansible_host=%2$s "
      +"ansible_user=%3$s ansible_ssh_pass=%4$s ansible_become_pass=%4$s\" | "
      +"tee -a /tmp/%1$s && export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i /tmp/%1$s %5$s -e '%6$s';\n", id,  hostIpAddress, username, password, playbookPath, ansibleParams);

  }
}
