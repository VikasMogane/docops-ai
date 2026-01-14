package com.docops.worker.listener;

import com.docops.worker.client.WorkflowCommandClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StepReadyListener {

    private final WorkflowCommandClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public StepReadyListener(WorkflowCommandClient client) {
        this.client = client;
    }

    @KafkaListener(
            topics = "workflow.step.ready",
            groupId = "docops-workers"
    )
    public void onMessage(String message) throws Exception {

        JsonNode event = mapper.readTree(message);

        Long documentId = event.get("documentId").asLong();
        String step = event.get("step").asText();
        int retryCount = event.get("retryCount").asInt();

        System.out.println("⚙️ Worker executing step " + step + " for doc " + documentId);

        if ("HUMAN_REVIEW".equals(step)) {
            System.out.println("⏸ HUMAN_REVIEW — waiting for human");
            return;
        }

        try {
            simulateWork(step, retryCount);

            client.complete(documentId);

            client.advance(documentId, eventFor(step));

            System.out.println("✅ Step completed and advanced: " + step);

        } catch (Exception ex) {

            System.out.println("❌ Step failed: " + ex.getMessage());

            client.fail(documentId, ex.getMessage());
        }
    }

    private void simulateWork(String step, int retryCount) throws InterruptedException {

        switch (step) {

            case "TEXT_EXTRACTION" -> Thread.sleep(2000);

            case "AI_SUMMARY" -> {
                if (retryCount == 0) {
                    throw new RuntimeException("Simulated AI failure");
                }
                Thread.sleep(3000);
            }

            case "INDEXING" -> Thread.sleep(1000);

            default -> Thread.sleep(500);
        }
    }

    private String eventFor(String step) {
        return switch (step) {
            case "TEXT_EXTRACTION" -> "TEXT_EXTRACTION_COMPLETED";
            case "AI_SUMMARY" -> "AI_SUMMARY_COMPLETED";
            case "HUMAN_REVIEW" -> "REVIEW_SUBMITTED";
            case "APPROVAL" -> "APPROVAL_GRANTED";
            case "INDEXING" -> "INDEXING_COMPLETED";
            default -> throw new IllegalStateException("No event mapping for " + step);
        };
    }
}