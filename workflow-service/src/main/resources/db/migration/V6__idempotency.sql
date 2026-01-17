ALTER TABLE workflow_step_execution
ADD COLUMN idempotency_key VARCHAR(100);

CREATE UNIQUE INDEX uq_step_idempotency
ON workflow_step_execution(idempotency_key);