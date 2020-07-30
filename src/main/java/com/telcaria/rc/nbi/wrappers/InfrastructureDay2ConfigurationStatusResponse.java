package com.telcaria.rc.nbi.wrappers;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import lombok.Data;

@Data
public class InfrastructureDay2ConfigurationStatusResponse {

  private Day2ConfigurationStatus status;

}
