package com.docops.workflow.domain.enums;

public enum WorkflowStep {
    UPLOAD,
    TEXT_EXTRACTION,
    AI_SUMMARY,
    HUMAN_REVIEW,
    APPROVAL,
    INDEXING,
    COMPLETED
}
