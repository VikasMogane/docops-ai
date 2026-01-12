package com.docops.workflow.domain.entity;

import com.docops.workflow.domain.enums.WorkflowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_instance")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;

    private String currentStep;

    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    private Long assignedTo;

    //private LocalDateTime createdAt = LocalDateTime.now();
    //private LocalDateTime updatedAt = LocalDateTime.now();
}
