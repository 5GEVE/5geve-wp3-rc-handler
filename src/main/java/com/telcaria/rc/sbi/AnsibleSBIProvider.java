package com.telcaria.rc.sbi;

import com.telcaria.rc.core.events.AnsibleEventResponse;
import com.telcaria.rc.core.events.ApplicationEventResponse;
import com.telcaria.rc.core.events.ExecutionEventResponse;
import com.telcaria.rc.core.events.InfrastructureEventResponse;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("ANSIBLE")
@Slf4j
public class AnsibleSBIProvider implements SBIProvider{


  @Value("${known_hosts.path}")
  private String knownHostsPath = ".ssh/known_hosts";

  @Value("${ansible.playbooks.install_filebeat}")
  private String installFilebeatPlaybook = "5geve-rc/install_filebeat/ansible/install_filebeat.yml";

  @Value("${ansible.playbooks.execute_command}")
  private String executeCommandPlaybook = "5geve-rc/execute_command/ansible/execute_command.yml";

  @Value("${ansible.playbooks.execute_command_windows}")
  private String executeCommandWindowsPlaybook = "5geve-rc/execute_command_windows/ansible/execute_command.yml";

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public AnsibleSBIProvider(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  @Async
  public void start(ExecutionWrapper execution) {

    ExecutionEventResponse executionEventResponse = new ExecutionEventResponse();
    executionEventResponse.setSuccessful(true);
    executionEventResponse.setId(execution.getExecutionId());
    executionEventResponse.setMethod("START");
    int i = 0;
    for (String script : execution.getExecScript().split(";")) {
      executionEventResponse = (ExecutionEventResponse) launchAnsiblePlaybook(executionEventResponse, script, i++);
    }
    applicationEventPublisher.publishEvent(executionEventResponse);
  }

  @Override
  @Async
  public void abort(ExecutionWrapper execution) {
    // TODO missing implementation
  }

  @Override
  @Async
  public void start(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration) {

    ApplicationEventResponse applicationEventResponse = new ApplicationEventResponse();
    applicationEventResponse.setSuccessful(true);
    applicationEventResponse.setId(applicationDay2Configuration.getConfigId());
    applicationEventResponse.setMethod("START");
    int i = 0;
    for (String script : applicationDay2Configuration.getConfigurationScript().split(";")) {
      applicationEventResponse = (ApplicationEventResponse) launchAnsiblePlaybook(applicationEventResponse, script, i++);
    }

    applicationEventPublisher.publishEvent(applicationEventResponse);
  }

  @Override
  @Async
  public void stop(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration) {

    ApplicationEventResponse applicationEventResponse = new ApplicationEventResponse();
    applicationEventResponse.setSuccessful(true);
    applicationEventResponse.setId(applicationDay2Configuration.getConfigId());
    applicationEventResponse.setMethod("RESET");
    int i = 0;
    for (String script : applicationDay2Configuration.getResetConfigScript().split(";")) {
      applicationEventResponse = (ApplicationEventResponse) launchAnsiblePlaybook(applicationEventResponse, script, i++);
    }

    applicationEventPublisher.publishEvent(applicationEventResponse);
  }

  @Override
  @Async
  public void abort(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration) {
    // TODO missing implementation
  }

  @Override
  @Async
  public void start(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration) {

    InfrastructureEventResponse infrastructureEventResponse = new InfrastructureEventResponse();
    infrastructureEventResponse.setSuccessful(true);
    infrastructureEventResponse.setId(infrastructureDay2Configuration.getConfigId());
    infrastructureEventResponse.setMethod("START");
    int i = 0;
    for (String script : infrastructureDay2Configuration.getConfigurationScripts().split(";")) {
      infrastructureEventResponse = (InfrastructureEventResponse)launchAnsiblePlaybook(infrastructureEventResponse, script, i++);
    }
    applicationEventPublisher.publishEvent(infrastructureEventResponse);
  }

  @Override
  @Async
  public void stop(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration) {

    InfrastructureEventResponse infrastructureEventResponse = new InfrastructureEventResponse();
    infrastructureEventResponse.setSuccessful(true);
    infrastructureEventResponse.setId(infrastructureDay2Configuration.getConfigId());
    infrastructureEventResponse.setMethod("RESET");
    int i = 0;
    for (String script : infrastructureDay2Configuration.getStopConfigScripts().split(";")) {
      infrastructureEventResponse = (InfrastructureEventResponse) launchAnsiblePlaybook(infrastructureEventResponse, script, i++);
    }
    applicationEventPublisher.publishEvent(infrastructureEventResponse);
  }

  private AnsibleEventResponse launchAnsiblePlaybook(AnsibleEventResponse ansibleEventResponse,
      String script, int order) {
    if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
      AnsibleCommand ansibleCommand = parseScript(script);
      AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
      String ipAddress = "nil";
      if (ansibleCommand != null) {
        ansibleCommand.setId(ansibleEventResponse.getId()+"-"+order); // use the <config/executionId>-<number> format
        if (ansibleCommand instanceof InstallFilebeatWrapper) {
          script = ansibleCommand.generateAnsibleCommand("/usr/bin/rc/"+installFilebeatPlaybook);
          ipAddress = ((InstallFilebeatWrapper) ansibleCommand).getHostIpAddress();
        } else if (ansibleCommand instanceof SleepWrapper) {
          script = ansibleCommand.generateAnsibleCommand(null);
        } else if (ansibleCommand instanceof ExecuteCommandWrapper) {
          script = ansibleCommand.generateAnsibleCommand("/usr/bin/rc/"+executeCommandPlaybook);
          ipAddress = ((ExecuteCommandWrapper) ansibleCommand).getHostIpAddress();
        } else if (ansibleCommand instanceof ExecuteCommandWindowsWrapper) {
          script = ansibleCommand.generateAnsibleCommand("/usr/bin/rc/" + executeCommandWindowsPlaybook);
          ipAddress = ((ExecuteCommandWindowsWrapper) ansibleCommand).getHostIpAddress();
        }
      } else { // parsing error
        log.error(String.format("parsing error in script: %s", script));
        ansibleEventResponse.setSuccessful(false);
        return ansibleEventResponse;
      }

      ansibleGenericParams.setScript(script);

      if (!executeAnsibleGenericTemplate(ansibleGenericParams, ipAddress)) {
        ansibleEventResponse.setSuccessful(false);
        log.error(String.format("Error executing: %s",script));
      }
    }
    return ansibleEventResponse;
  }

