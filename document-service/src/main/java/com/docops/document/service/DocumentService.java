package com.docops.document.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.docops.document.entity.Document;
import com.docops.document.repository.DocumentRepository;
import com.docops.document.workflow.DocumentStatus;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public Document create(Long orgId, Long userId,
                           String name, String type) {

        Document doc = new Document();
        doc.setOrgId(orgId);
        doc.setUploadedBy(userId);
        doc.setFileName(name);
        doc.setContentType(type);
        doc.setStatus(DocumentStatus.UPLOADED);
        doc.setCreatedAt(Instant.now());

        return repository.save(doc);
    }
}
