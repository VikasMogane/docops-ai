package com.docops.workflow.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_event")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance workflowInstance;

    private String eventType;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private LocalDateTime createdAt = LocalDateTime.now();
}
