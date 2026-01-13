package com.docops.workflow.controller;

import com.docops.workflow.dto.AdvanceWorkflowRequest;
import com.docops.workflow.dto.WorkflowResponse;
import com.docops.workflow.dto.WorkflowViewResponse;
import com.docops.workflow.domain.entity.WorkflowInstance;
import com.docops.workflow.domain.model.StepCompletionResponse;
import com.docops.workflow.service.WorkflowOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowOrchestratorService orchestrator;

    @PostMapping("/{documentId}/retry")
    public WorkflowResponse retry(@PathVariable Long documentId) {

        WorkflowInstance instance = orchestrator.retry(documentId);
        return WorkflowResponse.from(instance);
    }
    @PostMapping("/{documentId}/complete")
    public StepCompletionResponse complete(@PathVariable Long documentId) {
        return orchestrator.markStepSuccess(documentId);
    }

    
    @PostMapping("/{documentId}/advance")
    public WorkflowResponse advance(
            @PathVariable Long documentId,
            @RequestBody AdvanceWorkflowRequest request) {

        WorkflowInstance instance =
                orchestrator.advance(documentId, request.getEvent());

        return new WorkflowResponse(
                instance.getDocumentId(),
                instance.getCurrentStep(),
                instance.getStatus()
        );
    }
    
    // Get Request
    @GetMapping("/{documentId}")
    public WorkflowViewResponse get(@PathVariable Long documentId) {
        return orchestrator.getWorkflow(documentId);
    }
    
   // Post Request
    @PostMapping("/{documentId}")
    public WorkflowInstance create(@PathVariable Long documentId) {
        return orchestrator.createWorkflow(documentId);
    }
    
    

}
