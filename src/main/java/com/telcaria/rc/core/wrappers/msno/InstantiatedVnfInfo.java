package com.telcaria.rc.core.wrappers.msno;


import lombok.Data;
import java.util.List;

@Data
public class InstantiatedVnfInfo {

  List<ExtCpInfo> extCpInfo;
  String flavourId;
  String vnfState;
}
