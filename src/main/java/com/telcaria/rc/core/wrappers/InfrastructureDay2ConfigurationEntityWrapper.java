package com.telcaria.rc.core.wrappers;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import java.util.List;
import lombok.Data;

@Data
public class InfrastructureDay2ConfigurationEntityWrapper {
  String configId;
  Day2ConfigurationStatus state;
  //List<String> configurationScripts;
  //List<String> stopConfigScripts;
  String configurationScripts;
  String stopConfigScripts;
}
