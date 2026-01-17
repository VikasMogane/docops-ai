package com.docops.workflow.util;

public final class IdempotencyKeyGenerator {

    private IdempotencyKeyGenerator() {}

    public static String generate(
            Long workflowInstanceId,
            String stepName,
            int retryCount
    ) {
        return workflowInstanceId + ":" + stepName + ":" + retryCount;
    }
}