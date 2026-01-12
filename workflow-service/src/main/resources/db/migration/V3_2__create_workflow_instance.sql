CREATE TABLE workflow_instance (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    workflow_definition_id BIGINT NOT NULL,
    current_step VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL, -- RUNNING, WAITING, FAILED, COMPLETED
    assigned_to BIGINT,          -- optional human reviewer
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_workflow_def
        FOREIGN KEY (workflow_definition_id)
        REFERENCES workflow_definition(id)
);


CREATE INDEX idx_workflow_instance_document
ON workflow_instance(document_id);

CREATE INDEX idx_workflow_instance_status
ON workflow_instance(status);