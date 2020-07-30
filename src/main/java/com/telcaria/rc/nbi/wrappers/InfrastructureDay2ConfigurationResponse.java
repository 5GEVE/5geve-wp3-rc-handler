package com.telcaria.rc.nbi.wrappers;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import lombok.Data;

@Data
public class InfrastructureDay2ConfigurationResponse {

  private Day2ConfigurationStatus status;
  private String configurationId;

}
