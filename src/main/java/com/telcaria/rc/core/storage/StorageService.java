package com.telcaria.rc.core.storage;

import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.DataShipper;
import com.telcaria.rc.core.wrappers.ExecutionEntityWrapper;

import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;

import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;


public interface StorageService {

  ApplicationDay2ConfigurationEntityWrapper getApplicationDay2Configuration(String configId);

  InfrastructureDay2ConfigurationEntityWrapper getInfrastructureDay2Configuration(String configId);

  Day2ConfigurationStatus getApplicationDay2ConfigurationStatus(String configId);

  Day2ConfigurationStatus getInfrastructureDay2ConfigurationStatus(String configId);

  String loadApplicationDay2Configuration(
      ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationEntityWrapper);

  String loadInfrastructureDay2Configuration(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationEntityWrapper);

  void updateApplicationDay2ConfigurationStatus(String configId,
      Day2ConfigurationStatus day2ConfigurationStatus);

  void updateInfrastructureDay2ConfigurationStatus(String configId,
      Day2ConfigurationStatus day2ConfigurationStatus);

  void deleteApplicationDay2Configuration(String configId);

  void deleteInfrastructureDay2Configuration(String configId);

  ExecutionWrapper getExecution(String executionId);

  ExecutionStatus getExecutionStatus(String executionId);

  String loadExecution(ExecutionEntityWrapper executionEntityWrapper);

  void updateExecutionStatus(String executionId, ExecutionStatus executionStatus);

  void deleteExecution(String executionId);

}
