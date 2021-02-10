package com.telcaria.rc.core.wrappers.msno;

import lombok.Data;

@Data
public class VnfInstance {

  String id;
  InstantiatedVnfInfo instantiatedVnfInfo;
  String instantiationState;
  String vimId;
  String vnfPkgId;
  String vnfProductName;
  String vnfProvider;
  String vnfSoftwareVersion;
  String vnfdId;
  String vnfdVersion;
}
