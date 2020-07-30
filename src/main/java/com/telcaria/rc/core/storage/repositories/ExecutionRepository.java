package com.telcaria.rc.core.storage.repositories;

import com.telcaria.rc.core.storage.entities.ExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRepository extends JpaRepository<ExecutionEntity, String> {


}
