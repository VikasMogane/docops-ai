package com.docops.workflow.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FailureMetrics {

    private final Counter stepRetryCounter;
    private final Counter dlqCounter;

    public FailureMetrics(MeterRegistry registry) {
        this.stepRetryCounter = Counter.builder("workflow_step_retry_total")
                .description("Total workflow step retries")
                .register(registry);

        this.dlqCounter = Counter.builder("workflow_dlq_total")
                .description("Total workflow steps sent to DLQ")
                .register(registry);
    }

    public void incrementRetry() {
        stepRetryCounter.increment();
    }

    public void incrementDlq() {
        dlqCounter.increment();
    }
}