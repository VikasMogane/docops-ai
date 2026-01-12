package com.docops.workflow.controller;

import com.docops.workflow.dto.AdvanceWorkflowRequest;
import com.docops.workflow.domain.entity.WorkflowInstance;
import com.docops.workflow.service.WorkflowOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowOrchestratorService orchestrator;

    @PostMapping("/{documentId}/advance")
    public WorkflowInstance advance(
            @PathVariable Long documentId,
            @RequestBody AdvanceWorkflowRequest request) {
        return orchestrator.advance(documentId, request.getEvent());
    }
    
    @PostMapping("/{documentId}")
    public WorkflowInstance create(@PathVariable Long documentId) {
        return orchestrator.createWorkflow(documentId);
    }

}
