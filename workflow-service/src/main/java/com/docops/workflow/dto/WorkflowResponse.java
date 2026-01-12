package com.docops.workflow.dto;

import com.docops.workflow.domain.enums.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkflowResponse {
    private Long documentId;
    private String currentStep;
    private WorkflowStatus status;
}