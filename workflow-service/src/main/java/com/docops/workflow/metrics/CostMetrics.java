package com.docops.workflow.metrics;

import com.docops.workflow.context.TenantContextHolder;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CostMetrics {

    private final MeterRegistry registry;

    public CostMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void workflowCost(double cost) {
    	 registry.counter(
    		        "workflow_cost_dollars_total",
    		        "tenant", tenant()
    		    ).increment(cost);
    }
    
    private String tenant() {
        return TenantContextHolder.getTenant() != null
                ? TenantContextHolder.getTenant()
                : "default";
    }
}