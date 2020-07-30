package com.telcaria.rc.core.storage;

import com.telcaria.rc.core.storage.entities.ApplicationDay2ConfigurationEntity;
import com.telcaria.rc.core.storage.entities.ExecutionEntity;
import com.telcaria.rc.core.storage.entities.InfrastructureDay2ConfigurationEntity;
import com.telcaria.rc.core.storage.repositories.ApplicationDay2ConfigurationRepository;
import com.telcaria.rc.core.storage.repositories.ExecutionRepository;
import com.telcaria.rc.core.storage.repositories.InfrastructureDay2ConfigurationRepository;
import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.DataShipper;
import com.telcaria.rc.core.wrappers.ExecutionEntityWrapper;
import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StorageServiceImpl implements StorageService{

  private
  ApplicationDay2ConfigurationRepository applicationDay2ConfigurationRepository;

  private
  InfrastructureDay2ConfigurationRepository infrastructureDay2ConfigurationRepository;

  private
  ExecutionRepository executionRepository;

  @Autowired
  public StorageServiceImpl(
      ApplicationDay2ConfigurationRepository applicationDay2ConfigurationRepository,
      InfrastructureDay2ConfigurationRepository infrastructureDay2ConfigurationRepository,
      ExecutionRepository executionRepository) {
    this.applicationDay2ConfigurationRepository = applicationDay2ConfigurationRepository;
    this.infrastructureDay2ConfigurationRepository = infrastructureDay2ConfigurationRepository;
    this.executionRepository = executionRepository;
  }

  @Override
  public ApplicationDay2ConfigurationEntityWrapper getApplicationDay2Configuration(String configId) {
    Optional<ApplicationDay2ConfigurationEntity> optional = applicationDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      ModelMapper modelMapper = new ModelMapper();
      return modelMapper.map(optional.get(), ApplicationDay2ConfigurationEntityWrapper.class);
    }
    return null;
  }

  @Override
  public InfrastructureDay2ConfigurationEntityWrapper getInfrastructureDay2Configuration(
      String configId) {
    Optional<InfrastructureDay2ConfigurationEntity> optional = infrastructureDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      ModelMapper modelMapper = new ModelMapper();
      return modelMapper.map(optional.get(), InfrastructureDay2ConfigurationEntityWrapper.class);
    }
    return null;
  }

  @Override
  public Day2ConfigurationStatus getApplicationDay2ConfigurationStatus(String configId) {
    Optional<ApplicationDay2ConfigurationEntity> optional = applicationDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      return optional.get().getState();
    }
    return null;
  }

  @Override
  public Day2ConfigurationStatus getInfrastructureDay2ConfigurationStatus(String configId) {
    Optional<InfrastructureDay2ConfigurationEntity> optional = infrastructureDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      return optional.get().getState();
    }
    return null;
  }

  @Override
  public String loadApplicationDay2Configuration(
      ApplicationDay2ConfigurationEntityWrapper applicationDay2ConfigurationEntityWrapper) {
    ModelMapper modelMapper = new ModelMapper();
    ApplicationDay2ConfigurationEntity day2ConfigurationEntity = modelMapper
        .map(applicationDay2ConfigurationEntityWrapper, ApplicationDay2ConfigurationEntity.class);
    day2ConfigurationEntity.setState(Day2ConfigurationStatus.INIT);
    day2ConfigurationEntity = applicationDay2ConfigurationRepository.save(day2ConfigurationEntity);
    return day2ConfigurationEntity.getConfigId();
  }

  @Override
  public String loadInfrastructureDay2Configuration(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2ConfigurationEntityWrapper) {
    ModelMapper modelMapper = new ModelMapper();
    InfrastructureDay2ConfigurationEntity day2ConfigurationEntity = modelMapper
        .map(infrastructureDay2ConfigurationEntityWrapper, InfrastructureDay2ConfigurationEntity.class);
    day2ConfigurationEntity.setState(Day2ConfigurationStatus.INIT);
    day2ConfigurationEntity = infrastructureDay2ConfigurationRepository.save(day2ConfigurationEntity);
    return day2ConfigurationEntity.getConfigId();
  }

  @Override
  public void updateApplicationDay2ConfigurationStatus(String configId,
      Day2ConfigurationStatus day2ConfigurationStatus) {
    Optional<ApplicationDay2ConfigurationEntity> optional = applicationDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      ApplicationDay2ConfigurationEntity day2ConfigurationEntity = optional.get();
      day2ConfigurationEntity.setState(day2ConfigurationStatus);
      applicationDay2ConfigurationRepository.save(day2ConfigurationEntity);
    }
  }

  @Override
  public void updateInfrastructureDay2ConfigurationStatus(String configId,
      Day2ConfigurationStatus day2ConfigurationStatus) {
    Optional<InfrastructureDay2ConfigurationEntity> optional = infrastructureDay2ConfigurationRepository.findById(configId);
    if (optional.isPresent()){
      InfrastructureDay2ConfigurationEntity day2ConfigurationEntity = optional.get();
      day2ConfigurationEntity.setState(day2ConfigurationStatus);
      infrastructureDay2ConfigurationRepository.save(day2ConfigurationEntity);
    }
  }

  @Override
  public void deleteApplicationDay2Configuration(String configId) {
    applicationDay2ConfigurationRepository.deleteById(configId);
  }

  @Override
  public void deleteInfrastructureDay2Configuration(String configId) {
    infrastructureDay2ConfigurationRepository.deleteById(configId);
  }

  @Override
  public ExecutionWrapper getExecution(String executionId) {
    Optional<ExecutionEntity> optional = executionRepository.findById(executionId);
    if (optional.isPresent()){
      ModelMapper modelMapper = new ModelMapper();
      return modelMapper.map(optional.get(), ExecutionWrapper.class);
    }
    return null;
  }

  @Override
  public ExecutionStatus getExecutionStatus(String executionId) {
    Optional<ExecutionEntity> optional = executionRepository.findById(executionId);
    if (optional.isPresent()){
      return optional.get().getState();
    }
    return null;
  }

  @Override
  public String loadExecution(ExecutionEntityWrapper executionEntityWrapper) {
    ModelMapper modelMapper = new ModelMapper();
    ExecutionEntity executionEntity = modelMapper
        .map(executionEntityWrapper, ExecutionEntity.class);
    executionEntity.setState(ExecutionStatus.INIT);
    executionEntity = executionRepository.save(executionEntity);
    return executionEntity.getExecutionId();
  }

  @Override
  public void updateExecutionStatus(String executionId, ExecutionStatus executionStatus) {
    Optional<ExecutionEntity> optional = executionRepository.findById(executionId);
    if (optional.isPresent()){
      ExecutionEntity executionEntity = optional.get();
      executionEntity.setState(executionStatus);
      executionRepository.save(executionEntity);
    }
  }

  @Override
  public void deleteExecution(String executionId) {
    executionRepository.deleteById(executionId);
  }


}
