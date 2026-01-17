package com.docops.workflow.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_comment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance workflowInstance;

    private Long userId;

    private String comment;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
