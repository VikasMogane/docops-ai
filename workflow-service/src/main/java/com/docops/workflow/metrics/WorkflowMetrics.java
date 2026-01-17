package com.docops.workflow.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import com.docops.workflow.context.TenantContextHolder;

@Component
public class WorkflowMetrics {

    private final MeterRegistry registry;

    public WorkflowMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    
    public void workflowStarted() {
    	  registry.counter(
    		        "workflow_started_total",
    		        "tenant", tenant()
    		    ).increment();
    }

    public void workflowFailed() {
    	  registry.counter(
    		        "workflow_failed_total",
    		        "tenant", tenant()
    		    ).increment();
    }
    
   /* public void workflowStarted() {
        registry.counter("workflow_started_total").increment();
    }
    public void workflowFailed() {
        registry.counter("workflow_failed_total").increment();
    } */


    public void workflowCompleted() {
        registry.counter("workflow_completed_total").increment();
    }

 
    public Timer.Sample startWorkflowTimer() {
        return Timer.start(registry);
    }

    public void stopWorkflowTimer(Timer.Sample sample) {
        sample.stop(
            Timer.builder("workflow_duration_seconds")
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