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

    // ================= CREATE =================

    @Transactional
    public WorkflowInstance createWorkflow(Long documentId) {

        instanceRepo.findTopByDocumentIdOrderByIdDesc(documentId)
                .ifPresent(existing -> {
                    if (existing.getStatus() != WorkflowStatus.COMPLETED) {
                        throw new IllegalStateException(
                            "Active workflow already exists for document " + documentId
                        );
                    }
                });

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

        stepRepo.save(
                WorkflowStepExecution.builder()
                        .workflowInstance(saved)
                        .stepName(WorkflowStep.UPLOAD.name())
                        .status(StepStatus.SUCCESS)
                        .build()
        );

        return saved;
    }


    // ================= ADVANCE =================

    @Transactional
    public WorkflowInstance advance(Long documentId, String event) {

    	WorkflowInstance instance = instanceRepo
    	        .findTopByDocumentIdOrderByIdDesc(documentId)
    	        .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowStep current = WorkflowStep.valueOf(instance.getCurrentStep());

        // 🚫 TERMINAL GUARD
        if (current == WorkflowStep.COMPLETED) {
            throw new IllegalStateException("Workflow already completed");
        }

        // Mark previous step SUCCESS (idempotent)
        stepRepo.findTopByWorkflowInstanceIdOrderByIdDesc(instance.getId())
                .ifPresent(prev -> {
                    if (prev.getStatus() == StepStatus.PENDING) {
                        prev.setStatus(StepStatus.SUCCESS);
                        prev.setCompletedAt(java.time.LocalDateTime.now());
                        stepRepo.save(prev);
                    }
                });

        WorkflowStep next = resolveNextStep(current, event);

        // Insert ONLY ONE next step
        stepRepo.save(
                WorkflowStepExecution.builder()
                        .workflowInstance(instance)
                        .stepName(next.name())
                        .status(StepStatus.PENDING)
                        .build()
        );

        // Update instance state
        instance.setCurrentStep(next.name());

        if (next == WorkflowStep.HUMAN_REVIEW) {
            instance.setStatus(WorkflowStatus.WAITING);
        } else if (next == WorkflowStep.COMPLETED) {
            instance.setStatus(WorkflowStatus.COMPLETED);
        } else {
            instance.setStatus(WorkflowStatus.RUNNING);
        }

        return instanceRepo.save(instance);
    }

    // ================= TRANSITIONS =================

    private WorkflowStep resolveNextStep(WorkflowStep current, String event) {

        return switch (current) {
            case UPLOAD -> WorkflowStep.TEXT_EXTRACTION;
            case TEXT_EXTRACTION -> WorkflowStep.AI_SUMMARY;
            case AI_SUMMARY -> WorkflowStep.HUMAN_REVIEW;
            case HUMAN_REVIEW -> WorkflowStep.APPROVAL;
            case APPROVAL -> WorkflowStep.INDEXING;
            case INDEXING -> WorkflowStep.COMPLETED;
            default -> throw new IllegalStateException("Invalid transition from " + current);
        };
    }
}
