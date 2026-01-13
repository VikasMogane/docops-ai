package com.docops.workflow.dto;

import com.docops.workflow.domain.entity.WorkflowInstance;
import com.docops.workflow.domain.enums.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowResponse {
    private Long documentId;
    private String currentStep;
    private WorkflowStatus status;
    
    public static WorkflowResponse from(WorkflowInstance instance) {
        return new WorkflowResponse(
                instance.getDocumentId(),
                instance.getCurrentStep(),
                instance.getStatus()
        );
    }
}