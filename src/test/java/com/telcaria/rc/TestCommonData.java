package com.telcaria.rc;

public class TestCommonData {

  public static final String IPADDRESS = "192.168.1.1";
  public static final String USERNAME = "john";
  public static final String PASSWORD = "verysecurepassword";
  public static final String CONFIG_START = "ls";
  public static final String CONFIG_RESET = "ls";
  public static final String CONFIG_ABORT = "ls";
  public static final String METRIC_ID = "latency";
  public static final String TOPIC = "uc.4.france_nice.app_metric.expb_metricId1";
  public static final String SITE = "sitetest";
  public static final String UNIT = "s";
  public static final String INTERVAL = "10";
  public static final String DEVICE_ID = "deviceidtest";
  public static final String CONTEXT = "contexttest";
  public static final String MONITORED_PATH = "monitoredpathtrest";


  public static final String IPADDRESS_INFRA = "192.168.1.3";
  public static final String USERNAME_INFRA = "john";
  public static final String PASSWORD_INFRA = "verysecurepassword";
  public static final String CONFIG_START_INFRA = "ls";
  public static final String CONFIG_RESET_INFRA = "ls";
  public static final String METRIC_ID_INFRA = "latency";
  public static final String TOPIC_INFRA = "uc.4.italy_turin.infrastructure_metric.expb_metricId";
  public static final String SITE_INFRA = "ITALY_TURIN";
  public static final String SITE_I_METRIC_TYPE = "CPU_CONSUMPTION";
  public static final String UNIT_INFRA = "s";
  public static final String INTERVAL_INFRA = "10";
  public static final String DEVICE_ID_INFRA = "deviceidtest";
  public static final String CONTEXT_INFRA = "contexttest";
  public static final String MONITORED_PATH_INFRA = "monitoredpathtrest";
  public static final String ITALY_SITE_ID = "1";

  public static final String IPADDRESS_EXEC = "192.168.1.5";
  public static final String USERNAME_EXEC = "john";
  public static final String PASSWORD_EXEC = "verysecurepassword";

  public static final String INSTALL_FILEBEAT_TCB_1 = "INSTALL_FILEBEAT 192.168.20.4 user:pass track_device example.4.spain.application_metric.track_device 10.10.10.10 position 5s vnf-1 /var/log/track_device.log;";
  public static final String SLEEP_TCB_1 = "SLEEP 0.1;";
  public static final String EXECUTE_COMMAND_TCB_PING_1 = "EXECUTE_COMMAND 192.168.1.192 userB:passwordB \"ping -c5 10.9.0.1\";";
  public static final String EXECUTE_COMMAND_TCB_LS_1 = "EXECUTE_COMMAND 127.0.0.1 root:root \"ls /\";";

  public static final String EXEC_SCRIPT = EXECUTE_COMMAND_TCB_LS_1;
  public static final String CONFIG_SCRIPT_INFRA = EXECUTE_COMMAND_TCB_LS_1;
  public static final String CONFIG_STOP_SCRIPT_INFRA = EXECUTE_COMMAND_TCB_LS_1;
  public static final String COMPLETE_CONFIG_SCRIPT_INFRA = EXECUTE_COMMAND_TCB_LS_1;
  public static final String COMPLETE_CONFIG_STOP_SCRIPT_INFRA = EXECUTE_COMMAND_TCB_LS_1;
  public static final String CONFIG_SCRIPT = EXECUTE_COMMAND_TCB_LS_1;
  public static final String RESET_CONFIG_SCRIPT = EXECUTE_COMMAND_TCB_LS_1;

  public static final String INSTALL_FILEBEAT_GENERATED_ANSIBLE_COMMAND_1 = "[ ! -e /tmp/819c5d41-e9f6-4e10-a8b1-4df27a8c031d ] || rm /tmp/819c5d41-e9f6-4e10-a8b1-4df27a8c031d && touch /tmp/819c5d41-e9f6-4e10-a8b1-4df27a8c031d && echo \"server ansible_host=192.168.20.4 ansible_user=user ansible_ssh_pass=pass ansible_become_pass=pass\" | tee -a /tmp/819c5d41-e9f6-4e10-a8b1-4df27a8c031d && export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i /tmp/819c5d41-e9f6-4e10-a8b1-4df27a8c031d \"/Users/aitor/telcaria-code/\"5geve-rc/filebeat_data_shipper_test/ansible -e 'metric_id=\"track_device\" topic_name=\"example.4.spain.application_metric.track_device\" broker_ip_address=\"10.10.10.10\" unit=\"position\" interval=\"5s\" device_id=\"vnf-1\" monitored_file_path=\"/var/log/track_device.log\"';\n";
  public static final String SLEEP_GENERATED_ANSIBLE_COMMAND_1 = "sleep 0.1";
  public static final String EXECUTE_COMMAND_GENERATED_ANSIBLE_COMMAND_1 ="[ ! -e /tmp/8600f80a-b523-4de2-a1ae-2f2deda57746 ] || rm /tmp/8600f80a-b523-4de2-a1ae-2f2deda57746 && touch /tmp/8600f80a-b523-4de2-a1ae-2f2deda57746 && echo \"server ansible_host=192.168.1.192 ansible_user=userB ansible_ssh_pass=passwordB ansible_become_pass=passwordB\" | tee -a /tmp/8600f80a-b523-4de2-a1ae-2f2deda57746 && export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i /tmp/8600f80a-b523-4de2-a1ae-2f2deda57746 \"/Users/aitor/telcaria-code/\"5geve-rc/execute_script/ansible -e 'script=\"ping -c5 10.9.0.1\"';\n";
}
