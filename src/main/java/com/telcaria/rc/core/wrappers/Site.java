package com.telcaria.rc.core.wrappers;


import lombok.Data;

@Data
public class Site {

  int id;
  String name;
  String location;
  String kafkaIpAddress;

}
