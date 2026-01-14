package com.docops.workflow.worker;

import com.docops.workflow.repository.WorkflowStepExecutionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkflowScheduler {

    private final WorkflowStepExecutionRepository stepRepo;
    private final WorkflowWorker worker;

    public WorkflowScheduler(
            WorkflowStepExecutionRepository stepRepo,
            WorkflowWorker worker
    ) {
        this.stepRepo = stepRepo;
        this.worker = worker;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollAndExecute() {
    	 System.out.println("🔁 Scheduler tick");

    	    stepRepo.findRunningSteps()
    	            .forEach(worker::process);
    }
}