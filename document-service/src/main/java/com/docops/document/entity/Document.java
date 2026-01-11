package com.docops.document.entity;

import com.docops.document.workflow.DocumentStatus;
import com.docops.document.workflow.DocumentWorkflow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orgId;
    private Long uploadedBy;

    private String fileName;
    private String contentType;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private Instant createdAt;

    public void transition(DocumentStatus next) {
        if (!DocumentWorkflow.allowed(this.status, next)) {
            throw new IllegalStateException(
                "Invalid transition " + status + " -> " + next
            );
        }
        this.status = next;
    }

//    // getters & setters
//    public Long getId() { return id; }
//    public Long getOrgId() { return orgId; }
//    public void setOrgId(Long orgId) { this.orgId = orgId; }
//    public Long getUploadedBy() { return uploadedBy; }
//    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
//    public String getFileName() { return fileName; }
//    public void setFileName(String fileName) { this.fileName = fileName; }
//    public String getContentType() { return contentType; }
//    public void setContentType(String contentType) { this.contentType = contentType; }
//    public DocumentStatus getStatus() { return status; }
//    public void setStatus(DocumentStatus status) { this.status = status; }
//    public Instant getCreatedAt() { return createdAt; }
//    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
