package com.telcaria.rc.core.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.telcaria.rc.core.wrappers.msno.CpProtocolInfo;
import com.telcaria.rc.core.wrappers.msno.ExtCpInfo;
import com.telcaria.rc.core.wrappers.msno.IpAddresses;
import com.telcaria.rc.core.wrappers.msno.NsInstance;
import com.telcaria.rc.core.wrappers.msno.VnfInstance;
import com.telcaria.rc.msno.MsnoClient;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureMetricWrapper;
import com.telcaria.rc.sbi.SBIProvider;
import com.telcaria.rc.siteinventory.SiteInventoryDataShipperClient;
import com.telcaria.rc.siteinventory.SiteInventorySiteClient;
import feign.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

  private MsnoClient msnoClient;

  private SBIProvider sbiProvider;

  @Autowired
  public RCServiceImpl(StorageService storageService,
                       SiteInventoryDataShipperClient siteInventoryDataShipperClient,
                       SiteInventorySiteClient siteInventorySiteClient,
                       MsnoClient msnoClient,
                       SBIProvider sbiProvider) {
    this.storageService = storageService;
    this.siteInventoryDataShipperClient = siteInventoryDataShipperClient;
    this.siteInventorySiteClient = siteInventorySiteClient;
    this.msnoClient = msnoClient;
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
    // format if necessary the configuration script
    if (applicationDay2ConfigurationEntityWrapper.getConfigurationScript() != null) {
      applicationDay2ConfigurationEntityWrapper.setConfigurationScript(applicationDay2ConfigurationEntityWrapper.getConfigurationScript().replace("\\\"", "\""));
    } else {
      applicationDay2ConfigurationEntityWrapper.setConfigurationScript("");
    }
    applicationDay2ConfigurationEntityWrapper.setConfigurationScript(findAndReplaceSiteFacilitiesWithKafkaBroker(applicationDay2ConfigurationEntityWrapper.getConfigurationScript()));

    // format if necessary the reset configuration script
    if (applicationDay2ConfigurationEntityWrapper.getResetConfigScript() != null) {
      applicationDay2ConfigurationEntityWrapper.setResetConfigScript(applicationDay2ConfigurationEntityWrapper.getResetConfigScript().replace("\\\"", "\""));
    } else {
      applicationDay2ConfigurationEntityWrapper.setResetConfigScript("");
    }
    applicationDay2ConfigurationEntityWrapper.setResetConfigScript(findAndReplaceSiteFacilitiesWithKafkaBroker(applicationDay2ConfigurationEntityWrapper.getResetConfigScript()));

    return storageService.loadApplicationDay2Configuration(applicationDay2ConfigurationEntityWrapper);
  }

  @Override
  public String loadDay2Configuration(
          InfrastructureDay2ConfigurationWrapper infrastructureDay2ConfigurationWrapper, String nsInstanceId) {

    // Create an empty InfrastructureDay2ConfigurationEntityWrapper
    InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationEntityWrapper = new InfrastructureDay2ConfigurationEntityWrapper();

    for (InfrastructureMetricWrapper infrastructureMetricWrapper : infrastructureDay2ConfigurationWrapper
        .getInfrastructureMetricsInfo()) { // Iterate InfrastructureMetricWrappers

      String dataShipperId = buildDataShipperId(infrastructureMetricWrapper.getSite(), infrastructureMetricWrapper.getMetricType(), infrastructureMetricWrapper.getMetricId());
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

        String ipAddressesString = "nil";

        if (nsInstanceId != null) {
          Response nsResponse = msnoClient.getNsInstance("1.0", "application/json",
                                                         "application/json", nsInstanceId,
                                                         true, false);

          if (nsResponse != null && nsResponse.status() == 200) {
            try {
              String nsResponseString = IOUtils.toString(nsResponse.body().asInputStream());
              log.info("nsInstance received: {}", nsResponseString);
              ObjectMapper objectMapper = new ObjectMapper();
              objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
              NsInstance nsInstance = objectMapper.readValue(nsResponseString, NsInstance.class);
              List<String> ipAddresses = parseNsInstanceToListIpAddresses(nsInstance);
              ipAddressesString = parseIpAddressListToString(ipAddresses);
              log.info("VNF IP addresses received: {}", ipAddressesString);
            } catch (JsonProcessingException e) {
              log.error("Error parsing JSON response from MSNO", e);
            } catch (IOException e) {
              log.error("IO error reading response from MSNO", e);
            }
          } else {
            log.warn("nsInstance is null");
          }
        }

        InfrastructureDay2ConfigurationScripts infrastructureDay2ConfigurationScripts = generateInfrastructureDay2ConfigurationEntityWrapper(
                infrastructureMetricWrapper, dataShipper, site, ipAddressesString);
        //infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts().add(infrastructureDay2ConfigurationScripts.getConfigurationScript());
        //infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts().add(infrastructureDay2ConfigurationScripts.getStopConfigScript());
        log.info("Commands generated: {}", infrastructureDay2ConfigurationScripts.toString());
        //infrastructureDay2ConfigurationEntityWrapper.setConfigurationScripts(infrastructureDay2ConfigurationScripts.getConfigurationScript());
        //infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts(infrastructureDay2ConfigurationScripts.getStopConfigScript());

        // The following if-else blocks are needed because there could be more than one infrastructure metric, and maybe the scripts are empty.
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
    infrastructureDay2ConfigurationEntityWrapper.setConfigurationScripts(findAndReplaceSiteFacilitiesWithKafkaBroker(infrastructureDay2ConfigurationEntityWrapper.getConfigurationScripts()));

    if (infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts() == null) {
      infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts("");
    }
    infrastructureDay2ConfigurationEntityWrapper.setStopConfigScripts(findAndReplaceSiteFacilitiesWithKafkaBroker(infrastructureDay2ConfigurationEntityWrapper.getStopConfigScripts()));

    return storageService.loadInfrastructureDay2Configuration(infrastructureDay2ConfigurationEntityWrapper);
  }

  private List<String> parseNsInstanceToListIpAddresses(NsInstance nsInstance) {
    List<String> ipAddresses = new ArrayList<>();

    try {
      for (VnfInstance vnfInstance : nsInstance.getVnfInstance()) {
        for (ExtCpInfo extCpInfo : vnfInstance.getInstantiatedVnfInfo().getExtCpInfo()) {
          for (CpProtocolInfo cpProtocolInfo : extCpInfo.getCpProtocolInfo()) {
            for (IpAddresses ipAddresses1 : cpProtocolInfo.getIpOverEthernet().getIpAddresses()) {
              ipAddresses.addAll(ipAddresses1.getAddresses());
            }
          }
        }
      }
    } catch (NullPointerException e) {
      log.warn("Null value while parsing IP addresses, ignoring");
    }

    return ipAddresses;
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

    if (executionWrapper.getExecScript() != null) {
      executionWrapper.setExecScript(executionWrapper.getExecScript().replace("\\\"", "\""));
    } else {
      executionWrapper.setExecScript("");
    }
    executionWrapper.setExecScript(findAndReplaceSiteFacilitiesWithKafkaBroker(executionWrapper.getExecScript()));

    ModelMapper modelMapper = new ModelMapper();
    ExecutionEntityWrapper executionEntityWrapper = modelMapper
        .map(executionWrapper, ExecutionEntityWrapper.class);

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
        storageService.updateExecutionStatus(executionResponse.getId(), ExecutionStatus.COMPLETED);
      } else {
        storageService.updateExecutionStatus(executionResponse.getId(), ExecutionStatus.FAILED);
      }
    }
  }

  @Override
  @EventListener
  public void handleApplicationResponse(ApplicationEventResponse applicationEventResponse) {
    if (applicationEventResponse.getMethod().equals("START")) {
      if (applicationEventResponse.isSuccessful()) {
        storageService
            .updateApplicationDay2ConfigurationStatus(applicationEventResponse.getId(),
                Day2ConfigurationStatus.COMPLETED);
      } else {
        storageService
            .updateApplicationDay2ConfigurationStatus(applicationEventResponse.getId(),
                Day2ConfigurationStatus.FAILED);
      }
    } else if (applicationEventResponse.getMethod().equals("RESET")) {
      if (applicationEventResponse.isSuccessful()) {
        storageService.updateApplicationDay2ConfigurationStatus(applicationEventResponse.getId(), Day2ConfigurationStatus.CLEANED);
      } else {
        storageService.updateApplicationDay2ConfigurationStatus(applicationEventResponse.getId(), Day2ConfigurationStatus.FAILED);
      }
    }
  }

  @Override
  @EventListener
  public void handleInfrastructureResponse(
      InfrastructureEventResponse infrastructureEventResponse) {
    if (infrastructureEventResponse.getMethod().equals("START")) {
      if (infrastructureEventResponse.isSuccessful()) {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getId(), Day2ConfigurationStatus.COMPLETED);
      } else {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getId(), Day2ConfigurationStatus.FAILED);
      }
    } else { // STOP
      if (infrastructureEventResponse.isSuccessful()) {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getId(), Day2ConfigurationStatus.STOPPED);
      } else {
        storageService.updateInfrastructureDay2ConfigurationStatus(infrastructureEventResponse.getId(), Day2ConfigurationStatus.FAILED);
      }
    }
  }

  @Override
  public String buildDataShipperId(String site, String iMetricType, String metricId) {
    return site+"."+iMetricType+"."+metricId;
  }

  private InfrastructureDay2ConfigurationScripts generateInfrastructureDay2ConfigurationEntityWrapper(
      InfrastructureMetricWrapper infrastructureMetricWrapper,
      DataShipper dataShipper, Site site, String ipAddressesString) {
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

      if (!ipAddressesString.equals("nil")) {
        configurationScript = configurationScript.replace("$$vnfIpAddresses", ipAddressesString);
      } else {
        configurationScript = configurationScript.replace("$$vnfIpAddresses", "");
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

      if (!ipAddressesString.equals("nil")) {
        stopConfigScript = stopConfigScript.replace("$$vnfIpAddresses", ipAddressesString);
      } else {
        stopConfigScript = stopConfigScript.replace("$$vnfIpAddresses", "");
      }

      infrastructureDay2ConfigurationScripts.setStopConfigScript(stopConfigScript);
    }
    return infrastructureDay2ConfigurationScripts;
  }

  private String parseIpAddressListToString(List<String> ipAddresses) {
    String ipAddressesString = "";

    if (ipAddresses.size() > 0) {
      for (String s : ipAddresses) {
        ipAddressesString = ipAddressesString.concat(s + ",");
      }
      // Remove the last char
      ipAddressesString = ipAddressesString.substring(0, ipAddressesString.length() - 1);
    } else {
      ipAddressesString = "nil";
    }

    return ipAddressesString;
  }

  @Override
  public String findAndReplaceSiteFacilitiesWithKafkaBroker(String script) {

    if (script.contains("__ITALY_TURIN")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("ITALY_TURIN");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__ITALY_TURIN", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__SPAIN_5TONIC")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("SPAIN_5TONIC");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__SPAIN_5TONIC", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__FRANCE_PARIS")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("FRANCE_PARIS");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__FRANCE_PARIS", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__FRANCE_RENNES")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("FRANCE_RENNES");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__FRANCE_RENNES", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__FRANCE_NICE")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("FRANCE_NICE");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__FRANCE_NICE", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__GREECE_ATHENS")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("GREECE_ATHENS");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__GREECE_ATHENS", site.getKafkaIpAddress());
      }
    }
    if (script.contains("__SPAIN_5GROWTH_INNOVALIA")) { // Retrieve KAFKA_BROKER_IP from iwf repository
      CollectionModel<EntityModel<Site>> siteEntityModel = siteInventorySiteClient.getSite("SPAIN_5GROWTH_INNOVALIA");
      if (siteEntityModel != null && !siteEntityModel.getContent().isEmpty()) {
        Site site = ((EntityModel<Site>) siteEntityModel.getContent().toArray()[0]).getContent();
        script = script.replace("__SPAIN_5GROWTH_INNOVALIA", site.getKafkaIpAddress());
      }
    }

    return script;
  }
}
