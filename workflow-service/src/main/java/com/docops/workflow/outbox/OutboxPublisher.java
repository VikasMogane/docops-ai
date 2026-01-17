package com.docops.workflow.outbox;

import com.docops.workflow.messaging.WorkflowEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final WorkflowOutboxRepository repository;
    private final WorkflowEventPublisher kafkaPublisher;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publish() {

    	  repository.findTop50ByPublishedFalseOrderByCreatedAtAsc()
          .forEach(event -> {

        	  kafkaPublisher.publishRaw(
                      "workflow.step.ready",
                      event.getPayload()
              );

              event.setPublished(true);
          });
    }
}