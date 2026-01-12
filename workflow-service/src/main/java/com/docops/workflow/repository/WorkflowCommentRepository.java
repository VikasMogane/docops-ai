package com.docops.workflow.repository;

import com.docops.workflow.domain.entity.WorkflowComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowCommentRepository
        extends JpaRepository<WorkflowComment, Long> {
}
