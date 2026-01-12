package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowStepExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowStepExecutionRepository
        extends JpaRepository<WorkflowStepExecution, Long> {

    List<WorkflowStepExecution> findByWorkflowInstanceId(Long workflowInstanceId);
}
