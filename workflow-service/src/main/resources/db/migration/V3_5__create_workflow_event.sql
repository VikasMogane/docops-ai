CREATE TABLE workflow_event (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_workflow
        FOREIGN KEY (workflow_instance_id)
        REFERENCES workflow_instance(id)
);


CREATE INDEX idx_workflow_event_type
ON workflow_event(event_type);
