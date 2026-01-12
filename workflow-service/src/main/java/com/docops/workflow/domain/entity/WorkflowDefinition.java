package com.docops.workflow.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_definition",
       uniqueConstraints = @UniqueConstraint(columnNames = {"name", "version"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();
}
