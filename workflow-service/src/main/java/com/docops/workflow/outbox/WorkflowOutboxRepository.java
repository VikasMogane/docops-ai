package com.docops.workflow.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowOutboxRepository
        extends JpaRepository<WorkflowOutboxEvent, Long> {

    List<WorkflowOutboxEvent> findTop50ByPublishedFalseOrderByCreatedAtAsc();
}