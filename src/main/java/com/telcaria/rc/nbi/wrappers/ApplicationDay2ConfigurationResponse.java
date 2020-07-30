package com.telcaria.rc.nbi.wrappers;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import lombok.Data;

@Data
public class ApplicationDay2ConfigurationResponse {

  private String configurationId;
  private Day2ConfigurationStatus status;

}
