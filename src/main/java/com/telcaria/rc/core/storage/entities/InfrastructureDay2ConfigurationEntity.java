package com.telcaria.rc.core.storage.entities;

import com.telcaria.rc.nbi.enums.Day2ConfigurationStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;



@Entity
@Data
public class InfrastructureDay2ConfigurationEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  String configId;
  @Enumerated
  Day2ConfigurationStatus state;

  //ArrayList<String> configurationScripts = new ArrayList<String>();

  //ArrayList<String> stopConfigScripts = new ArrayList<String>();
  @Column(columnDefinition = "text")
  String configurationScripts;
  @Column(columnDefinition = "text")
  String stopConfigScripts;

}
