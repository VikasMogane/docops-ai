package com.docops.workflow.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "workflow_outbox")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // WORKFLOW
    private Long aggregateId;     // workflowInstanceId
    private String eventType;     // STEP_READY

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    private boolean published = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}