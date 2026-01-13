package com.docops.workflow.domain.entity;

import com.docops.workflow.domain.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_step_execution")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStepExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", nullable = false)
    private WorkflowInstance workflowInstance;

    @Column(nullable = false)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepStatus status;

   // @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;

    private String errorMessage;
    
    @Column(nullable = false)
    private int retryCount;

}
