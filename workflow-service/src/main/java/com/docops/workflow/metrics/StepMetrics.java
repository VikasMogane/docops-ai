package com.docops.workflow.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import com.docops.workflow.context.TenantContextHolder;

@Component
public class StepMetrics {

    private final MeterRegistry registry;

    public StepMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public Timer.Sample stepStarted(String step) {
       // registry.counter("workflow_step_started_total", "tenant", tenant()).increment(step);
        registry.counter( "workflow_step_started_total", "step", step,  "tenant", tenant()).increment();
        return Timer.start(registry);
    }

    public void stepSucceeded(String step, Timer.Sample sample) {
      //  registry.counter("workflow_step_success_total", "step", step).increment();
        registry.counter( "workflow_step_success_total", "step", step,  "tenant", tenant()).increment();

        stopTimer(step, sample);
    }

    public void stepFailed(String step, Timer.Sample sample) {
       // registry.counter("workflow_step_failed_total", "step", step).increment();
        registry.counter( "workflow_step_failed_total", "step", step,  "tenant", tenant()).increment();

        stopTimer(step, sample);
    }

    public void stepRetried(String step) {
      //  registry.counter("workflow_step_retry_total", "step", step).increment();
        registry.counter( "workflow_step_retry_total", "step", step,  "tenant", tenant()).increment();

    }

    public void stepWaiting(String step) {
        registry.gauge("workflow_waiting_total", 
            Tags.of("step", step), 1);
    }

    private void stopTimer(String step, Timer.Sample sample) {
        sample.stop(
            Timer.builder("workflow_step_duration_seconds")
                .tag("step", step)
                .publishPercentileHistogram()
                .register(registry)
        );
    }
    
    private String tenant() {
        return TenantContextHolder.getTenant() != null
                ? TenantContextHolder.getTenant()
                : "default";
    }
}