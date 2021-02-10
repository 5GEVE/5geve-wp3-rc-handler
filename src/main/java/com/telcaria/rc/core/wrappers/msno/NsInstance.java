package com.telcaria.rc.core.wrappers.msno;

import lombok.Data;
import java.util.List;

@Data
public class NsInstance {

  String id;
  String nsInstanceDescription;
  String nsInstanceName;
  String nsState;
  String nsdId;
  String nsdInfoId;
  List<VnfInstance> vnfInstance;
}
