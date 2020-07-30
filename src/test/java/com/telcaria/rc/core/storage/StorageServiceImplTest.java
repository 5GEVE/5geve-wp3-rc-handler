package com.telcaria.rc.core.storage;


import com.telcaria.rc.TestCommonData;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.DataShipper;
import com.telcaria.rc.core.wrappers.ExecutionEntityWrapper;

import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class StorageServiceImplTest {

  @Autowired
  StorageService storageService;

  @Test
  void functionalTest() {
    // Load Application Day2 Configuration Test

    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationWrapper = new ApplicationDay2ConfigurationEntityWrapper();
    applicationDay2ConfigurationWrapper.setConfigurationScript(TestCommonData.CONFIG_SCRIPT);
    applicationDay2ConfigurationWrapper.setResetConfigScript(TestCommonData.RESET_CONFIG_SCRIPT);

    String configId = storageService.loadApplicationDay2Configuration(applicationDay2ConfigurationWrapper);

    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationPersisted = storageService
        .getApplicationDay2Configuration(configId);
    assert applicationDay2ConfigurationPersisted.getConfigurationScript().equals(TestCommonData.CONFIG_SCRIPT);
    assert applicationDay2ConfigurationPersisted.getResetConfigScript().equals(TestCommonData.RESET_CONFIG_SCRIPT);

    // Load Infrastructure Day2 Configuration Test

    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationWrapper = new InfrastructureDay2ConfigurationEntityWrapper();
    String persistedConfScripts = TestCommonData.COMPLETE_CONFIG_SCRIPT_INFRA;
    String persistedStopConfScripts = TestCommonData.COMPLETE_CONFIG_STOP_SCRIPT_INFRA;
    infrastructureDay2ConfigurationWrapper.setConfigurationScripts(persistedConfScripts);
    infrastructureDay2ConfigurationWrapper.setStopConfigScripts(persistedStopConfScripts);

    String configinfraId = storageService.loadInfrastructureDay2Configuration(infrastructureDay2ConfigurationWrapper);

    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationPersisted = storageService
        .getInfrastructureDay2Configuration(configinfraId);
    assert infrastructureDay2ConfigurationPersisted != null;

    persistedConfScripts = TestCommonData.COMPLETE_CONFIG_SCRIPT_INFRA;
    persistedStopConfScripts = TestCommonData.COMPLETE_CONFIG_STOP_SCRIPT_INFRA;
    assert infrastructureDay2ConfigurationPersisted.getConfigurationScripts().equals(persistedConfScripts);
    assert infrastructureDay2ConfigurationPersisted.getStopConfigScripts().equals(persistedStopConfScripts);


    // Load Execution Test

    ExecutionEntityWrapper executionWrapper = new ExecutionEntityWrapper();
    executionWrapper.setExecScript(TestCommonData.EXEC_SCRIPT);

    String execId = storageService.loadExecution(executionWrapper);
    ExecutionWrapper executionWrapperPersisted = storageService.getExecution(execId);
    assert executionWrapperPersisted.getExecScript().equals(TestCommonData.EXEC_SCRIPT);

    // Update Application Day2 Configuration Status Test

    storageService.updateApplicationDay2ConfigurationStatus(configId, Day2ConfigurationStatus.COMPLETED);
    Day2ConfigurationStatus day2ConfigurationStatus = storageService
        .getApplicationDay2ConfigurationStatus(configId);
    assert day2ConfigurationStatus.equals(Day2ConfigurationStatus.COMPLETED);

    // Update Infrastructure Day2 Configuration Status Test

    storageService.updateInfrastructureDay2ConfigurationStatus(configinfraId, Day2ConfigurationStatus.COMPLETED);
    Day2ConfigurationStatus day2ConfigurationStatusInfra = storageService
        .getInfrastructureDay2ConfigurationStatus(configinfraId);
    assert day2ConfigurationStatusInfra.equals(Day2ConfigurationStatus.COMPLETED);

    // Update Execution Status Test

    storageService.updateExecutionStatus(execId, ExecutionStatus.COMPLETED);
    ExecutionStatus executionStatus = storageService
        .getExecutionStatus(execId);
    assert executionStatus.equals(ExecutionStatus.COMPLETED);

    // Delete Application Day2 Configuration Test

    storageService.deleteApplicationDay2Configuration(configId);
    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationDeleted = storageService
        .getApplicationDay2Configuration(configId);
    assert applicationDay2ConfigurationDeleted == null;

    // Delete Infrastructure Day2 Configuration Test

    storageService.deleteInfrastructureDay2Configuration(configinfraId);
    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationDelete = storageService
        .getInfrastructureDay2Configuration(configinfraId);
    assert infrastructureDay2ConfigurationDelete == null;

    // Delete Execution Test

    storageService.deleteExecution(execId);
    ExecutionWrapper executionDeleted = storageService.getExecution(execId);
    assert executionDeleted == null;

  }
}