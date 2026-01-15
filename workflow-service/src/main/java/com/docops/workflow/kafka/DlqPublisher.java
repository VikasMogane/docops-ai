package com.docops.workflow.kafka;

import com.docops.workflow.domain.entity.WorkflowStepExecution;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DlqPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void publish(WorkflowStepExecution step) {

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("documentId", step.getWorkflowInstance().getDocumentId());
            payload.put("workflowInstanceId", step.getWorkflowInstance().getId());
            payload.put("step", step.getStepName());
            payload.put("retryCount", step.getRetryCount());
            payload.put("status", "FINAL_FAILED");

            String message = mapper.writeValueAsString(payload);

            kafkaTemplate.send(
                    "workflow.step.dlq",
                    step.getWorkflowInstance().getDocumentId().toString(),
                    message
            );

            System.out.println("☠️ DLQ published: " + message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish DLQ message", e);
        }
    }
}