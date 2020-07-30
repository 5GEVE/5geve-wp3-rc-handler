package com.telcaria.rc.core.storage.entities;

import com.telcaria.rc.nbi.enums.ExecutionStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class ExecutionEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  String executionId;

  @Enumerated
  ExecutionStatus state=ExecutionStatus.RUNNING;
  @Column(columnDefinition = "text")
  String execScript;
}
