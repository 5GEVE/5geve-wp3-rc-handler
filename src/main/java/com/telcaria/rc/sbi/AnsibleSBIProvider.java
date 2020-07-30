package com.telcaria.rc.sbi;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("ANSIBLE")
@Slf4j
public class AnsibleSBIProvider implements SBIProvider{

  @Value("${ansible.user}")
  private String user = "telcaria";

  @Value("${ansible.path}")
  private String ansiblePath = "/home/%s/5g-eve/github/5geve-rc/execute_script/ansible";

  @Value("${known_hosts.path}")
  private String knownHostsPath = "/home/%s/.ssh/known_hosts";

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
    executionEventResponse.setExecutionId(execution.getExecutionId());
    executionEventResponse.setMethod("START");

    for (String script : execution.getExecScript().split(";")) {
      if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
        AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
        ansibleGenericParams.setScript(script);

        if (executeAnsibleGenericTemplate(ansibleGenericParams)) {
          if (executionEventResponse.isSuccessful()) {
            executionEventResponse.setSuccessful(true);
          }
        } else {
          executionEventResponse.setSuccessful(false);
        }
      }
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
    applicationEventResponse.setConfigId(applicationDay2Configuration.getConfigId());
    applicationEventResponse.setMethod("START");

    for (String script : applicationDay2Configuration.getConfigurationScript().split(";")) {
      if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
        AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
        ansibleGenericParams.setScript(script);

        if (executeAnsibleGenericTemplate(ansibleGenericParams)) {
          if (applicationEventResponse.isSuccessful()) {
            applicationEventResponse.setSuccessful(true);
          }
        } else {
          applicationEventResponse.setSuccessful(false);
        }
      }
    }

    applicationEventPublisher.publishEvent(applicationEventResponse);
  }

  @Override
  @Async
  public void stop(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration) {

    ApplicationEventResponse applicationEventResponse = new ApplicationEventResponse();
    applicationEventResponse.setSuccessful(true);
    applicationEventResponse.setConfigId(applicationDay2Configuration.getConfigId());
    applicationEventResponse.setMethod("RESET");

    for (String script : applicationDay2Configuration.getResetConfigScript().split(";")) {
      if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
        AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
        ansibleGenericParams.setScript(script);

        if (executeAnsibleGenericTemplate(ansibleGenericParams)) {
          if (applicationEventResponse.isSuccessful()) {
            applicationEventResponse.setSuccessful(true);
          }
        } else {
          applicationEventResponse.setSuccessful(false);
        }
      }
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
    infrastructureEventResponse.setConfigId(infrastructureDay2Configuration.getConfigId());
    infrastructureEventResponse.setMethod("START");

    for (String script : infrastructureDay2Configuration.getConfigurationScripts().split(";")) {
      if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
        AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
        ansibleGenericParams.setScript(script);

        if (executeAnsibleGenericTemplate(ansibleGenericParams)) {
          if (infrastructureEventResponse.isSuccessful()) {
            infrastructureEventResponse.setSuccessful(true);
          }
        } else {
          infrastructureEventResponse.setSuccessful(false);
        }
      }
    }

    applicationEventPublisher.publishEvent(infrastructureEventResponse);
  }

  @Override
  @Async
  public void stop(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration) {

    InfrastructureEventResponse infrastructureEventResponse = new InfrastructureEventResponse();
    infrastructureEventResponse.setSuccessful(true);
    infrastructureEventResponse.setConfigId(infrastructureDay2Configuration.getConfigId());
    infrastructureEventResponse.setMethod("RESET");

    for (String script : infrastructureDay2Configuration.getStopConfigScripts().split(";")) {
      if (!script.isEmpty() && !script.replaceAll("\\s+","").equals("")) {
        AnsibleGenericParams ansibleGenericParams = new AnsibleGenericParams();
        ansibleGenericParams.setScript(script);

        if (executeAnsibleGenericTemplate(ansibleGenericParams)) {
          if (infrastructureEventResponse.isSuccessful()) {
            infrastructureEventResponse.setSuccessful(true);
          }
        } else {
          infrastructureEventResponse.setSuccessful(false);
        }
      }
    }

    applicationEventPublisher.publishEvent(infrastructureEventResponse);
  }

  private boolean executeAnsibleGenericTemplate(AnsibleGenericParams ansibleGenericParams) {
    // Make sure you have this repository in your $HOME:
    // https://github.com/5GEVE/5geve-rc
    boolean success = false;
    String path = String.format(ansiblePath, user);

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
        output.append(s + "\n");
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

      // Clean known_hosts file.
      File file = new File(String.format(knownHostsPath, user));
      if (file.delete()) {
        log.info("known_hosts file has been cleaned");
      } else {
        log.warn("known_hosts file has not been cleaned");
      }

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return success;
  }
}
