package com.docops.workflow.domain.model;

import com.docops.workflow.domain.enums.WorkflowStep;

public record TransitionRule(
        WorkflowStep from,
        String event,
        WorkflowStep to
) {}
