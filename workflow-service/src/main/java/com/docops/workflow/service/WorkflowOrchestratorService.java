package com.docops.workflow.service;

import com.docops.workflow.domain.entity.*;
import com.docops.workflow.domain.enums.*;
import com.docops.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowOrchestratorService {

    private final WorkflowInstanceRepository instanceRepo;
    private final WorkflowStepExecutionRepository stepRepo;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;

    @Transactional
    public WorkflowInstance advance(Long documentId, String event) {

        WorkflowInstance instance = instanceRepo.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowStep nextStep = resolveNextStep(instance.getCurrentStep(), event);

        WorkflowStepExecution execution = WorkflowStepExecution.builder()
                .workflowInstance(instance)
                .stepName(nextStep.name())
                .status(StepStatus.PENDING)
                .build();

        stepRepo.save(execution);

        instance.setCurrentStep(nextStep.name());
        instance.setStatus(
                nextStep == WorkflowStep.HUMAN_REVIEW
                        ? WorkflowStatus.WAITING
                        : WorkflowStatus.RUNNING
        );

        return instanceRepo.save(instance);
    }

    private WorkflowStep resolveNextStep(String current, String event) {
        if ("UPLOAD".equals(current)) return WorkflowStep.TEXT_EXTRACTION;
        if ("TEXT_EXTRACTION".equals(current)) return WorkflowStep.AI_SUMMARY;
        if ("AI_SUMMARY".equals(current)) return WorkflowStep.HUMAN_REVIEW;
        if ("HUMAN_REVIEW".equals(current)) return WorkflowStep.APPROVAL;
        if ("APPROVAL".equals(current)) return WorkflowStep.INDEXING;
        if ("INDEXING".equals(current)) return WorkflowStep.COMPLETED;
        throw new IllegalStateException("Invalid transition");
    }
    
    
    
    
    @Transactional
    public WorkflowInstance createWorkflow(Long documentId) {

        // Fetch workflow definition (hardcoded for v1)
        WorkflowDefinition definition = workflowDefinitionRepository
                .findByNameAndVersion("DOCOPS_AI", "v1")
                .orElseThrow(() -> new RuntimeException("Workflow definition not found"));

        WorkflowInstance instance = WorkflowInstance.builder()
                .documentId(documentId)
                .workflowDefinition(definition)
                .currentStep(WorkflowStep.UPLOAD.name())
                .status(WorkflowStatus.RUNNING)
                .build();

        WorkflowInstance saved = instanceRepo.save(instance);

        WorkflowStepExecution firstStep = WorkflowStepExecution.builder()
                .workflowInstance(saved)
                .stepName(WorkflowStep.UPLOAD.name())
                .status(StepStatus.SUCCESS)
                .build();

        stepRepo.save(firstStep);

        return saved;
    }

}
