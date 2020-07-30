package com.telcaria.rc.core.storage.entities;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class ApplicationDay2ConfigurationEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  String configId;
  @Enumerated
  Day2ConfigurationStatus state;
  @Column(columnDefinition = "text")
  String configurationScript;
  @Column(columnDefinition = "text")
  String resetConfigScript;

}
