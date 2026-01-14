package com.docops.workflow.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEventPublisher {

    private static final String TOPIC = "workflow.step.ready";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public WorkflowEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStepReady(
            Long documentId,
            Long workflowInstanceId,
            String step,
            int retryCount
    ) {

        String message = """
            {
              "documentId": %d,
              "workflowInstanceId": %d,
              "step": "%s",
              "retryCount": %d
            }
            """.formatted(
                documentId,
                workflowInstanceId,
                step,
                retryCount
            );

        kafkaTemplate.send(
                TOPIC,
                documentId.toString(),
                message
        );

        System.out.println("📤 Kafka event published: " + message);
    }
}