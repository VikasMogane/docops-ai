package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowStepExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import com.docops.workflow.domain.enums.StepStatus;
import java.util.List;
import java.util.Optional;

public interface WorkflowStepExecutionRepository
        extends JpaRepository<WorkflowStepExecution, Long> {

    Optional<WorkflowStepExecution>
    findTopByWorkflowInstanceIdOrderByIdDesc(Long workflowInstanceId);
    
    Optional<WorkflowStepExecution>
    findTopByWorkflowInstanceIdAndStatusOrderByIdDesc(
            Long workflowInstanceId,
            StepStatus status
    );
}


