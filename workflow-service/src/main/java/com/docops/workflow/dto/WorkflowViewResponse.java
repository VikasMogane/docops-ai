package com.docops.workflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WorkflowViewResponse(
        Long documentId,
        String currentStep,
        String workflowStatus,
        List<StepView> steps
) {

    public record StepView(
            String stepName,
            String status,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Integer retryCount,
            String errorMessage
    ) {}
}