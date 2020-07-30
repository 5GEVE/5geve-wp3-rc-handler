package com.telcaria.rc.nbi.controllers;

import com.telcaria.rc.core.service.RCService;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ExecutionResponse;
import com.telcaria.rc.nbi.wrappers.ExecutionStatusResponse;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationResponse;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationStatusResponse;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationResponse;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationStatusResponse;
import com.telcaria.rc.nbi.wrappers.ApplicationDay2ConfigurationWrapper;
import com.telcaria.rc.nbi.wrappers.InfrastructureDay2ConfigurationWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping(value = "/rc/nbi")
public class RCNBIController {

  @Autowired
  RCService rcService;

  @GetMapping
  public @ResponseBody String listApiVersion() {
    return "";
  }

  // RC NBI Day-2 configuration for application metrics operations

  @PostMapping(value = "/application/day2/configuration")
  public @ResponseBody
  ApplicationDay2ConfigurationResponse applicationDay2ConfigurationInit(@RequestBody ApplicationDay2ConfigurationWrapper day2ConfigurationWrapper) {
    /*
     * Workflow:
     * - Receive the data, create the corresponding entity assigning to it a configId.
     * - Set VALIDATING status while copying data from wrapper to entity. Save the entity.
     * - Set INIT status.
     * - If exception - FAILED status.
     * - Return configId and status.
     */
    log.info("Calling POST /application/day2/configuration with Body: {}", day2ConfigurationWrapper.toString());
    String configId = rcService.loadDay2Configuration(day2ConfigurationWrapper);
    if (configId != null) {
      log.info("configId: {}", configId);
      Day2ConfigurationStatus day2ConfigurationStatus = rcService
          .getApplicationDay2ConfigurationStatus(configId);
      ApplicationDay2ConfigurationResponse applicationDay2ConfigurationResponse = new ApplicationDay2ConfigurationResponse();
      applicationDay2ConfigurationResponse.setConfigurationId(configId);
      applicationDay2ConfigurationResponse.setStatus(day2ConfigurationStatus);
      return applicationDay2ConfigurationResponse;
    }
    throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "Application Day2 Loading Failed"
    );
  }

  @GetMapping(value = "/application/day2/configuration/{configurationId}")
  public @ResponseBody
  ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStatus(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Return current status.
     */
    log.info("Calling GET /application/day2/configuration/{}", configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService.getApplicationDay2ConfigurationStatus(configurationId);
    if (day2ConfigurationStatus != null) {
      ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStatusResponse = new ApplicationDay2ConfigurationStatusResponse();
      applicationDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
      return applicationDay2ConfigurationStatusResponse;
    }
    throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Application Day2 Configuration not found"
    );
  }

  @PostMapping(value = "/application/day2/configuration/{configurationId}/start")
  public @ResponseBody
  ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStart(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set CONFIGURING status.
     * - Execute configStart in another thread, so that the EEM is not waiting until we finish. After finishing, set COMPLETED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling POST /application/day2/configuration/{}/start", configurationId);
    rcService.startApplicationDay2Configuration(configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getApplicationDay2ConfigurationStatus(configurationId);
    ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStatusResponse = new ApplicationDay2ConfigurationStatusResponse();
    applicationDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
    return applicationDay2ConfigurationStatusResponse;
  }

  @PostMapping(value = "/application/day2/configuration/{configurationId}/reset")
  public @ResponseBody
  ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationReset(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set CONFIGURING status.
     * - Execute configReset in another thread, so that the EEM is not waiting until we finish. After finishing, set COMPLETED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling POST /application/day2/configuration/{}/reset", configurationId);
    rcService.resetApplicationDay2Configuration(configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getApplicationDay2ConfigurationStatus(configurationId);
    ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStatusResponse = new ApplicationDay2ConfigurationStatusResponse();
    applicationDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
    return applicationDay2ConfigurationStatusResponse;
  }

  @DeleteMapping(value = "/application/day2/configuration/{configurationId}/abort")
  public @ResponseBody
  ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationAbort(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set ABORTING status.
     * - Execute the commands needed in another thread, so that the EEM is not waiting until we finish. After finishing, set ABORTED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling DELETE /application/day2/configuration/{}/abort", configurationId);
    rcService.abortApplicationDay2Configuration(configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getApplicationDay2ConfigurationStatus(configurationId);
    ApplicationDay2ConfigurationStatusResponse applicationDay2ConfigurationStatusResponse = new ApplicationDay2ConfigurationStatusResponse();
    applicationDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
    return applicationDay2ConfigurationStatusResponse;
  }

  // RC NBI Day-2 configuration for infrastructure metrics operations

  @PostMapping(value = "/infrastructure/day2/configuration")
  public @ResponseBody
  InfrastructureDay2ConfigurationResponse infrastructureDay2ConfigurationInit(@RequestBody InfrastructureDay2ConfigurationWrapper day2ConfigurationWrapper) {

    /*
     * Workflow:
     * - Receive the data, create the corresponding entity assigning to it a configId.
     * - Set VALIDATING status while copying data from wrapper to entity. Save the entity.
     * - Complete the entity by interacting with the IWL Site Inventory.
     * - Set INIT status.
     * - If exception - FAILED status.
     * - Return configId and status.
     */
    log.info("Calling POST /infrastructure/day2/configuration with Body: {}", day2ConfigurationWrapper.toString());
    String configId = rcService.loadDay2Configuration(day2ConfigurationWrapper);
    if (configId != null) {
      log.info("configId: {}", configId);
      Day2ConfigurationStatus day2ConfigurationStatus = rcService
          .getInfrastructureDay2ConfigurationStatus(configId);
      InfrastructureDay2ConfigurationResponse infrastructureDay2ConfigurationResponse = new InfrastructureDay2ConfigurationResponse();
      infrastructureDay2ConfigurationResponse.setConfigurationId(configId);
      infrastructureDay2ConfigurationResponse.setStatus(day2ConfigurationStatus);
      return infrastructureDay2ConfigurationResponse;
    }
    throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "Infrastructure Day2 Loading Failed"
    );
  }

  @GetMapping(value = "/infrastructure/day2/configuration/{configurationId}")
  public @ResponseBody
  InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStatus(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Return current status.
     */
    log.info("Calling GET /infrastructure/day2/configuration/{}", configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getInfrastructureDay2ConfigurationStatus(configurationId);
    if (day2ConfigurationStatus != null) {
      InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStatusResponse = new InfrastructureDay2ConfigurationStatusResponse();
      infrastructureDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
      return infrastructureDay2ConfigurationStatusResponse;
    }
    throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Infrastructure Day2 Configuration Not found"
    );
  }

  @PostMapping(value = "/infrastructure/day2/configuration/{configurationId}/start")
  public @ResponseBody
  InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStart(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set CONFIGURING status.
     * - Execute configStart in another thread, so that the EEM is not waiting until we finish. After finishing, set COMPLETED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling POST /infrastructure/day2/configuration/{}/start", configurationId);
    rcService.startInfrastructureDay2Configuration(configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getInfrastructureDay2ConfigurationStatus(configurationId);
    InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStatusResponse = new InfrastructureDay2ConfigurationStatusResponse();
    infrastructureDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
    return infrastructureDay2ConfigurationStatusResponse;
  }

  @PostMapping(value = "/infrastructure/day2/configuration/{configurationId}/stop")
  public @ResponseBody
  InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStop(@PathVariable String configurationId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set CONFIGURING status.
     * - Execute configReset in another thread, so that the EEM is not waiting until we finish. After finishing, set COMPLETED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling POST /infrastructure/day2/configuration/{}/stop", configurationId);
    rcService.stopInfrastructureDay2Configuration(configurationId);
    Day2ConfigurationStatus day2ConfigurationStatus = rcService
        .getInfrastructureDay2ConfigurationStatus(configurationId);
    InfrastructureDay2ConfigurationStatusResponse infrastructureDay2ConfigurationStatusResponse = new InfrastructureDay2ConfigurationStatusResponse();
    infrastructureDay2ConfigurationStatusResponse.setStatus(day2ConfigurationStatus);
    return infrastructureDay2ConfigurationStatusResponse;
  }

  // RC NBI for experiment execution operations

  @PostMapping(value = "/execution")
  public @ResponseBody
  ExecutionResponse executionInit(@RequestBody ExecutionWrapper executionWrapper) {

    /*
     * Workflow:
     * - Receive the data, create the corresponding entity assigning to it a configId.
     * - Set INIT status.
     * - Copy data from wrapper to entity. Save the entity.
     * - If exception - FAILED status.
     * - Return configId and status.
     */
    log.info("Calling POST /execution with Body: {}", executionWrapper.toString());
    String execId = rcService.loadExecution(executionWrapper);
    if (execId != null) {
      log.info("execId: {}", execId);
      ExecutionStatus executionStatus = rcService.getExecutionStatus(execId);
      ExecutionResponse executionResponse = new ExecutionResponse();
      executionResponse.setStatus(executionStatus);
      executionResponse.setExecutionId(execId);
      return executionResponse;
    }
    throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Experiment Execution Loading Failed"
    );
  }

  @GetMapping(value = "/execution/{executionId}")
  public @ResponseBody
  ExecutionStatusResponse executionStatus(@PathVariable String executionId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Return current status.
     */
    log.info("Calling GET /execution/{}", executionId);
    ExecutionStatus executionStatus = rcService.getExecutionStatus(executionId);
    if (executionStatus != null) {
      ExecutionStatusResponse executionStatusResponse = new ExecutionStatusResponse();
      executionStatusResponse.setStatus(executionStatus);
      return executionStatusResponse;
    }
    throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Execution Not found"
    );
  }

  @PostMapping(value = "/execution/{executionId}/start")
  public @ResponseBody
  ExecutionStatusResponse executionStart(@PathVariable String executionId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set RUNNING status.
     * - Execute configStart in another thread, so that the EEM is not waiting until we finish. After finishing, set COMPLETED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling POST /execution/{}/start", executionId);
    rcService.startExecution(executionId);
    ExecutionStatus executionStatus = rcService.getExecutionStatus(executionId);
    ExecutionStatusResponse executionStatusResponse = new ExecutionStatusResponse();
    executionStatusResponse.setStatus(executionStatus);
    return executionStatusResponse;
  }

  @DeleteMapping(value = "/execution/{executionId}/abort")
  public @ResponseBody
  ExecutionStatusResponse executionAbort(@PathVariable String executionId) {

    /*
     * Workflow:
     * - Obtain entity using the configId
     * - Set ABORTING status.
     * - Execute the commands needed in another thread, so that the EEM is not waiting until we finish. After finishing, set ABORTED status.
     * - If exception - FAILED status.
     * - Return status.
     */
    log.info("Calling DELETE /execution/{}/abort", executionId);
    rcService.abortExecution(executionId);
    ExecutionStatus executionStatus = rcService.getExecutionStatus(executionId);
    ExecutionStatusResponse executionStatusResponse = new ExecutionStatusResponse();
    executionStatusResponse.setStatus(executionStatus);
    return executionStatusResponse;
  }

}
