package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowInstanceRepository
extends JpaRepository<WorkflowInstance, Long> {

Optional<WorkflowInstance>
findTopByDocumentIdOrderByIdDesc(Long documentId);
}
