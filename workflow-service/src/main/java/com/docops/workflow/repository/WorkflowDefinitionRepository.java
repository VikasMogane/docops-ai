package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowDefinitionRepository
        extends JpaRepository<WorkflowDefinition, Long> {

    Optional<WorkflowDefinition> findByNameAndVersion(String name, String version);
}
