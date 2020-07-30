package com.telcaria.rc.nbi.wrappers;

import lombok.Data;

@Data
public class InfrastructureMetricWrapper {

  // Data provided by the ELM
  private String metricId;    // metricId to be also included as infrastructureParameters in the TCB
  private String metricType;
  private String topic;
  private String site;        // To obtain the Kafka IP address from the IWL site inventory
  private String unit;
  private String interval;
  private String deviceId;    // Optional, to be also included as infrastructureParameters in the TCB
}

