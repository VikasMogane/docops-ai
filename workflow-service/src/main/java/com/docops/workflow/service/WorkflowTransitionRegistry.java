package com.docops.workflow.service;

import com.docops.workflow.domain.enums.WorkflowStep;
import com.docops.workflow.domain.model.TransitionRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class WorkflowTransitionRegistry {

    private final Map<WorkflowStep, List<TransitionRule>> rules;

    public WorkflowTransitionRegistry() {
        this.rules = Map.of(
                WorkflowStep.UPLOAD, List.of(
                        new TransitionRule(WorkflowStep.UPLOAD, "DOCUMENT_UPLOADED", WorkflowStep.TEXT_EXTRACTION)
                ),
                WorkflowStep.TEXT_EXTRACTION, List.of(
                        new TransitionRule(WorkflowStep.TEXT_EXTRACTION, "TEXT_EXTRACTION_COMPLETED", WorkflowStep.AI_SUMMARY)
                ),
                WorkflowStep.AI_SUMMARY, List.of(
                        new TransitionRule(WorkflowStep.AI_SUMMARY, "AI_SUMMARY_COMPLETED", WorkflowStep.HUMAN_REVIEW)
                ),
                WorkflowStep.HUMAN_REVIEW, List.of(
                        new TransitionRule(WorkflowStep.HUMAN_REVIEW, "REVIEW_SUBMITTED", WorkflowStep.APPROVAL)
                ),
                WorkflowStep.APPROVAL, List.of(
                        new TransitionRule(WorkflowStep.APPROVAL, "APPROVED", WorkflowStep.INDEXING)
                ),
                WorkflowStep.INDEXING, List.of(
                        new TransitionRule(WorkflowStep.INDEXING, "INDEXING_DONE", WorkflowStep.COMPLETED)
                )
        );
    }

    public WorkflowStep resolveNextStep(WorkflowStep current, String event) {
        return Optional.ofNullable(rules.get(current))
                .orElseThrow(() -> new IllegalStateException("No transitions defined for " + current))
                .stream()
                .filter(rule -> rule.event().equals(event))
                .findFirst()
                .map(TransitionRule::to)
                .orElseThrow(() ->
                        new IllegalStateException("Invalid event '" + event + "' for step " + current)
                );
    }
}