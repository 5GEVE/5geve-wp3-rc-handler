package com.telcaria.rc.core.wrappers;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import lombok.Data;

@Data
public class ApplicationDay2ConfigurationEntityWrapper {

  String configId;
  Day2ConfigurationStatus state;
  String configurationScript;
  String resetConfigScript;

}