  private boolean executeAnsibleGenericTemplate(AnsibleGenericParams ansibleGenericParams, String ipAddress) {
    // Make sure you have this repository in your $HOME:
    // https://github.com/5GEVE/5geve-rc
    boolean success = false;
    String path = System.getProperty("user.dir"); // use the path where the jar is executing

    int exitVal = -1;

    log.info("Command to be executed: {}", ansibleGenericParams.getScript());
    ProcessBuilder hostProcessBuilder = new ProcessBuilder("bash", "-c", ansibleGenericParams.getScript());
    hostProcessBuilder.directory(new File(path));

    try {
      Process process = hostProcessBuilder.start();

      StringBuilder output = new StringBuilder();
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String s = null;
      while ((s = stdInput.readLine()) != null) {
        output.append(s).append("\n");
      }
      exitVal = process.waitFor();
      if (exitVal == 0) {
        log.info("Success!");
        log.info(String.valueOf(output));
        success = true;
      } else {
        log.warn("Failure!");
        log.warn(String.valueOf(output));
        success = false;
      }

      if (!ipAddress.equals("nil")) {
        // Remove host from SSH keygen
        ProcessBuilder sshProcessBuilder = new ProcessBuilder("bash", "-c", "ssh-keygen -f \""
                + System.getProperty("user.home") + "/" + knownHostsPath + "\" -R " + ipAddress);
        Process sshProcess = sshProcessBuilder.start();

        StringBuilder sshOutput = new StringBuilder();
        BufferedReader sshInput = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()));
        String sshS = null;
        while ((sshS = sshInput.readLine()) != null) {
          sshOutput.append(sshS).append("\n");
        }
        int sshExitVal = sshProcess.waitFor();
        if (sshExitVal == 0) {
          log.info("SSH Keygen clean success!");
          log.info(String.valueOf(sshOutput));
        } else {
          log.warn("SSH Keygen clean failure!");
          log.warn(String.valueOf(sshOutput));
        }
      }
      else {
        // Clean known_hosts file.
        File file = new File(System.getProperty("user.home") + "/" + knownHostsPath);
        if (file.delete()) {
          log.info("known_hosts file has been cleaned");
        } else {
          log.warn("known_hosts file has not been cleaned");
        }
      }

    } catch (IOException | InterruptedException e) {
      log.error(e.toString());
    }
    return success;
  }

  @Override
  public AnsibleCommand parseScript(String string) {
    // Use https://regex101.com/ to validate and debug
    // INSTALL_FILEBEAT ((?:[0-9]{1,3}\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^;]+)
    final String installFilebeatRegex = "INSTALL_FILEBEAT ((?:[0-9]{1,3}\\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^ ]+) ([^;]+)";
    // SLEEP ([.?\d]+)
    final String sleepRegex = "SLEEP ([.?\\d]+)";
    // EXECUTE_COMMAND ((?:[0-9]{1,3}\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([]+)
    final String executeCommandRegex = "EXECUTE_COMMAND ((?:[0-9]{1,3}\\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([^;]+)";
    // EXECUTE_COMMAND_WINDOWS ((?:[0-9]{1,3}\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([]+)
    final String executeCommandWindowsRegex = "EXECUTE_COMMAND_WINDOWS ((?:[0-9]{1,3}\\.){3}[0-9]{1,3}) ([^:]+):([^ ]+) ([^;]+)";

    Pattern pattern = Pattern.compile(installFilebeatRegex, Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(string);
    if (matcher.find()) { // is it an INSTALL_FILEBEAT
      InstallFilebeatWrapper installFilebeatWrapper = new InstallFilebeatWrapper();
      installFilebeatWrapper.setHostIpAddress(matcher.group(1));
      installFilebeatWrapper.setUsername(matcher.group(2));
      installFilebeatWrapper.setPassword(matcher.group(3));
      installFilebeatWrapper.setMetric_id(matcher.group(4));
      installFilebeatWrapper.setTopic_name(matcher.group(5));
      installFilebeatWrapper.setBroker_ip_address(matcher.group(6));
      installFilebeatWrapper.setUnit(matcher.group(7));
      installFilebeatWrapper.setInterval(matcher.group(8));
      installFilebeatWrapper.setDevice_id(matcher.group(9));
      installFilebeatWrapper.setMonitored_file_path(matcher.group(10));
      return installFilebeatWrapper;
    } else {
      pattern = Pattern.compile(sleepRegex, Pattern.MULTILINE);
      matcher = pattern.matcher(string);
      if (matcher.find()) { // is it an SLEEP
        SleepWrapper sleepWrapper = new SleepWrapper();
        sleepWrapper.setSleepTime(matcher.group(1));
        return sleepWrapper;
      } else {
        pattern = Pattern.compile(executeCommandRegex, Pattern.MULTILINE);
        matcher = pattern.matcher(string);
        if (matcher.find()) { // is it an EXECUTE_COMMAND
          ExecuteCommandWrapper executeCommandWrapper = new ExecuteCommandWrapper();
          executeCommandWrapper.setHostIpAddress(matcher.group(1));
          executeCommandWrapper.setUsername(matcher.group(2));
          executeCommandWrapper.setPassword(matcher.group(3));
          executeCommandWrapper.setScript(matcher.group(4));
          return executeCommandWrapper;
        } else {
          pattern = Pattern.compile(executeCommandWindowsRegex, Pattern.MULTILINE);
          matcher = pattern.matcher(string);
          if (matcher.find()) { // is it an EXECUTE_COMMAND_WINDOWS
            ExecuteCommandWindowsWrapper executeCommandWindowsWrapper = new ExecuteCommandWindowsWrapper();
            executeCommandWindowsWrapper.setHostIpAddress(matcher.group(1));
            executeCommandWindowsWrapper.setUsername(matcher.group(2));
            executeCommandWindowsWrapper.setPassword(matcher.group(3));
            executeCommandWindowsWrapper.setScript(matcher.group(4));
            return executeCommandWindowsWrapper;
          }
        }
      }
    }
    return null;
  }
}
