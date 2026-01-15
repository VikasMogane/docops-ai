package com.docops.workflow.worker;

import com.docops.workflow.client.WorkflowCommandClient;
import com.docops.workflow.domain.entity.WorkflowStepExecution;
import com.docops.workflow.domain.enums.WorkflowStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkflowWorker {

    private final WorkflowCommandClient commandClient;

    public WorkflowWorker(WorkflowCommandClient commandClient) {
        this.commandClient = commandClient;
    }

    @Transactional  // 🔥 THIS IS THE KEY FIX
    public void process(WorkflowStepExecution stepExecution) {

        WorkflowStep step =
                WorkflowStep.valueOf(stepExecution.getStepName());

        Long documentId =
                stepExecution.getWorkflowInstance().getDocumentId();

        System.out.println("⚙️ Processing step: " + step + " for doc " + documentId);

        if (step == WorkflowStep.HUMAN_REVIEW) {
            System.out.println("⏸ HUMAN_REVIEW — waiting");
            return;
        }

        try {
            simulateWork(step, stepExecution.getRetryCount());

            System.out.println("✅ Completing step: " + step);
            commandClient.complete(documentId);

            String event = StepEventMapper.eventFor(step);
            System.out.println("➡️ Advancing with event: " + event);
            commandClient.advance(documentId, event);

        } catch (Exception ex) {
            System.out.println("❌ Step failed: " + ex.getMessage());
            commandClient.fail(documentId, ex.getMessage());
        }
    }

    private void simulateWork(WorkflowStep step, int retryCount)
            throws InterruptedException {
    	
    	  if (retryCount > 3) {
    	        throw new RuntimeException("Retry limit exceeded");
    	    }

    	    long backoff = (long) Math.pow(2, retryCount) * 1000;
    	    Thread.sleep(backoff);


        switch (step) {
            case TEXT_EXTRACTION -> Thread.sleep(2000);

            case AI_SUMMARY -> {
                if (retryCount == 0) {
                    throw new RuntimeException("Simulated AI failure");
                }
                Thread.sleep(3000);
            }

            case INDEXING -> Thread.sleep(1000);
            case APPROVAL -> Thread.sleep(1500);
            default -> Thread.sleep(500);
        }
    }
}
