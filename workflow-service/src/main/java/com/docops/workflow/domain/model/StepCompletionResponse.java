package com.docops.workflow.domain.model;

import java.time.LocalDateTime;

import com.docops.workflow.domain.enums.StepStatus;

public record StepCompletionResponse(
        Long documentId,
        String completedStep,
        StepStatus status,
        LocalDateTime completedAt,
        String workflowStatus
) {}