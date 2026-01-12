package com.docops.workflow.dto;

import lombok.Data;

@Data
public class AdvanceWorkflowRequest {
    private String event;
    private String actor;
}
