package com.docops.workflow.service;

import com.docops.workflow.domain.entity.*;
import com.docops.workflow.domain.enums.*;
import com.docops.workflow.domain.model.StepCompletionResponse;
import com.docops.workflow.dto.WorkflowViewResponse;
import com.docops.workflow.messaging.WorkflowEventPublisher;
import com.docops.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowOrchestratorService {

    private static final int MAX_RETRIES = 3;

    private final WorkflowInstanceRepository instanceRepo;
    private final WorkflowStepExecutionRepository stepRepo;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowTransitionRegistry transitionRegistry;
    private final WorkflowEventPublisher eventPublisher;


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

        // Initial step is already SUCCESS
        stepRepo.save(
                WorkflowStepExecution.builder()
                        .workflowInstance(saved)
                        .stepName(WorkflowStep.UPLOAD.name())
                        .status(StepStatus.SUCCESS)
                        .retryCount(0)
                        .startedAt(LocalDateTime.now())
                        .completedAt(LocalDateTime.now())
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

        // 🔒 Phase-4 rule:
        // You can advance ONLY if last step is SUCCESS
        WorkflowStepExecution lastStep = stepRepo
                .findTopByWorkflowInstanceIdOrderByIdDesc(instance.getId())
                .orElseThrow(() -> new IllegalStateException("No step found"));

        if (lastStep.getStatus() != StepStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Cannot advance workflow. Step "
                            + lastStep.getStepName()
                            + " is not SUCCESS"
            );
        }

        // Resolve next step via rule engine
        WorkflowStep next = transitionRegistry.resolveNextStep(current, event);

        // Insert next step as RUNNING
        stepRepo.save(
                WorkflowStepExecution.builder()
                        .workflowInstance(instance)
                        .stepName(next.name())
                        .status(StepStatus.RUNNING)
                        .retryCount(0)
                        .startedAt(LocalDateTime.now())
                        .build()
        );
        
        eventPublisher.publishStepReady(
                instance.getDocumentId(),
                instance.getId(),
                next.name(),
                0
        );

        // Update workflow instance
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

    // ================= FAIL STEP =================

    @Transactional
    public void markStepFailed(Long documentId, String error) {

        WorkflowInstance instance = instanceRepo
                .findTopByDocumentIdOrderByIdDesc(documentId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowStepExecution step = stepRepo
                .findTopByWorkflowInstanceIdOrderByIdDesc(instance.getId())
                .orElseThrow(() -> new RuntimeException("No step found"));

        if (step.getStatus() != StepStatus.RUNNING) {
            throw new IllegalStateException(
                    "Only RUNNING step can be marked FAILED"
            );
        }

        step.setStatus(StepStatus.FAILED);
        step.setErrorMessage(error);
        step.setCompletedAt(LocalDateTime.now());
        step.setRetryCount(step.getRetryCount() + 1);

        stepRepo.save(step);

        instance.setStatus(WorkflowStatus.FAILED);
        instanceRepo.save(instance);
    }

    // ================= RETRY =================

    @Transactional
    public WorkflowInstance retry(Long documentId) {

        WorkflowInstance instance = instanceRepo
                .findTopByDocumentIdOrderByIdDesc(documentId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowStepExecution failedStep = stepRepo
                .findTopByWorkflowInstanceIdAndStatusOrderByIdDesc(
                        instance.getId(), StepStatus.FAILED)
                .orElseThrow(() -> new RuntimeException("No failed step to retry"));

        if (failedStep.getRetryCount() >= MAX_RETRIES) {
            throw new IllegalStateException("Retry limit exceeded");
        }

        // Reset step for retry
        failedStep.setStatus(StepStatus.RUNNING);
        failedStep.setErrorMessage(null);
        failedStep.setStartedAt(LocalDateTime.now());

        stepRepo.save(failedStep);

        instance.setStatus(WorkflowStatus.RUNNING);
        instanceRepo.save(instance);
        
     // 🔥 THIS IS THE KEY LINE
        eventPublisher.publishStepReady(
                instance.getDocumentId(),
                instance.getId(),
                failedStep.getStepName(),
                failedStep.getRetryCount()
        );

        return instance;
       // return instanceRepo.save(instance);
    }
    
    @Transactional
    public StepCompletionResponse markStepSuccess(Long documentId) {

        WorkflowInstance instance = instanceRepo
                .findTopByDocumentIdOrderByIdDesc(documentId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowStepExecution step = stepRepo
                .findTopByWorkflowInstanceIdOrderByIdDesc(instance.getId())
                .orElseThrow(() -> new RuntimeException("No step found"));

        if (step.getStatus() != StepStatus.RUNNING) {
            throw new IllegalStateException("Only RUNNING step can be completed");
        }

        step.setStatus(StepStatus.SUCCESS);
        step.setCompletedAt(LocalDateTime.now());

        stepRepo.save(step);
        return new StepCompletionResponse(
                documentId,
                step.getStepName(),
                step.getStatus(),
                step.getCompletedAt(),
                instance.getStatus().name()
        );
    }
    
    @Transactional(readOnly = true)
    public WorkflowViewResponse getWorkflow(Long documentId) {

        WorkflowInstance instance = instanceRepo
                .findTopByDocumentIdOrderByIdDesc(documentId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        List<WorkflowViewResponse.StepView> steps =
                stepRepo.findTopByWorkflowInstanceIdOrderByIdDesc(instance.getId())
                        .stream()
                        .map(s -> new WorkflowViewResponse.StepView(
                                s.getStepName(),
                                s.getStatus().name(),
                                s.getStartedAt(),
                                s.getCompletedAt(),
                                s.getRetryCount(),
                                s.getErrorMessage()
                        ))
                        .toList();

        return new WorkflowViewResponse(
                documentId,
                instance.getCurrentStep(),
                instance.getStatus().name(),
                steps
        );
    }

}
