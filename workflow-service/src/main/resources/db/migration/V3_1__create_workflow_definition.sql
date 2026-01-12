CREATE TABLE workflow_definition (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    version VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_workflow_def UNIQUE (name, version)
);
