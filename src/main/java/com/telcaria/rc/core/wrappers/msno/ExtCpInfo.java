package com.telcaria.rc.core.wrappers.msno;


import lombok.Data;
import java.util.List;

@Data
public class ExtCpInfo {

  List<CpProtocolInfo> cpProtocolInfo;
  String cpdId;
  String id;
}
