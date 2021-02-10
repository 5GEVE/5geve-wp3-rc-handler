package com.telcaria.rc.core.wrappers.msno;


import lombok.Data;
import java.util.List;

@Data
public class IpOverEthernet {

  AddressRange addressRange;
  List<IpAddresses> ipAddresses;
  String macAddress;
}
