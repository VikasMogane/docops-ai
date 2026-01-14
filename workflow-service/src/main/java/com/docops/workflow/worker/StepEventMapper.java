package com.docops.workflow.worker;

import com.docops.workflow.domain.enums.WorkflowStep;

import java.util.Map;

public class StepEventMapper {

    private static final Map<WorkflowStep, String> MAP = Map.of(
            WorkflowStep.UPLOAD, "DOCUMENT_UPLOADED",
            WorkflowStep.TEXT_EXTRACTION, "TEXT_EXTRACTION_COMPLETED",
            WorkflowStep.AI_SUMMARY, "AI_SUMMARY_COMPLETED",
            WorkflowStep.HUMAN_REVIEW, "REVIEW_SUBMITTED",
            WorkflowStep.APPROVAL, "APPROVAL_GRANTED",
            WorkflowStep.INDEXING, "INDEXING_COMPLETED"
    );

    public static String eventFor(WorkflowStep step) {
        return MAP.get(step);
    }
}