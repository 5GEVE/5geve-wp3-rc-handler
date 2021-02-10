package com.telcaria.rc.core.wrappers.msno;


import lombok.Data;
import java.util.List;

@Data
public class IpAddresses {

  List<String> addresses;
  String type;
}
