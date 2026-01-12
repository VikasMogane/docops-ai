package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowEventRepository
        extends JpaRepository<WorkflowEvent, Long> {
}
