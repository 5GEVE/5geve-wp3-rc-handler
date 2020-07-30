package com.telcaria.rc.core.wrappers;


import lombok.Data;

@Data
public class DataShipper {

  int id;
  String dataShipperId;
  String ipAddress;
  String username;
  String password;//*]
  String configurationScript;
  String stopConfigScript;
  String metricType;

}
