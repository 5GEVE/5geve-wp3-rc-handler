package com.telcaria.rc.core.service;

import com.telcaria.rc.TestCommonData;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;

import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureMetricWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
class RCServiceImplTest {

  @Autowired
  RCService rcService;

  @Test
  void functionalTest() {
    // Load Application Day2 Configuration Test

    ApplicationDay2ConfigurationWrapper applicationDay2ConfigurationWrapper = new ApplicationDay2ConfigurationWrapper();
    applicationDay2ConfigurationWrapper.setResetConfigScript(TestCommonData.RESET_CONFIG_SCRIPT);
    applicationDay2ConfigurationWrapper.setConfigurationScript(TestCommonData.CONFIG_SCRIPT);

    String configId = rcService.loadDay2Configuration(applicationDay2ConfigurationWrapper);

    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationPersisted = rcService
        .getApplicationDay2Configuration(configId);
    assert applicationDay2ConfigurationPersisted.getConfigurationScript().equals(TestCommonData.CONFIG_SCRIPT);
    assert applicationDay2ConfigurationPersisted.getResetConfigScript().equals(TestCommonData.RESET_CONFIG_SCRIPT);

    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.INIT);


    // Load Infrastructure Day2 Configuration Test
    // TODO think how to mock site inventory call

//    InfrastructureMetricWrapper infrastructureMetricWrapper = new InfrastructureMetricWrapper();
//    infrastructureMetricWrapper.setMetricId(TestCommonData.METRIC_ID_INFRA);
//    infrastructureMetricWrapper.setTopic(TestCommonData.TOPIC_INFRA);
//    infrastructureMetricWrapper.setSite(TestCommonData.SITE_INFRA);
//    infrastructureMetricWrapper.setUnit(TestCommonData.UNIT_INFRA);
//    infrastructureMetricWrapper.setInterval(TestCommonData.INTERVAL_INFRA);
//    infrastructureMetricWrapper.setDeviceId(TestCommonData.DEVICE_ID_INFRA);
//
//    InfrastructureDay2ConfigurationWrapper infrastructureDay2ConfigurationWrapper = new InfrastructureDay2ConfigurationWrapper();
//    List<InfrastructureMetricWrapper> infrastructureMetricWrappers = new ArrayList<>();
//    infrastructureMetricWrappers.add(infrastructureMetricWrapper);
//    infrastructureDay2ConfigurationWrapper.setInfrastructureMetricsInfo(infrastructureMetricWrappers);
//
//    String configinfraId = rcService.loadDay2Configuration(infrastructureDay2ConfigurationWrapper);
//
//    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationPersisted = rcService
//        .getInfrastructureDay2Configuration(configinfraId);
//    List<String> persistedConfScripts = new ArrayList<>();
//    persistedConfScripts.add(TestCommonData.COMPLETE_CONFIG_SCRIPT_INFRA);
//    List<String> persistedStopConfScripts = new ArrayList<>();
//    persistedStopConfScripts.add(TestCommonData.COMPLETE_CONFIG_STOP_SCRIPT_INFRA);
//    assert infrastructureDay2ConfigurationPersisted.getConfigurationScripts().equals(persistedConfScripts);
//    assert infrastructureDay2ConfigurationPersisted.getStopConfigScripts().equals(persistedStopConfScripts);



    // Validate Application Configuration State changes Test

    rcService.startApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CONFIGURING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.COMPLETED);

    while (rcService.getApplicationDay2ConfigurationStatus(configId).equals(Day2ConfigurationStatus.COMPLETED)){
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    rcService.abortApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.ABORTING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.ABORTED);

    // No need of waiting

    rcService.resetApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANED);


//    // Validate Infrastructure Configuration State changes Test
//
//    rcService.startInfrastructureDay2Configuration(configinfraId);
//    day2ConfigurationStatus = rcService.getInfrastructureDay2ConfigurationStatus(configinfraId);
//    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CONFIGURING) ||
//        day2ConfigurationStatus.equals(Day2ConfigurationStatus.COMPLETED);
//
//    rcService.resetInfrastructureDay2Configuration(configinfraId);
//    day2ConfigurationStatus = rcService.getInfrastructureDay2ConfigurationStatus(configinfraId);
//    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANING) ||
//        day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANED);

    // Load Execution Test

    ExecutionWrapper executionWrapper = new ExecutionWrapper();
    executionWrapper.setExecScript(TestCommonData.EXEC_SCRIPT);

    String execId = rcService.loadExecution(executionWrapper);
    ExecutionWrapper executionWrapperPersisted = rcService.getExecution(execId);
    assert executionWrapperPersisted.getExecScript().equals(TestCommonData.EXEC_SCRIPT);

    // Execution State changes Test

    rcService.startExecution(execId);
    ExecutionStatus executionStatus = rcService.getExecutionStatus(execId);
    assert executionStatus.equals(ExecutionStatus.RUNNING) ||
        executionStatus.equals(ExecutionStatus.COMPLETED);

    while (rcService.getExecutionStatus(execId).equals(ExecutionStatus.COMPLETED)) { // Wait for execution status to Complete
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    rcService.abortExecution(execId);
    ExecutionStatus executionStatus1 = rcService.getExecutionStatus(execId);
    log.info(executionStatus1.toString());
    assert executionStatus1.equals(ExecutionStatus.ABORTING) ||
        executionStatus1.equals(ExecutionStatus.ABORTED);
  }
}