package com.docops.workflow.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.docops.workflow.outbox.WorkflowOutboxEvent;
import com.docops.workflow.outbox.WorkflowOutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkflowEventPublisher {

    private final WorkflowOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    private static final String TOPIC = "workflow.step.ready";

    //private final KafkaTemplate<String, String> kafkaTemplate;

//    public WorkflowEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//    public void publishRaw(
//            String topic,
//            String payload
//    ) {
//        kafkaTemplate.send(topic, payload);
//    }
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
        WorkflowOutboxEvent event =
                WorkflowOutboxEvent.builder()
                        .aggregateType("WORKFLOW")
                        .aggregateId(workflowInstanceId)   // ✅ FIXED
                        .eventType("STEP_READY")
                        .payload(message)
                        .build();

        outboxRepository.save(event);

    /*    kafkaTemplate.send(
                TOPIC,
                documentId.toString(),
                message
        ); */

        System.out.println("📤 Kafka event published: " + message); 
    }
    // ===== CALLED BY OUTBOX PUBLISHER =====
    public void publishRaw(String topic, String payload) {
        kafkaTemplate.send(topic, payload);
    }
}