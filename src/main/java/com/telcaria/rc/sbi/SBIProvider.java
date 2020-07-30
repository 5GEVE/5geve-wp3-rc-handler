package com.telcaria.rc.sbi;

import com.telcaria.rc.core.wrappers.ApplicationDay2ConfigurationEntityWrapper;
import com.telcaria.rc.core.wrappers.InfrastructureDay2ConfigurationEntityWrapper;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;

public interface SBIProvider {

  void start(ExecutionWrapper execution);

  void abort(ExecutionWrapper execution);

  void start(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration);

  void stop(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration);

  void abort(ApplicationDay2ConfigurationEntityWrapper applicationDay2Configuration);

  void start(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration);

  void stop(
      InfrastructureDay2ConfigurationEntityWrapper infrastructureDay2Configuration);
}
