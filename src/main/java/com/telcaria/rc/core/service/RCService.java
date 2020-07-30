package com.telcaria.rc.core.service;



import com.telcaria.rc.core.events.ApplicationEventResponse;
import com.telcaria.rc.core.events.ExecutionEventResponse;
import com.telcaria.rc.core.events.InfrastructureEventResponse;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import org.springframework.context.event.EventListener;

public interface RCService {

  ApplicationDay2ConfigurationEntityWrapper getApplicationDay2Configuration(String configId);

  InfrastructureDay2ConfigurationEntityWrapper getInfrastructureDay2Configuration(String configId);

  Day2ConfigurationStatus getApplicationDay2ConfigurationStatus(String configId);

  Day2ConfigurationStatus getInfrastructureDay2ConfigurationStatus(String configId);

  String loadDay2Configuration(ApplicationDay2ConfigurationWrapper applicationDay2ConfigurationWrapper);

  String loadDay2Configuration(InfrastructureDay2ConfigurationWrapper infrastructureDay2ConfigurationWrapper);

  void startApplicationDay2Configuration(String configurationId);

  void resetApplicationDay2Configuration(String configurationId);

  void abortApplicationDay2Configuration(String configurationId);

  void startInfrastructureDay2Configuration(String configurationId);

  void stopInfrastructureDay2Configuration(String configurationId);

  ExecutionWrapper getExecution(String executionId);

  ExecutionStatus getExecutionStatus(String executionId);

  String loadExecution(ExecutionWrapper executionWrapper);

  void startExecution(String executionId);

  void abortExecution(String executionId);

  @EventListener
  void handleExecutionResponse(ExecutionEventResponse executionResponse);

  @EventListener
  void handleApplicationResponse(ApplicationEventResponse applicationEventResponse);

  @EventListener
  void handleInfrastructureResponse(
      InfrastructureEventResponse infrastructureEventResponse);

  String buildDataShipperId(String site, String iMetricType, String metricId);

  String findAndReplaceSiteFacilitiesWithKafkaBroker(String script);
}
