package com.telcaria.rc.core.service;


import com.telcaria.rc.core.events.ApplicationEventResponse;
import com.telcaria.rc.core.events.ExecutionEventResponse;
import com.telcaria.rc.core.events.InfrastructureEventResponse;
import com.telcaria.rc.core.storage.StorageService;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.DataShipper;
import com.telcaria.rc.core.wrappers.ExecutionEntityWrapper;
import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;

import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationScripts;
import com.telcaria.rc.core.wrappers.Site;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureMetricWrapper;
import com.telcaria.rc.sbi.SBIProvider;
import com.telcaria.rc.siteinventory.SiteInventoryDataShipperClient;
import com.telcaria.rc.siteinventory.SiteInventorySiteClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.event.EventListener;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RCServiceImpl implements RCService {

  private StorageService storageService;

  private SiteInventoryDataShipperClient siteInventoryDataShipperClient;

  private SiteInventorySiteClient siteInventorySiteClient;

  private SBIProvider sbiProvider;

  @Value("${rc.testing}")
  private String testing;

  @Autowired
  public RCServiceImpl(StorageService storageService,
      SiteInventoryDataShipperClient siteInventoryDataShipperClient,
      SiteInventorySiteClient siteInventorySiteClient,
      SBIProvider sbiProvider) {
    this.storageService = storageService;
    this.siteInventoryDataShipperClient = siteInventoryDataShipperClient;
    this.siteInventorySiteClient = siteInventorySiteClient;
    this.sbiProvider = sbiProvider;
  }

  @Override
  public ApplicationDay2ConfigurationEntityWrapper getApplicationDay2Configuration(String configId) {
    return storageService.getApplicationDay2Configuration(configId);
  }

  @Override
  public InfrastructureDay2ConfigurationEntityWrapper getInfrastructureDay2Configuration(
      String configId) {
    return storageService.getInfrastructureDay2Configuration(configId);
  }

  @Override
  public Day2ConfigurationStatus getApplicationDay2ConfigurationStatus(String configId) {
    return storageService.getApplicationDay2ConfigurationStatus(configId);
  }

  @Override
  public Day2ConfigurationStatus getInfrastructureDay2ConfigurationStatus(String configId) {
    return storageService.getInfrastructureDay2ConfigurationStatus(configId);
  }

  @Override
  public String loadDay2Configuration(
      ApplicationDay2ConfigurationWrapper applicationDay2ConfigurationWrapper) {
    ModelMapper modelMapper = new ModelMapper();
    ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationEntityWrapper = modelMapper
        .map(applicationDay2ConfigurationWrapper, ApplicationDay2ConfigurationEntityWrapper.class);
    if (applicationDay2ConfigurationEntityWrapper.getConfigurationScript() != null) {
      applicationDay2ConfigurationEntityWrapper.setConfigurationScript(applicationDay2ConfigurationEntityWrapper.getConfigurationScript().replace("\\\"", "\""));
    } else {
      applicationDay2ConfigurationEntityWrapper.setConfigurationScript("");
    }
    if (applicationDay2ConfigurationEntityWrapper.getResetConfigScript() != null) {
      applicationDay2ConfigurationEntityWrapper.setResetConfigScript(applicationDay2ConfigurationEntityWrapper.getResetConfigScript().replace("\\\"", "\""));
    } else {
      applicationDay2ConfigurationEntityWrapper.setResetConfigScript("");
    }

    return storageService.loadApplicationDay2Configuration(applicationDay2ConfigurationEntityWrapper);
  }

  @Override
  public String loadDay2Configuration(
      InfrastructureDay2ConfigurationWrapper infrastructureDay2ConfigurationWrapper) {

    // Create an empty InfrastructureDay2ConfigurationEntityWrapper
    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationEntityWrapper = new InfrastructureDay2ConfigurationEntityWrapper();

    for (InfrastructureMetricWrapper infrastructureMetricWrapper : infrastructureDay2ConfigurationWrapper
        .getInfrastructureMetricsInfo()) { // Iterate InfrastructureMetricWrappers

      String dataShipperId = buildDataShipperId(infrastructureMetricWrapper.getSite(), infrastructureMetricWrapper.getMetricId());
      log.info("DataShipperId: {}", dataShipperId);
      CollectionModel<EntityModel<DataShipper>> dataShipperEntityModel = siteInventoryDataShipperClient
          .getDataShipper(dataShipperId);
      if (dataShipperEntityModel != null) {
        log.info("Data shipper received: {}", dataShipperEntityModel.toString());
      } else {
        log.warn("Data shipper is null");
      }
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite(infrastructureMetricWrapper.getSite());
      if (siteEntityModel != null) {
        log.info("Site received: {}", siteEntityModel.toString());
      } else {
        log.warn("Site is null");
      }

      if (dataShipperEntityModel != null && siteEntityModel != null && !dataShipperEntityModel.getContent().isEmpty() && !siteEntityModel.getContent().isEmpty()) {
        DataShipper dataShipper = ((EntityModel<DataShipper>) dataShipperEntityModel.getContent().toArray()[0]).getContent();
        log.info("Data shipper received: {}", dataShipper.toString());
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        log.info("Site received: {}", site.toString());
        InfrastructureDay2ConfigurationScripts infrastructureDay2ConfigurationScripts = generateInfrastructureDay2ConfigurationEntityWrapper(
                infrastructureMetricWrapper, dataShipper, site);
        //infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts().add(infrastructureDay2ConfigurationScripts.getConfigurationScript());
        //infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts().add(infrastructureDay2ConfigurationScripts.getStopConfigScript());
        log.info("Commands generated: {}", infrastructureDay2ConfigurationScripts.toString());
        if (!infrastructureDay2ConfigurationScripts.getConfigurationScript().equals("")) {
          if (infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts() == null) {
            infrastructureDay2ConfigurationEntityWrapper.setConfigurationScripts(infrastructureDay2ConfigurationScripts.getConfigurationScript().concat("; "));
          } else {
            infrastructureDay2ConfigurationEntityWrapper.setConfigurationScripts(infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts().concat(infrastructureDay2ConfigurationScripts.getConfigurationScript()).concat("; "));
          }
        }
        if (!infrastructureDay2ConfigurationScripts.getStopConfigScript().equals("")) {
          if (infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts() == null) {
            infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts(infrastructureDay2ConfigurationScripts.getStopConfigScript().concat("; "));
          } else {
            infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts(infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts().concat(infrastructureDay2ConfigurationScripts.getStopConfigScript()).concat("; "));
          }
        }
      }
    }
    if (infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts() == null) {
      infrastructureDay2ConfigurationEntityWrapper.setConfigurationScripts("");
    }
    if (infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts() == null) {
      infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts("");
    }
    return storageService.loadInfrastructureDay2Configuration(infrastructureDay2ConfigurationEntityWrapper);
  }

  @Override
  public void startApplicationDay2Configuration(String configurationId) {
    storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.CONFIGURING);
    ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration = storageService
        .getApplicationDay2Configuration(configurationId);
    sbiProvider.start(applicationDay2Configuration);
    //storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.COMPLETED);
  }

  @Override
  public void resetApplicationDay2Configuration(String configurationId) {
    storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.CLEANING);
    ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration = storageService
        .getApplicationDay2Configuration(configurationId);
    sbiProvider.stop(applicationDay2Configuration);
    //storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.CLEANED);
  }

  @Override
  public void abortApplicationDay2Configuration(String configurationId) {
    storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.ABORTING);
    ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration = storageService
        .getApplicationDay2Configuration(configurationId);
    sbiProvider.abort(applicationDay2Configuration);
    //storageService.updateApplicationDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.ABORTED);
  }

  @Override
  public void startInfrastructureDay2Configuration(String configurationId) {
    storageService.updateInfrastructureDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.CONFIGURING);
    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration = storageService
        .getInfrastructureDay2Configuration(configurationId);
    sbiProvider.start(infrastructureDay2Configuration);
    //storageService.updateInfrastructureDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.COMPLETED);
  }

  @Override
  public void stopInfrastructureDay2Configuration(String configurationId) {
    storageService.updateInfrastructureDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.STOPPING);
    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration = storageService
        .getInfrastructureDay2Configuration(configurationId);
    sbiProvider.stop(infrastructureDay2Configuration);
    //storageService.updateInfrastructureDay2ConfigurationStatus(configurationId, Day2ConfigurationStatus.STOPPED);
  }

  @Override
  public ExecutionWrapper getExecution(String executionId) {
    return storageService.getExecution(executionId);
  }

  @Override
  public ExecutionStatus getExecutionStatus(String executionId) {
    return storageService.getExecutionStatus(executionId);
  }

  @Override
  public String loadExecution(ExecutionWrapper executionWrapper) {
    ModelMapper modelMapper = new ModelMapper();
    ExecutionEntityWrapper executionEntityWrapper = modelMapper
        .map(executionWrapper, ExecutionEntityWrapper.class);
    if (executionWrapper.getExecScript() != null) {
      executionWrapper.setExecScript(executionWrapper.getExecScript().replace("\\\"", "\""));
    } else {
      executionWrapper.setExecScript("");
    }
    return storageService.loadExecution(executionEntityWrapper);
  }

  @Override
  public void startExecution(String executionId) {
    storageService.updateExecutionStatus(executionId, ExecutionStatus.RUNNING);
    ExecutionWrapper execution = storageService.getExecution(executionId);
    sbiProvider.start(execution);
    //storageService.updateExecutionStatus(executionId, ExecutionStatus.COMPLETED);
  }

  @Override
  public void abortExecution(String executionId) {
    storageService.updateExecutionStatus(executionId, ExecutionStatus.ABORTING);
    ExecutionWrapper execution = storageService.getExecution(executionId);
    sbiProvider.abort(execution);
    //storageService.updateExecutionStatus(executionId, ExecutionStatus.ABORTED);
  }

  @Override
  @EventListener
  public void handleExecutionResponse(ExecutionEventResponse executionResponse) {
    if (executionResponse.getMethod().equals("START")) {
      if (executionResponse.isSuccessful()) {
        storageService.updateExecutionStatus(executionResponse.getExecutionId(), ExecutionStatus.COMPLETED);
      } else {
        storageService.updateExecutionStatus(executionResponse.getExecutionId(), ExecutionStatus.FAILED);
      }
    }
  }

  @Override
  @EventListener
  public void handleApplicationResponse(ApplicationEventResponse applicationEventResponse) {
    if (applicationEventResponse.getMethod().equals("START")) {
      if (applicationEventResponse.isSuccessful()) {
        storageService
            .updateApplicationDay2ConfigurationStatus(applicationEventResponse.getConfigId(),
                Day2ConfigurationStatus.COMPLETED);
      } else {
        storageService
            .updateApplicationDay2ConfigurationStatus(applicationEventResponse.getConfigId(),
                Day2ConfigurationStatus.FAILED);
      }
    } else if (applicationEventResponse.getMethod().equals("RESET")) {
      if (applicationEventResponse.isSuccessful()) {
        storageService.updateApplicationDay2ConfigurationStatus(applicationEventResponse.getConfigId(), Day2ConfigurationStatus.CLEANED);
      } else {
        storageService.updateApplicationDay2ConfigurationStatus(applicationEventResponse.getConfigId(), Day2ConfigurationStatus.FAILED);
      }
    }
  }

  @Override
  @EventListener
  public void handleInfrastructureResponse(
      InfrastructureEventResponse infrastructureEventResponse) {
    if (infrastructureEventResponse.getMethod().equals("START")) {
      if (infrastructureEventResponse.isSuccessful()) {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getConfigId(), Day2ConfigurationStatus.COMPLETED);
      } else {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getConfigId(), Day2ConfigurationStatus.FAILED);
      }
    } else { // STOP
      if (infrastructureEventResponse.isSuccessful()) {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getConfigId(), Day2ConfigurationStatus.STOPPED);
      } else {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getConfigId(), Day2ConfigurationStatus.FAILED);
      }
    }
  }

  private String buildDataShipperId(String site, String metricId) {
    return site+"."+metricId;
  }

  private InfrastructureDay2ConfigurationScripts generateInfrastructureDay2ConfigurationEntityWrapper(
      InfrastructureMetricWrapper infrastructureMetricWrapper,
      DataShipper dataShipper, Site site) {
    InfrastructureDay2ConfigurationScripts infrastructureDay2ConfigurationScripts = new InfrastructureDay2ConfigurationScripts();

    if (dataShipper.getConfigurationScript() == null) {
      infrastructureDay2ConfigurationScripts.setConfigurationScript("");
    } else if (dataShipper.getConfigurationScript().equals("")) {
      infrastructureDay2ConfigurationScripts.setConfigurationScript("");
    } else {
      String configurationScript = dataShipper.getConfigurationScript()
              .replace("$$ipAddress", dataShipper.getIpAddress())
              .replace("$$username", dataShipper.getUsername())
              .replace("$$password", dataShipper.getPassword())
              .replace("$$metric_id", infrastructureMetricWrapper.getMetricId())
              .replace("$$topic_name", infrastructureMetricWrapper.getTopic())
              .replace("$$broker_ip_address", site.getKafkaIpAddress())
              .replace("$$unit", infrastructureMetricWrapper.getUnit())
              .replace("$$interval", infrastructureMetricWrapper.getInterval());
      if (infrastructureMetricWrapper.getDeviceId() == null) {
        configurationScript = configurationScript.replace("$$device_id", "nil");
      } else {
        configurationScript = configurationScript.replace("$$device_id", infrastructureMetricWrapper.getDeviceId());
      }
      infrastructureDay2ConfigurationScripts.setConfigurationScript(configurationScript);
    }

    if (dataShipper.getStopConfigScript() == null) {
      infrastructureDay2ConfigurationScripts.setStopConfigScript("");
    } else if (dataShipper.getStopConfigScript().equals("")) {
      infrastructureDay2ConfigurationScripts.setStopConfigScript("");
    } else {
      String stopConfigScript = dataShipper.getStopConfigScript()
              .replace("$$ipAddress", dataShipper.getIpAddress())
              .replace("$$username", dataShipper.getUsername())
              .replace("$$password", dataShipper.getPassword())
              .replace("$$metric_id", infrastructureMetricWrapper.getMetricId())
              .replace("$$topic_name", infrastructureMetricWrapper.getTopic())
              .replace("$$broker_ip_address", site.getKafkaIpAddress())
              .replace("$$unit", infrastructureMetricWrapper.getUnit())
              .replace("$$interval", infrastructureMetricWrapper.getInterval());
      if (infrastructureMetricWrapper.getDeviceId() == null) {
        stopConfigScript = stopConfigScript.replace("$$device_id", "nil");
      } else {
        stopConfigScript = stopConfigScript.replace("$$device_id", infrastructureMetricWrapper.getDeviceId());
      }
      infrastructureDay2ConfigurationScripts.setStopConfigScript(stopConfigScript);
    }
    return infrastructureDay2ConfigurationScripts;
  }

}
