package com.telcaria.rc.core.service;

import com.telcaria.rc.TestCommonData;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;

import com.telcaria.rc.core.wrappers.DataShipper;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static com.telcaria.rc.TestCommonData.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
class RCServiceImplTest {

  @Autowired
  RCService rcService;

  @Autowired
  IWFSiteInventoryDataShipperClient iwfSiteInventoryDataShipperClient;

  @Test
  @Order(1)
  void functionalTest() {
    // Load Application Day2 Configuration Test

    ApplicationDay2ConfigurationWrapper applicationDay2ConfigurationWrapper = new ApplicationDay2ConfigurationWrapper();
    applicationDay2ConfigurationWrapper.setResetConfigScript(RESET_CONFIG_SCRIPT);
    applicationDay2ConfigurationWrapper.setConfigurationScript(CONFIG_SCRIPT);

    String configId = rcService.loadDay2Configuration(applicationDay2ConfigurationWrapper);

    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationPersisted = rcService
        .getApplicationDay2Configuration(configId);
    assert applicationDay2ConfigurationPersisted.getConfigurationScript().equals(CONFIG_SCRIPT);
    assert applicationDay2ConfigurationPersisted.getResetConfigScript().equals(RESET_CONFIG_SCRIPT);

    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.INIT);


    // Load Infrastructure Day2 Configuration Test
    DataShipper dataShipper = new DataShipper();
    dataShipper.setId(2);
    dataShipper.setStopConfigScript(CONFIG_STOP_SCRIPT_INFRA);
    dataShipper.setConfigurationScript(CONFIG_SCRIPT_INFRA);
    dataShipper.setDataShipperId(rcService.buildDataShipperId(SITE_INFRA, SITE_I_METRIC_TYPE, METRIC_ID_INFRA));
    dataShipper.setIpAddress("127.0.0.1");
    dataShipper.setMetricType("CPU_CONSUMPTION");
    dataShipper.setUsername("root");
    dataShipper.setPassword("password");
    iwfSiteInventoryDataShipperClient.createDataShipper(dataShipper); // Add new datashipper
    iwfSiteInventoryDataShipperClient.linkSite(ITALY_SITE_ID, String.format("http://localhost:8087/sites/%s",ITALY_SITE_ID)); // link DataSipper to Site

    InfrastructureMetricWrapper infrastructureMetricWrapper = new InfrastructureMetricWrapper();
    infrastructureMetricWrapper.setMetricId(METRIC_ID_INFRA);
    infrastructureMetricWrapper.setMetricType(SITE_I_METRIC_TYPE);
    infrastructureMetricWrapper.setTopic(TOPIC_INFRA);
    infrastructureMetricWrapper.setSite(SITE_INFRA);
    infrastructureMetricWrapper.setUnit(UNIT_INFRA);
    infrastructureMetricWrapper.setInterval(INTERVAL_INFRA);
    infrastructureMetricWrapper.setDeviceId(DEVICE_ID_INFRA);

    InfrastructureDay2ConfigurationWrapper infrastructureDay2ConfigurationWrapper = new InfrastructureDay2ConfigurationWrapper();
    List<InfrastructureMetricWrapper> infrastructureMetricWrappers = new ArrayList<>();
    infrastructureMetricWrappers.add(infrastructureMetricWrapper);
    infrastructureDay2ConfigurationWrapper.setInfrastructureMetricsInfo(infrastructureMetricWrappers);

    String configinfraId = rcService.loadDay2Configuration(infrastructureDay2ConfigurationWrapper);

    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationPersisted = rcService
        .getInfrastructureDay2Configuration(configinfraId);
    log.info(infrastructureDay2ConfigurationPersisted.getConfigurationScripts());
    assert infrastructureDay2ConfigurationPersisted.getConfigurationScripts().equals(COMPLETE_CONFIG_SCRIPT_INFRA);
    assert infrastructureDay2ConfigurationPersisted.getStopConfigScripts().equals(COMPLETE_CONFIG_STOP_SCRIPT_INFRA);


    // Validate Application Configuration State changes Test

    rcService.startApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CONFIGURING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.COMPLETED);

    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      return rcService.getApplicationDay2ConfigurationStatus(configId).equals(Day2ConfigurationStatus.COMPLETED);
    });

    rcService.abortApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.ABORTING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.ABORTED);

// FIXME ABORT is still not implemented
//    await().atMost(10, TimeUnit.SECONDS).until(() -> {
//      return rcService.getApplicationDay2ConfigurationStatus(configId).equals(Day2ConfigurationStatus.ABORTED);
//    });

    rcService.resetApplicationDay2Configuration(configId);
    day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.CLEANED);

    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      return rcService.getApplicationDay2ConfigurationStatus(configId).equals(Day2ConfigurationStatus.CLEANED);
    });


//    // Validate Infrastructure Configuration State changes Test

    rcService.startInfrastructureDay2Configuration(configinfraId);
    day2ConfigurationStatus = rcService.getInfrastructureDay2ConfigurationStatus(configinfraId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.CONFIGURING) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.COMPLETED);


    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      return rcService.getInfrastructureDay2ConfigurationStatus(configinfraId).equals(Day2ConfigurationStatus.COMPLETED);
    });

    rcService.stopInfrastructureDay2Configuration(configinfraId);
    day2ConfigurationStatus = rcService.getInfrastructureDay2ConfigurationStatus(configinfraId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.STOPPED) ||
        day2ConfigurationStatus.equals(Day2ConfigurationStatus.STOPPING);

    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      return rcService.getInfrastructureDay2ConfigurationStatus(configinfraId).equals(Day2ConfigurationStatus.STOPPED);
    });

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

    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      return rcService.getExecutionStatus(execId).equals(ExecutionStatus.COMPLETED);
    });

    rcService.abortExecution(execId);
    ExecutionStatus executionStatus1 = rcService.getExecutionStatus(execId);
    log.info(executionStatus1.toString());
    assert executionStatus1.equals(ExecutionStatus.ABORTING) ||
        executionStatus1.equals(ExecutionStatus.ABORTED);
// FIXME ABORT is still not implemented
//    await().atMost(10, TimeUnit.SECONDS).until(() -> {
//      return rcService.getExecutionStatus(execId).equals(ExecutionStatus.ABORTED);
//    });
  }

  @Test
  @Order(2)
  void findAndReplaceSiteNameWithKafkaBrokerIp() {
    String execution_script_google_dns = rcService.findAndReplaceSiteFacilitiesWithKafkaBroker("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 8.8.8.8\";");
    assert execution_script_google_dns.equals("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 8.8.8.8\";");

    String execution_script_greece_athens = rcService.findAndReplaceSiteFacilitiesWithKafkaBroker("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 GREECE_ATHENS\";");
    assert execution_script_greece_athens.equals("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 10.0.0.6\";");

    String execution_script_greece_athens_and_italy_turin = rcService.findAndReplaceSiteFacilitiesWithKafkaBroker("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 GREECE_ATHENS\";EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 ITALY_TURIN\";");
    assert execution_script_greece_athens_and_italy_turin.equals("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 10.0.0.6\";EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 10.50.80.18\";");

    String execution_script_italy_turin = rcService.findAndReplaceSiteFacilitiesWithKafkaBroker("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 ITALY_TURIN\";EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 ITALY_TURIN\";");
    assert execution_script_italy_turin.equals("EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 10.50.80.18\";EXECUTE_COMMAND 127.0.0.1 root:root \"ping -c 1 10.50.80.18\";");
  }
}