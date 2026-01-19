CREATE TABLE workflow_step_execution (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL,
    step_name VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, RUNNING, SUCCESS, FAILED
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,
    error_message TEXT,

    CONSTRAINT fk_workflow_instance
        FOREIGN KEY (workflow_instance_id)
        REFERENCES workflow_instance(id)
);


CREATE INDEX idx_step_execution_instance
ON workflow_step_execution(workflow_instance_id);

CREATE INDEX idx_step_execution_step
ON workflow_step_execution(step_name);
